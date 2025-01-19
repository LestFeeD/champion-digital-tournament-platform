package zChampions.catalogue.responseDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import zChampions.catalogue.exceptions.validation.ExtendedEmailValidator;

import java.time.LocalDate;

@Data
@Builder
public class BasicProfileResponseDto {
    private String firstName;

    private String lastName;

    private String patronymic;

    private String gender;


    private String email;

    private String password;

    private String information;

    private LocalDate dateOfBirth;

    private Double height;

    private Double weight;

    private String country;

    private String region;

    private String city;
}
