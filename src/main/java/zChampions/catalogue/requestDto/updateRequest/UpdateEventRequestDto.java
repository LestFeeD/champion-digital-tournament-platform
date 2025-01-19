package zChampions.catalogue.requestDto.updateRequest;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateEventRequestDto {
    private String title;
    private LocalDate createdAt;
    private LocalDate  endsAt;
    @Size( max=300, message = "Размер значения не должен превышать 300 символов")
    private String information;
    @Size( max=300, message = "Размер значения не должен превышать 300 символов")
    private String comments;
    @NotEmpty(message = "Список ID организаций не должен быть пустым")
    private List<Long> organizationIds;



}
