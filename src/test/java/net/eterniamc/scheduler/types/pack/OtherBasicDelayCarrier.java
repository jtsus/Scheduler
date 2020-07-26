package net.eterniamc.scheduler.types.pack;

import net.eterniamc.scheduler.annotation.Delayed;

public class OtherBasicDelayCarrier {

    @Delayed(hours = 4)
    public static void doSomething() {
        // Actually does nothing
    }
}
