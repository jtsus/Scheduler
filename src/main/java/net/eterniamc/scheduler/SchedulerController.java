package net.eterniamc.scheduler;

import com.google.common.collect.Sets;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public enum SchedulerController {
    INSTANCE;

    /**
     * ALL SYNCHRONIZED DATA WILL BE STORED HERE
     * DOES NOT REMOVE DEAD OBJECTS BE SURE TO DISMISS SERVICE WHEN FINISHED
     */
    protected final Set<ScheduledElement> scheduledElements = Sets.newHashSet();
    protected final Set<ScheduledElement> asyncScheduledElements = Sets.newConcurrentHashSet();

    protected final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private boolean initialized = false;

    public void initialize() {
        if (initialized) {
            return;
        }
        executor.scheduleAtFixedRate(() -> {
            for (ScheduledElement element : asyncScheduledElements) {
                if (element.shouldRun()) {
                    element.run();
                }
            }
        }, 0, 2, TimeUnit.MILLISECONDS);
        MinecraftForge.EVENT_BUS.register(this);
        initialized = true;
    }

    /**
     * Registers a class as refresh service listener
     *
     * @param source This can be any object you want
     */
    public void registerSynchronizationService(Object source) {
        List<ScheduledElement> elements = SynchronizationServiceInspector.parseElementsFrom(source);
        for (ScheduledElement element : elements) {
            if (element.isAsync()) {
                asyncScheduledElements.add(element);
            } else {
                scheduledElements.add(element);
            }
        }
    }

    /**
     * Removes a synchronized service
     *
     * @param source This can be any object you want
     */
    public void dismissSynchronizationService(Object source) {
        scheduledElements.removeIf(element -> element.getSource().equals(source));
        asyncScheduledElements.removeIf(element -> element.getSource().equals(source));
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            for (ScheduledElement element : scheduledElements) {
                if (element.shouldRun()) {
                    element.run();
                }
            }
        }
    }
}
