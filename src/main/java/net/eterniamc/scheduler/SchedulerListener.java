package net.eterniamc.scheduler;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SchedulerListener {

    protected SchedulerListener() {}

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            for (ScheduledElement element : SchedulerController.INSTANCE.scheduledElements) {
                if (element.shouldRun()) {
                    element.run();
                }
            }
            for (DelayedElement element : SchedulerController.INSTANCE.delayedElements) {
                if (element.shouldRun()) {
                    element.run();
                    SchedulerController.INSTANCE.delayedElements.remove(element);
                }
            }
        }
    }
}
