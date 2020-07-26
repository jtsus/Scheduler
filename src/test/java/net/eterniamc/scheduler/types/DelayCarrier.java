package net.eterniamc.scheduler.types;

import lombok.Getter;
import net.eterniamc.scheduler.annotation.Delayed;

public class DelayCarrier {
    @Getter
    private int updates;

    @Delayed(milliseconds = 10, async = true)
    public void someCall() {
        updates = 1;
    }
}
