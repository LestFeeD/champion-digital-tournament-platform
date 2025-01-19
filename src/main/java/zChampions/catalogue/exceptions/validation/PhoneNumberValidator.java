package zChampions.catalogue.exceptions.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {})
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = "^\\+?[0-9\\s\\-()]{7,15}$", message = "Пожалуйста, предоставьте действующий номер телефона")
public @interface PhoneNumberValidator {
    String message() default "Пожалуйста, предоставьте действующий номер телефона";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
