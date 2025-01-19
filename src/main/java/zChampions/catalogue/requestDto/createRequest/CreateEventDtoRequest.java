package zChampions.catalogue.requestDto.createRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zChampions.catalogue.enumsEntities.KindOfSport;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateEventDtoRequest {


    @Size(min=2, max=30, message = "Размер названия должен быт от 2 до 50млн")
    @NotEmpty(message = "Значение не должно быть пустым.")
    private String title;

    @NotNull(message = "название вида спорта не должно быть пустое")
    @JsonProperty("kind_of_sport")
    private KindOfSport kindOfSport;

    @NotEmpty(message = "Значение не должно быть пустым.")
    private String region;

    @NotEmpty(message = "Значение не должно быть пустым.")
    private String city;

    @NotNull(message = "Значение не должно быть пустым.")
    private LocalDate  createdAt;

    @NotNull(message = "Значение не должно быть пустым.")
    private LocalDate  endsAt;

    @Size( max=300, message = "Размер значения не должен превышать 300 символов")
    private String information;

    @Size( max=300, message = "Размер значения не должен превышать 300 символов")
    private String comments;

    @NotEmpty(message = "Список ID организаций не должен быть пустым")
    private List<Long> organizationIds;
}
