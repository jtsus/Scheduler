package net.eterniamc.scheduler.sync;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {
    /**
     * @return The rate of refresh
     */
    Rate rate() default Rate.TICK;

    /**
     * @return Whether the method should be executed asynchronously
     */
    boolean async() default false;
}
