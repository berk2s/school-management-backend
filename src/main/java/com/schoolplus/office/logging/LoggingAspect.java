package com.schoolplus.office.logging;

import com.schoolplus.office.annotations.*;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.security.SecurityUser;
import com.schoolplus.office.web.models.LogableType;
import com.schoolplus.office.components.ColumnExtractor;
import com.schoolplus.office.domain.TransactionLog;
import com.schoolplus.office.repository.TransactionLogRepository;
import com.schoolplus.office.web.models.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Configuration
public class LoggingAspect {

    private final TransactionLogRepository transactionLogRepository;
    private final UserRepository userRepository;

    @AfterReturning(value = "@annotation(com.schoolplus.office.annotations.ReadingEntity)", returning = "object")
    public void afterReadingEntity(JoinPoint joinPoint, Object object) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        ReadingEntity readingEntity = method.getAnnotation(ReadingEntity.class);

        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setTransactionType(TransactionType.READ);
        transactionLog.setTransactionDomain(readingEntity.domain());
        transactionLog.setDomainActionType(readingEntity.action());

        if (SecurityContextHolder.getContext().getAuthentication().getDetails() instanceof SecurityUser) {
            SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
            transactionLog.setPerformedBy(securityUser.getUser());
        }

        if (!readingEntity.isList()) {
            Field idField;

            ColumnExtractor extractor = new ColumnExtractor(object.getClass());
            idField = extractor.getColumnField(LogableType.ID);

            if (idField == null) {
                ColumnExtractor superExtractor = new ColumnExtractor(object.getClass().getSuperclass());
                idField = superExtractor.getColumnField(LogableType.ID);
            }

            Object relatedId;

            if (idField.getType().isAssignableFrom(Long.class)) {
                relatedId = (Long) extractor.runGetter(idField, object);
            } else if (idField.getType().isAssignableFrom(UUID.class)) {
                relatedId = (UUID) extractor.runGetter(idField, object);
            } else if (idField.getType().isAssignableFrom(String.class)) {
                try {
                    relatedId = UUID.fromString((String) extractor.runGetter(idField, object));
                } catch (IllegalArgumentException e) {
                    relatedId = (String) extractor.runGetter(idField, object);
                }
            } else {
                relatedId = null;
            }

            assert relatedId != null;
            transactionLog.setRelatedId(relatedId.toString());
        }

