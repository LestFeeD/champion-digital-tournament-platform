package zChampions.catalogue.enumsEntities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Qualification {

    SPORTS_REFEREE_OF_THE_ONE_CATEGORY("Спортивный судья 1 категории"),
    SPORTS_REFEREE_OF_THE_SECOND_CATEGORY("Спортивный судья 2 категории"),
    SPORTS_REFEREE_OF_THE_THIRD_CATEGORY("Спортивный судья 3 категории"),
    SPORTS_REFEREE_OF_THE_ALL_RUSSIAN_CATEGORY("Спортивный судья всероссийской категории");

    private final String displayName;

    Qualification(String displayName) {
        this.displayName = displayName;
    }
}
