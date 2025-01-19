package zChampions.catalogue.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginResponse {
    private String message;
    private Object data;
}
