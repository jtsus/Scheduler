package net.eterniamc.scheduler;

import javassist.*;
import lombok.SneakyThrows;
import net.eterniamc.scheduler.annotation.Delayed;

import java.util.ArrayList;
import java.util.List;

public class DelayedServiceInspector {

    @SneakyThrows
    public static void register(String toLoad) {
        CtClass ctClass = ClassPool.getDefault().getCtClass(toLoad);
        for (CtMethod method : getAllMethods(ctClass.getDeclaredMethods())) {
            long rate = getRate(method);
            boolean async = isAsync(method);

            if (rate > 0) {
                String name = method.getName();
                method.setName("__" + name);
                String definition = "public void " + name + "() {";
                String before = SchedulerController.class.getName() + ".INSTANCE.delay(" + rate + "L," + async + ",";
                String selfReference = Modifier.isStatic(method.getModifiers()) ? "null" : "this";
                String callOriginal = "\"" + toLoad + "\"," + selfReference + ", \"" + method.getName() + "\"";
                String after = ");}";
                CtMethod generated = CtMethod.make(definition + before + callOriginal + after, ctClass);
                generated.setModifiers(method.getModifiers());
                ctClass.addMethod(generated);
            }
        }
        ctClass.toClass();
    }

    @SneakyThrows
    public static CtMethod[] getAllMethods(CtMethod[] methods) {
        List<CtMethod> methods1 = new ArrayList<>();
        for (CtMethod method : methods) {
            if (method.hasAnnotation(Delayed.class) && method.getReturnType() == CtClass.voidType) {
                methods1.add(method);
            }
        }
        return methods1.toArray(new CtMethod[0]);
    }

    @SneakyThrows
    private static long getRate(CtMethod element) {
        Delayed annotation = (Delayed) element.getAnnotation(Delayed.class);

        long rate = 0;

        rate += annotation.milliseconds();
        rate += annotation.ticks() * 50;
        rate += annotation.seconds() * 1000;
        rate += annotation.minutes() * 60000;
        rate += annotation.hours() * 3600000;
        rate += annotation.days() * 86400000;

        return rate;
    }

    @SneakyThrows
    private static boolean isAsync(CtMethod element) {
        Delayed annotation = (Delayed) element.getAnnotation(Delayed.class);

        return annotation.async();
    }
}
