package com.schoolplus.office.web.exceptions;

import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.ErrorResponseDto;
import com.schoolplus.office.web.models.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    // TODO
    @Override
    protected ResponseEntity handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn("HttpMessageNotReadableException: {}", ex.getMessage());
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(),  HttpStatus.BAD_REQUEST));
    }

    @Override
    protected ResponseEntity handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn("NoHandlerFoundException: {}", ex.getMessage());
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(),  HttpStatus.NOT_FOUND));
    }

    @Override
    protected ResponseEntity handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn("HttpMessageNotWritableException: {}", ex.getMessage());
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(),  HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    protected ResponseEntity handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn("HttpMediaTypeNotAcceptableException: {}", ex.getMessage());
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(),  HttpStatus.BAD_REQUEST));
    }

    @Override
    protected ResponseEntity handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn("HttpMediaTypeNotSupportedException: {}", ex.getMessage());
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(),  HttpStatus.UNSUPPORTED_MEDIA_TYPE));
    }

    @Override
    protected ResponseEntity handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn("MissingServletRequestParameterException: {}", ex.getMessage());
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @Override
    protected ResponseEntity handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn("MethodArgumentNotValidException: {}", ex.getMessage());
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ErrorDesc.BAD_REQUEST.getDesc(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponseDto> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("ConstraintViolationException: {}", ex.getMessage());
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException: {}", ex.getMessage());
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<ErrorResponseDto> handleNullPointerException(NullPointerException ex) {
        log.warn("NullPointerException: {} {}", ex.getLocalizedMessage(), ex.getMessage());
        return errorResponse(new ErrorResponseDto(ErrorType.SERVER_ERROR, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentialsException(BadCredentialsException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_GRANT, ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(InvalidGrantException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidGrantException(InvalidGrantException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_GRANT, ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_GRANT, ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(JWTException.class)
    public ResponseEntity<ErrorResponseDto> handleJWTCreatingException(JWTException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_GRANT, ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFoundException(UserNotFoundException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(AuthorityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthorityNotFoundException(AuthorityNotFoundException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleRoleNotFoundException(RoleNotFoundException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleStudentNotFoundException(StudentNotFoundException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(ParentNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleParentNotFoundException(ParentNotFoundException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(TeachingSubjectNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleTeachingSubjectNotFoundException(TeachingSubjectNotFoundException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(TeacherNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleTeacherNotFoundException(TeacherNotFoundException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(ClassroomNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleClassroomNotFoundException(ClassroomNotFoundException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleAppointmentNotFoundException(AppointmentNotFoundException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(AppointmentNotAvailableException.class)
    public ResponseEntity<ErrorResponseDto> handleAppointmentNotAvailableException(AppointmentNotAvailableException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(AnnouncementNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleAnnouncementNotFoundException(AnnouncementNotFoundException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(OrganizationNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleOrganizationNotFoundException(OrganizationNotFoundException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(GradeNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleGradeNotFoundException(GradeNotFoundException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(LessonNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleLessonNotFoundException(LessonNotFoundException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(SyllabusNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleSyllabusNotFoundException(SyllabusNotFoundException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(ContinuityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleContinuityNotFoundException(ContinuityNotFoundException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidRequestException(InvalidRequestException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_REQUEST, ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleTokenNotFoundException(TokenNotFoundException ex) {
        return errorResponse(new ErrorResponseDto(ErrorType.INVALID_GRANT, ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    private ResponseEntity<ErrorResponseDto> errorResponse(ErrorResponseDto errorResponseDto){
        return new ResponseEntity<>(errorResponseDto, errorResponseDto.getHttpStatus());
    }
}
