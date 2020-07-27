package net.eterniamc.scheduler;

import junit.framework.TestCase;
import lombok.SneakyThrows;
import net.eterniamc.scheduler.runner.SeparateClassloaderTestRunner;
import net.eterniamc.scheduler.types.BasicDelayCarrier;
import net.eterniamc.scheduler.types.DelayCarrier;
import net.eterniamc.scheduler.types.pack.AnotherObject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SeparateClassloaderTestRunner.class)
public class ClassImpactorTest extends TestCase {

    @Test
    public void delayedMethodGetsHookedInto() {
        SchedulerController.INSTANCE.delayedElements.clear();
        SchedulerController.INSTANCE.processAnnotationsFor("net.eterniamc.scheduler.types.BasicDelayCarrier");

        BasicDelayCarrier.doSomething(new Double[0], "yoo");

        assertEquals(1, SchedulerController.INSTANCE.delayedElements.size());
    }

    @Test
    public void delayedMethodsInPackageGetsHookedInto() {
        SchedulerController.INSTANCE.delayedElements.clear();
        SchedulerController.INSTANCE.processAnnotationsIn("net.eterniamc.scheduler.types.pack");

        new AnotherObject().someDelayedMethod();
        new AnotherObject().someSuperDelayedMethod();

        assertEquals(2, SchedulerController.INSTANCE.delayedElements.size());
    }

    @Test
    @SneakyThrows
    public void delayedMethodGetsDelayed() {
        SchedulerController.INSTANCE.processAnnotationsFor("net.eterniamc.scheduler.types.DelayCarrier");

        DelayCarrier object = new DelayCarrier();

        assertEquals(0, object.getUpdates());

        object.someCall();
        Thread.sleep(50);

        assertEquals(1, object.getUpdates());
    }
}
