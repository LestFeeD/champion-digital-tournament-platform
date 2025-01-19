package zChampions.catalogue.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import zChampions.catalogue.exceptions.DuplicateApplicationException;
import zChampions.catalogue.responseDto.ResponseException;
import zChampions.catalogue.exceptions.BadRequestException;
import zChampions.catalogue.exceptions.NotFoundException;
import zChampions.catalogue.exceptions.RoleNotAllowedException;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class DefaultAdvice {


    @ExceptionHandler(value = {BadRequestException.class})
    public ResponseEntity<ResponseException> handleExceptionBadRequest(BadRequestException exception) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ResponseException response = new ResponseException(
                exception.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(response,badRequest);
    }



    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<ResponseException> handleExceptionNotFound(NotFoundException exception) {
        HttpStatus badRequest = HttpStatus.NOT_FOUND;
        ResponseException response = new ResponseException(
                exception.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(response, badRequest);
    }
    @ExceptionHandler(value = {RoleNotAllowedException.class})
    public ResponseEntity<ResponseException> handleExceptionRoleNotAllowedException(RoleNotAllowedException exception) {
        HttpStatus badRequest = HttpStatus.FORBIDDEN;
        ResponseException response = new ResponseException(
                exception.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(response,badRequest);
    }
    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<ResponseException> handleExceptionIllegalArgumentException(IllegalArgumentException exception) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ResponseException response = new ResponseException(
                exception.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(response, badRequest);
    }
    @ExceptionHandler(value = {DuplicateApplicationException.class})
    public ResponseEntity<ResponseException> handleDuplicateApplicationException(DuplicateApplicationException exception) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ResponseException response = new ResponseException(
                exception.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(response, badRequest);
    }



}
