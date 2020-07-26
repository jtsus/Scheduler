package net.eterniamc.scheduler;

import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public enum SchedulerController {
    INSTANCE;

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
        executor.scheduleAtFixedRate(() -> {
            for (ScheduledElement element : asyncScheduledElements) {
                if (element.shouldRun()) {
                    element.run();
                }
            }
        }, 0, 2, TimeUnit.MILLISECONDS);
        MinecraftForge.EVENT_BUS.register(this);
        initialized = true;
    }

    @SneakyThrows
    @SuppressWarnings("unused")
    public void delay(long delay, boolean async, String className, Object source, String method) {
        Class<?> clazz = Class.forName(className);
        Method method1 = clazz.getDeclaredMethod(method);
        delay(delay, async, () -> {
            try {
                method1.invoke(source);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void delay(long delay, boolean async, Runnable runnable) {
        if (async) {
            executor.schedule(runnable, delay, TimeUnit.MILLISECONDS);
        } else {
            delayedElements.add(new DelayedElement(delay, runnable));
        }
    }

    /**
     * This method must be called before the class is loaded!
     *
     * @param clazz full path of class to register (java.lang.Object)
     */
    public void registerDelayedMethods(String clazz) {
        DelayedServiceInspector.register(clazz);
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

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            for (ScheduledElement element : scheduledElements) {
                if (element.shouldRun()) {
                    element.run();
                }
            }
            for (DelayedElement element : delayedElements) {
                if (element.shouldRun()) {
                    element.run();
                    delayedElements.remove(element);
                }
            }
        }
    }
}
