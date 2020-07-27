**Scheduler**
=
Created by Justin

Description
-
Scheduler is a library which adds the @Scheduled and @Delayed annotations. The @Scheduled annotation makes the method it
is attatched to run at a set interval while the @Delayed annotation delays calls to its method.

SchedulerController#delay() may be called instead of using the @Delayed annotation

Scheduler can and should be shaded into your jar.

SchedulerController#initialize() must be called.

#### @Scheduled

The Scheduled annotation takes in a double for each time unit, and a boolean for whether to schedule the target method 
out of sync or not. The interval that the method is scheduled at is equal to the sum of all times entered. In order to 
have the annotation take effect you must call SchedulerController#registerSynchronizationService() for the object 
holding the annotated method.

You may call SchedulerController#dismissSynchronizationService() to stop all scheduled tasks for a source

#### @Delayed

The Scheduled annotation takes in a double for each time unit, and a boolean for whether to schedule the target method 
out of sync or not. The delay takes place before the method is actually called is equal to the sum of all times entered.
In order to have the annotation take effect you must call SchedulerController#processAnnotationsFor(className) or 
processAnnotationsIn(packageName) before the classes holding the annotations are loaded.

This annotation changes the bytecode of classes by basically wrapping methods annotated with @Delayed with
a call to SchedulerController#delay().

Adding to your project
-

Scheduler can easily be added to your project using the JitPack dependency. Go [here](https://jitpack.io/#JustinSamaKun/Scheduler)
for more information.
