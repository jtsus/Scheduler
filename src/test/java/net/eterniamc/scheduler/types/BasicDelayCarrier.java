package net.eterniamc.scheduler.types;

import net.eterniamc.scheduler.annotation.Delayed;

public class BasicDelayCarrier {

    @Delayed(hours = 4, seconds = 5)
    public static void doSomething(Double[] array, String var0) {

    }

    @Delayed(milliseconds = 1)
    public void doSomethingComplex(int[][] array, double d) {
        // Actually does nothing
    }
}
