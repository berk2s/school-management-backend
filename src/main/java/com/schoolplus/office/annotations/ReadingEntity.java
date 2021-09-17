package com.schoolplus.office.annotations;

import com.schoolplus.office.web.models.DomainAction;
import com.schoolplus.office.web.models.TransactionDomain;
import org.mapstruct.TargetType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadingEntity {
    TransactionDomain domain();
    DomainAction action();
    boolean isList() default false;
}
