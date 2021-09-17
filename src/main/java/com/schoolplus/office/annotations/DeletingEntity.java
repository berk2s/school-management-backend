package com.schoolplus.office.annotations;

import com.schoolplus.office.web.models.DomainAction;
import com.schoolplus.office.web.models.TransactionDomain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeletingEntity {
    TransactionDomain domain();
    DomainAction action();
    String idArg();
}
