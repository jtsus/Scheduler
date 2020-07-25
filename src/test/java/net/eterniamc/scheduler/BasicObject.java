package net.eterniamc.scheduler;

import lombok.Getter;
import net.eterniamc.scheduler.sync.Rate;
import net.eterniamc.scheduler.sync.Scheduled;

public class BasicObject {
    @Getter
    private int updates;

    @Scheduled(rate = Rate.HALF_TICK, async = true)
    public void update() {
        updates++;
    }

    @Scheduled(rate = Rate.FAST)
    public void updateSomething() {

    }

    @Scheduled(rate = Rate.INSTANT)
    public void render() {

    }
}
