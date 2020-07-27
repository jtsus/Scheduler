package net.eterniamc.scheduler.types;

import lombok.Getter;
import net.eterniamc.scheduler.annotation.Delayed;

public class DelayCarrier {
    @Getter
    private int updates;

    @Delayed(ticks = 0.2, async = true)
    public void someCall() {
        updates = 1;
    }
}
