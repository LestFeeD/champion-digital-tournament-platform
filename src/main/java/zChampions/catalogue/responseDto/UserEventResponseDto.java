package zChampions.catalogue.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserEventResponseDto {
    private String firstName;
    private String lastName;
    private String email;

}
