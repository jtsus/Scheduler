package net.eterniamc.scheduler;

import lombok.Data;
import lombok.SneakyThrows;
import net.eterniamc.scheduler.sync.Rate;

import java.lang.reflect.Method;

@Data
public class ScheduledElement {
    private final Object source;
    private final Rate rate;
    private final boolean async;
    private final Method method;
    private long lastRan;

    public boolean shouldRun() {
        return System.currentTimeMillis() - lastRan >= rate.getTime();
    }

    @SneakyThrows
    public void run() {
        method.invoke(source);
        lastRan = System.currentTimeMillis();
    }
}
