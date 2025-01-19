package zChampions.catalogue.requestDto.updateRequest;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zChampions.catalogue.enumsEntities.KindOfSport;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrganizationGeneralSettingsRequestDto {

    @Size(min=2, max=30, message = "Размер значения должен быть в пределах от 2 до 30")
    private String title;

    private String country;

    private String region;

    private String city;

    private KindOfSport kindOfSport;
}
