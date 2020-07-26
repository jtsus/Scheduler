package net.eterniamc.scheduler;

import com.google.common.collect.Lists;
import net.eterniamc.scheduler.annotation.Scheduled;

import java.lang.reflect.*;
import java.util.*;

public class ScheduledServiceInspector {

    public static List<ScheduledElement> parseElementsFrom(Object source) {
        return getElements(source, getAllMethods(source.getClass()));
    }

    public static Method[] getAllMethods(Class<?> aClass) {
        List<Method> methods = new ArrayList<>();
        do {
            Collections.addAll(methods, aClass.getDeclaredMethods());
            aClass = aClass.getSuperclass();
        } while (aClass != null);
        return methods.toArray(new Method[0]);
    }

    private static List<ScheduledElement> getElements(Object source, Method[] objects) {
        List<ScheduledElement> elements = Lists.newArrayList();

        for (Method object : objects) {
            // Set them to public if they are private for obvious reasons
            if (!object.isAccessible()) {
                object.setAccessible(true);
            }

            long declaredRate = getRate(object);

            if (declaredRate <= 0) {
                continue;
            }

            elements.add(new ScheduledElement(source, declaredRate, isAsync(object), object));
        }

        return elements;
    }

    private static long getRate(AnnotatedElement element) {
        if (!element.isAnnotationPresent(Scheduled.class)) {
            return -1;
        }

        Scheduled annotation = element.getAnnotation(Scheduled.class);

        long rate = 0;

        rate += annotation.milliseconds();
        rate += annotation.ticks() * 50;
        rate += annotation.seconds() * 1000;
        rate += annotation.minutes() * 60000;
        rate += annotation.hours() * 3600000;
        rate += annotation.days() * 86400000;

        return rate;
    }

    private static boolean isAsync(AnnotatedElement element) {
        if (!element.isAnnotationPresent(Scheduled.class)) {
            return false;
        }

        Scheduled annotation = element.getAnnotation(Scheduled.class);

        return annotation.async();
    }
}
