package net.eterniamc.scheduler;

import lombok.Getter;
import net.eterniamc.scheduler.sync.Scheduled;

public class BasicObject {
    @Getter
    private int updates;

    @Scheduled(milliseconds = 25, async = true)
    public void update() {
        updates++;
    }

    @Scheduled(seconds = 1)
    public void updateSomething() {

    }

    @Scheduled(milliseconds = 1)
    public void render() {

    }
}
