package net.eterniamc.scheduler;

import lombok.Data;
import lombok.SneakyThrows;

@Data
public class DelayedElement {
    private final long rate;
    private final Runnable action;
    private final long start = System.currentTimeMillis();

    public boolean shouldRun() {
        return System.currentTimeMillis() - start >= rate;
    }

    @SneakyThrows
    public void run() {
        action.run();
    }
}