        transactionLogRepository.save(transactionLog);
    }

    @AfterReturning(value = "@annotation(com.schoolplus.office.annotations.CreatingEntity)", returning = "object")
    public void afterCreatingEntity(JoinPoint joinPoint, Object object) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        CreatingEntity creatingEntity = method.getAnnotation(CreatingEntity.class);

        Field idField;

        ColumnExtractor extractor = new ColumnExtractor(object.getClass());
        idField = extractor.getColumnField(LogableType.ID);

        if (idField == null) {
            ColumnExtractor superExtractor = new ColumnExtractor(object.getClass().getSuperclass());
            idField = superExtractor.getColumnField(LogableType.ID);
        }

        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setTransactionType(TransactionType.CREATE);
        transactionLog.setTransactionDomain(creatingEntity.domain());
        transactionLog.setDomainActionType(creatingEntity.action());

        if (SecurityContextHolder.getContext().getAuthentication().getDetails() instanceof SecurityUser) {
            SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
            transactionLog.setPerformedBy(securityUser.getUser());
        }

        Object relatedId;

        if (idField.getType().isAssignableFrom(Long.class)) {
            relatedId = (Long) extractor.runGetter(idField, object);
        } else if (idField.getType().isAssignableFrom(UUID.class)) {
            relatedId = (UUID) extractor.runGetter(idField, object);
        } else if (idField.getType().isAssignableFrom(String.class)) {
            try {
                relatedId = UUID.fromString((String) extractor.runGetter(idField, object));
            } catch (IllegalArgumentException e) {
                relatedId = (String) extractor.runGetter(idField, object);
            }
        } else {
            relatedId = null;
        }

        assert relatedId != null;
        transactionLog.setRelatedId(relatedId.toString());

        transactionLogRepository.save(transactionLog);
    }

    @Transactional
    @AfterReturning(value = "@annotation(com.schoolplus.office.annotations.UpdatingEntity)", returning = "object")
    public void afterUpdatingEntity(JoinPoint joinPoint, Object object) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        UpdatingEntity updatingEntity = method.getAnnotation(UpdatingEntity.class);

        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setTransactionType(TransactionType.UPDATE);
        transactionLog.setTransactionDomain(updatingEntity.domain());
        transactionLog.setDomainActionType(updatingEntity.action());

        if (SecurityContextHolder.getContext().getAuthentication().getDetails() instanceof SecurityUser) {
            SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
            transactionLog.setPerformedBy(securityUser.getUser());
        }

        int idArgIndex = -1;
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];

            if (parameter.getName().equals(updatingEntity.idArg())) {
                idArgIndex = i;
                break;
            }
        }

        if (idArgIndex > -1 && joinPoint.getArgs().length > idArgIndex) {
            transactionLog.setRelatedId(joinPoint.getArgs()[idArgIndex].toString());
        } else {
            if (!updatingEntity.isList()) {
                ColumnExtractor extractor = new ColumnExtractor(object.getClass());
                Field idField = extractor.getColumnField(LogableType.ID);

                Object relatedId;

                if (idField.getType().isAssignableFrom(Long.class)) {
                    relatedId = (Long) extractor.runGetter(idField, object);
                } else if (idField.getType().isAssignableFrom(UUID.class)) {
                    relatedId = (UUID) extractor.runGetter(idField, object);
                } else if (idField.getType().isAssignableFrom(String.class)) {
                    try {
                        relatedId = UUID.fromString((String) extractor.runGetter(idField, object));
                    } catch (IllegalArgumentException e) {
                        relatedId = (String) extractor.runGetter(idField, object);
                    }
                } else {
                    relatedId = null;
                }

                assert relatedId != null;
                transactionLog.setRelatedId(relatedId.toString());
            }
        }

        transactionLogRepository.save(transactionLog);
    }

    @AfterReturning(value = "@annotation(com.schoolplus.office.annotations.DeletingEntity)")
    public void afterDeletingEntity(JoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        DeletingEntity deletingEntity = method.getAnnotation(DeletingEntity.class);

        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setTransactionType(TransactionType.DELETE);
        transactionLog.setTransactionDomain(deletingEntity.domain());
        transactionLog.setDomainActionType(deletingEntity.action());

        if (SecurityContextHolder.getContext().getAuthentication().getDetails() instanceof SecurityUser) {
            SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
            transactionLog.setPerformedBy(securityUser.getUser());
        }

        int idArgIndex = -1;
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];

            if (parameter.getName().equals(deletingEntity.idArg())) {
                idArgIndex = i;
                break;
            }
        }

        if (idArgIndex > -1 && joinPoint.getArgs().length > idArgIndex) {
            transactionLog.setRelatedId(joinPoint.getArgs()[idArgIndex].toString());
        }

        transactionLogRepository.save(transactionLog);
    }

    @AfterReturning(value = "@annotation(com.schoolplus.office.annotations.AuthenticationProcess)", returning = "object")
    public void afterAuthenticationProcess(JoinPoint joinPoint, Object object) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        AuthenticationProcess authenticationProcess = method.getAnnotation(AuthenticationProcess.class);

        Object requestObject = joinPoint.getArgs()[0];

        ColumnExtractor extractor = new ColumnExtractor(requestObject.getClass());
        Field usernameField = extractor.getColumnField(LogableType.NAME);

        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setTransactionType(TransactionType.AUTHENTICATION);
        transactionLog.setTransactionDomain(authenticationProcess.domain());
        transactionLog.setDomainActionType(authenticationProcess.action());

        if (usernameField.getType().isAssignableFrom(String.class)) {
            String username = (String) extractor.runGetter(usernameField, requestObject);

            userRepository.
                    findByUsername(username)
                    .ifPresent(user -> transactionLog.setRelatedId(user.getId().toString()));
        }

        transactionLogRepository.save(transactionLog);
    }

}
