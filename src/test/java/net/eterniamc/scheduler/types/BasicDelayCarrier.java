package net.eterniamc.scheduler.types;

import net.eterniamc.scheduler.annotation.Delayed;

public class BasicDelayCarrier {

    @Delayed(hours = 4)
    public static void doSomething() {
        // Actually does nothing
    }
}
