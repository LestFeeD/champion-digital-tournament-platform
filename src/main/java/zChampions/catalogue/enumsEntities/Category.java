package zChampions.catalogue.enumsEntities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Category {

    MEDALIST_COACH("тренер медалист"),
    HONORED_COACH_OF_RUSSIA("Заслуженный тренер России"),
    COACH("Тренер"),
    COACH_OF_THE_FIRST_CATEGORY("Тренер первой категории"),
    COACH_OF_THE_SECOND_CATEGORY("Тренер второй категории"),
    COACH_OF_THE_HIGHEST_CATEGORY("Тренер высшей категории");



    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }
}
