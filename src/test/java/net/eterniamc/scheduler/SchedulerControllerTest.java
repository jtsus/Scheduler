package net.eterniamc.scheduler;

import lombok.SneakyThrows;
import net.eterniamc.scheduler.types.BasicObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SchedulerControllerTest {

    @Before
    public void setUp() {
        SchedulerController.INSTANCE.scheduledElements.clear();
        SchedulerController.INSTANCE.asyncScheduledElements.clear();
    }

    /**
     * This test will print an error due to the call to EventBus#register(), it can be ignored.
     */
    @Test
    @SneakyThrows
    public void rate() {
        BasicObject object = new BasicObject();
        SchedulerController.INSTANCE.initialize();
        SchedulerController.INSTANCE.registerSynchronizationService(object);

        Thread.sleep(50);

        assertTrue(2 <= object.getUpdates() && object.getUpdates() <= 3);
        SchedulerController.INSTANCE.executor.shutdown();
    }

    @Test
    public void testObjectRegistration() {
        SchedulerController.INSTANCE.registerSynchronizationService(new BasicObject());

        assertEquals(2, SchedulerController.INSTANCE.scheduledElements.size());
    }

    @Test
    public void testObjectDeregistration() {
        BasicObject object = new BasicObject();
        SchedulerController.INSTANCE.registerSynchronizationService(object);
        SchedulerController.INSTANCE.dismissSynchronizationService(object);

        assertTrue(SchedulerController.INSTANCE.scheduledElements.isEmpty());
    }
}
