package net.eterniamc.scheduler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("unused")
public @interface Scheduled {
    /**
     * @return The rate of refresh in ticks
     */
    long ticks() default 0;

    /**
     * @return The rate of refresh in milliseconds
     */
    long milliseconds() default 0;

    /**
     * @return The rate of refresh in seconds
     */
    long seconds() default 0;

    /**
     * @return The rate of refresh in minutes
     */
    long minutes() default 0;

    /**
     * @return The rate of refresh in hours
     */
    long hours() default 0;

    /**
     * @return The rate of refresh in
     */
    long days() default 0;

    /**
     * @return Whether the method should be executed asynchronously
     */
    boolean async() default false;
}
