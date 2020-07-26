package net.eterniamc.scheduler;

import lombok.SneakyThrows;
import net.eterniamc.scheduler.runner.SeparateClassloaderTestRunner;
import net.eterniamc.scheduler.types.BasicDelayCarrier;
import net.eterniamc.scheduler.types.DelayCarrier;
import net.eterniamc.scheduler.types.pack.OtherBasicDelayCarrier;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(SeparateClassloaderTestRunner.class)
public class DelayedServiceInspectorTest {

    @Test
    public void packageBasedLookup() {
        SchedulerController.INSTANCE.delayedElements.clear();
        SchedulerController.INSTANCE.registerAllDelayedMethods("net.eterniamc.scheduler.types.pack");

        OtherBasicDelayCarrier.doSomething();

        assertEquals(1, SchedulerController.INSTANCE.delayedElements.size());
    }

    @Test
    public void delayedMethodGetsHookedInto() {
        SchedulerController.INSTANCE.delayedElements.clear();
        SchedulerController.INSTANCE.registerDelayedMethodsIn("net.eterniamc.scheduler.types.BasicDelayCarrier");

        BasicDelayCarrier.doSomething();

        assertEquals(1, SchedulerController.INSTANCE.delayedElements.size());
    }

    @Test
    @SneakyThrows
    public void delayedMethodGetsDelayed() {
        SchedulerController.INSTANCE.registerDelayedMethodsIn("net.eterniamc.scheduler.types.DelayCarrier");

        DelayCarrier object = new DelayCarrier();

        assertEquals(0, object.getUpdates());

        object.someCall();
        Thread.sleep(50);

        assertEquals(1, object.getUpdates());
    }
}
