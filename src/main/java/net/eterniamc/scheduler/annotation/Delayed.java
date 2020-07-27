package net.eterniamc.scheduler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("unused")
public @interface Delayed {
    /**
     * @return The rate of refresh in ticks
     */
    double ticks() default 0;

    /**
     * @return The rate of refresh in milliseconds
     */
    double milliseconds() default 0;

    /**
     * @return The rate of refresh in seconds
     */
    double seconds() default 0;

    /**
     * @return The rate of refresh in minutes
     */
    double minutes() default 0;

    /**
     * @return The rate of refresh in hours
     */
    double hours() default 0;

    /**
     * @return The rate of refresh in
     */
    double days() default 0;

    /**
     * @return Whether the method should be executed asynchronously
     */
    boolean async() default false;
}
