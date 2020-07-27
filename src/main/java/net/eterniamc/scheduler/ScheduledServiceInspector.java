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
        return Arrays.stream(aClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Scheduled.class))
                .toArray(Method[]::new);
    }

    private static List<ScheduledElement> getElements(Object source, Method[] objects) {
        List<ScheduledElement> elements = Lists.newArrayList();

        for (Method object : objects) {
            // Set them to public if they are private for obvious reasons
            if (!object.isAccessible()) {
                object.setAccessible(true);
            }

            AnnotationData data = new AnnotationData(object.getAnnotation(Scheduled.class));

            long declaredRate = data.getTime();

            if (declaredRate <= 0) {
                continue;
            }

            elements.add(new ScheduledElement(source, declaredRate, data.isAsync(), object));
        }

        return elements;
    }
}
