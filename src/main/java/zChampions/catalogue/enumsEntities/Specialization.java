package zChampions.catalogue.enumsEntities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Specialization {

    CROSSFIT("Кроссфит"),
    HEAD_COACH("Главный тренер"),
    SENIOR_COACH("Старший тренер"),
    INSTRUCTOR("Инструктор");

    private final String displayName;

    Specialization(String displayName) {
        this.displayName = displayName;
    }
}
