package com.schoolplus.office.components;

import com.schoolplus.office.annotations.Logable;
import com.schoolplus.office.web.models.LogableType;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ColumnExtractor<T> {
    private final Map<LogableType, Field> fields;

    public ColumnExtractor(Class<T> clazz) {
        this.fields = Stream.of(clazz.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(Logable.class))
                        .collect(Collectors.toMap(field -> field.getAnnotation(Logable.class).type(),
                                Function.identity()));
    }

    public Field getColumnField(LogableType logableType) {
        return fields.get(logableType);
    }

    public <R> R extract(LogableType logableType, T t, Class<R> clazz) throws IllegalAccessException {
        return clazz.cast(extract(logableType, t));
    }

    public Object extract(LogableType logableType, T t) throws IllegalAccessException {
        return getColumnField(logableType).get(t);
    }

    public Object runGetter(Field field, Object o) {
        for (Method method : o.getClass().getMethods()) {
            if ((method.getName().startsWith("get")) && (method.getName().length() == (field.getName().length() + 3))) {
                if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase())) {
                    try {
                        return method.invoke(o);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.warn(e.getMessage());
                    }
                }
            }
        }

        return null;
    }

    public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationType) {
        T result = clazz.getAnnotation(annotationType);
        if (result == null) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) {
                return getAnnotation(superclass, annotationType);
            } else {
                return null;
            }
        } else {
            return result;
        }
    }


}
