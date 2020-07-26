package net.eterniamc.scheduler;

import lombok.SneakyThrows;
import net.eterniamc.scheduler.runner.SeparateClassloaderTestRunner;
import net.eterniamc.scheduler.types.BasicDelayCarrier;
import net.eterniamc.scheduler.types.DelayCarrier;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(SeparateClassloaderTestRunner.class)
public class DelayedServiceInspectorTest {

    @Test
    public void delayedMethodGetsHookedInto() {
        SchedulerController.INSTANCE.registerDelayedMethods("net.eterniamc.scheduler.types.BasicDelayCarrier");

        BasicDelayCarrier.doSomething();

        assertEquals(1, SchedulerController.INSTANCE.delayedElements.size());
    }

    @Test
    @SneakyThrows
    public void delayedMethodGetsDelayed() {
        SchedulerController.INSTANCE.registerDelayedMethods("net.eterniamc.scheduler.types.DelayCarrier");

        DelayCarrier object = new DelayCarrier();

        assertEquals(0, object.getUpdates());

        object.someCall();
        Thread.sleep(50);

        assertEquals(1, object.getUpdates());
    }
}
