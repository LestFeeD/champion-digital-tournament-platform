package zChampions.catalogue.responseDto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BasicProfileEditResponseDto {
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
