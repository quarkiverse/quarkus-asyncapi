package io.quarkiverse.asyncapi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Schema {

    /**
     * If the message-Object is type-safe, the Annotation-Scanner can obtain the message-description via reflection. But
     * if the message is send as String (e.g. JSON-encoded), this is no possible. Array of classes, contained in the
     * message. E.g. if a Message <code>List&lt;A&lt;B&gt;&gt;</code> can be described as
     * <code>@Schema({List.class, A.class, B.class})</code>
     *
     * @return
     */
    Class[] implementation() default {};

    String description() default "";

    Class<?>[] oneOf() default {};

    boolean readOnly() default false;

    boolean deprecated() default false;
}
