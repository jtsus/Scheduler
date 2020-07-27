package net.eterniamc.scheduler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;

@Getter
public class AnnotationData {
    private long time;
    private boolean async;

    public AnnotationData(Annotation annotation) {
        String raw = annotation.toString();
        String args = raw.replaceAll(".+[(]", "").replace(")", "").replace(" ", "");
        for (String s : args.split(",")) {
            String key = s.split("=")[0];
            String value = s.split("=")[1];

            TimeUnit unit = TimeUnit.get(key);

            if (unit == null) {
                async = Boolean.parseBoolean(value);
            } else {
                time += unit.getMultiplier() * Double.parseDouble(value);
            }
        }
    }

    @RequiredArgsConstructor
    @Getter
    private enum TimeUnit {
        milliseconds(1),
        ticks(50),
        seconds(1000),
        minutes(60000),
        hours(3600000),
        days(86400000);

        private final long multiplier;

        public static TimeUnit get(String name) {
            for (TimeUnit unit : values()) {
                if (unit.name().equalsIgnoreCase(name)) {
                    return unit;
                }
            }
            return null;
        }
    }
}
