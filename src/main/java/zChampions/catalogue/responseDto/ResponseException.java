package zChampions.catalogue.responseDto;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;


public record ResponseException(String message, HttpStatus httpStatus, ZonedDateTime timestamp) {

}
