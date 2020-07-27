package net.eterniamc.scheduler;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import javassist.ClassPool;
import javassist.LoaderClassPath;
import lombok.SneakyThrows;
import net.eterniamc.scheduler.annotation.Delayed;
import net.minecraftforge.common.MinecraftForge;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public enum SchedulerController {
    INSTANCE;

    private static final DynamicClassLoader LOADER = new DynamicClassLoader();

    /**
     * ALL SYNCHRONIZED DATA WILL BE STORED HERE
     * DOES NOT REMOVE DEAD OBJECTS BE SURE TO DISMISS SERVICE WHEN FINISHED
     */
    protected final Set<ScheduledElement> scheduledElements = Sets.newConcurrentHashSet();
    protected final Set<ScheduledElement> asyncScheduledElements = Sets.newConcurrentHashSet();

    protected final Set<DelayedElement> delayedElements = Sets.newConcurrentHashSet();

    protected final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();


    private boolean initialized = false;

    public void initialize() {
        if (initialized) {
            return;
        }
        ClassPool.getDefault().insertClassPath(new LoaderClassPath(LOADER));
        executor.scheduleAtFixedRate(() -> {
            for (ScheduledElement element : asyncScheduledElements) {
                if (element.shouldRun()) {
                    element.run();
                }
            }
        }, 0, 2, TimeUnit.MILLISECONDS);
        MinecraftForge.EVENT_BUS.register(new SchedulerListener());
        initialized = true;
    }

    @SneakyThrows
    @SuppressWarnings("unused")
    public void delay(long delay, boolean async, String className, Object source, String method, Object[] params) {
        Class<?> clazz = Class.forName(className);
        Class<?>[] paramTypes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            paramTypes[i] = param.getClass();
        }
        Method method1 = clazz.getDeclaredMethod(method, paramTypes);
        delay(delay, async, () -> {
            try {
                method1.invoke(source, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void delay(long delay, boolean async, Runnable runnable) {
        if (async) {
            executor.schedule(runnable, delay, TimeUnit.MILLISECONDS);
        } else {
            System.out.println("Scheduled " + runnable + " to run in " + delay + " milliseconds");
            delayedElements.add(new DelayedElement(delay, runnable));
        }
    }

    /**
     * This method must be called before the class is loaded!
     *
     * @param clazz full path of class to register (java.lang.Object)
     */
    @SneakyThrows
    public void processAnnotationsFor(String clazz) {
        ClassImpactor.register(clazz);
    }

    /**
     * Finds and loads all delayed methods in a package
     *
     * @param pkg The package path (java.lang)
     */
    public void processAnnotationsIn(String pkg) {
        Reflections reflections = new Reflections(pkg, LOADER, new MethodAnnotationsScanner(), new SubTypesScanner(false));

        List<Class<? extends Annotation>> processedAnnotations = Lists.newArrayList(Delayed.class);

        processedAnnotations.stream()
                .flatMap(clazz -> reflections.getMethodsAnnotatedWith(clazz).stream())
                .map(Method::getDeclaringClass)
                .map(Class::getCanonicalName)
                .distinct()
                .forEach(this::processAnnotationsFor);
    }

    /**
     * Registers a class as refresh service listener
     *
     * @param source This can be any object you want
     */
    public void registerSynchronizationService(Object source) {
        List<ScheduledElement> elements = ScheduledServiceInspector.parseElementsFrom(source);
        for (ScheduledElement element : elements) {
            if (element.isAsync()) {
                asyncScheduledElements.add(element);
            } else {
                scheduledElements.add(element);
            }
        }
    }

    /**
     * Removes a synchronized service
     *
     * @param source This can be any object you want
     */
    public void dismissSynchronizationService(Object source) {
        scheduledElements.removeIf(element -> element.getSource().equals(source));
        asyncScheduledElements.removeIf(element -> element.getSource().equals(source));
    }
}
