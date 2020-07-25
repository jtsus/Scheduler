package net.eterniamc.scheduler;

import com.google.common.collect.Lists;
import net.eterniamc.scheduler.sync.Rate;
import net.eterniamc.scheduler.sync.Scheduled;

import java.lang.reflect.*;
import java.util.*;

public class SynchronizationServiceInspector {

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

            Rate declaredRate = getRate(object);

            if (declaredRate == null) {
                continue;
            }

            elements.add(new ScheduledElement(source, declaredRate, isAsync(object), object));
        }

        return elements;
    }

    private static Rate getRate(AnnotatedElement element) {
        if (!element.isAnnotationPresent(Scheduled.class)) {
            return null;
        }

        Scheduled annotation = element.getAnnotation(Scheduled.class);

        return annotation.rate();
    }

    private static boolean isAsync(AnnotatedElement element) {
        if (!element.isAnnotationPresent(Scheduled.class)) {
            return false;
        }

        Scheduled annotation = element.getAnnotation(Scheduled.class);

        return annotation.async();
    }
}
