package models;

import extensions.LoginExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Target - применяем к методам и классам
 * @Retention- будет доступна во время выполнения (RUNTIME)
 * @ExtendWith(LoginExtension.class) -при обнаружении аннотации @WithLogin нужно подключать расширение LoginExtension и внутри него
 * вызвать его метод beforeEach.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(LoginExtension.class)
public @interface WithLogin {

    String username() default "";
    String password() default "";
}
