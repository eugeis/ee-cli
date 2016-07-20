package ee.cli.core.integ

import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.Target

import static java.lang.annotation.ElementType.*
import static java.lang.annotation.RetentionPolicy.RUNTIME

@Target([METHOD, FIELD, PARAMETER])
@Retention(RUNTIME)
@Documented
public @interface Relative {
    String value() default 'home';
}

