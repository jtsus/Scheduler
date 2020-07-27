package net.eterniamc.scheduler.types.pack;

import net.eterniamc.scheduler.annotation.Delayed;

public class AnotherObject {

    @Delayed(seconds = 4)
    public void someDelayedMethod() {
        System.out.println("Hey there");
    }

    @Delayed(days = 1)
    public void someSuperDelayedMethod() {

    }
}
