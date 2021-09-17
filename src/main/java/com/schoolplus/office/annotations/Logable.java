package com.schoolplus.office.annotations;

import com.schoolplus.office.web.models.LogableType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Logable {

    LogableType type() default LogableType.ID;

    Class returnType() default Long.class;

}
