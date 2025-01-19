package zChampions.catalogue.enumsEntities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Disciplines {
    BREASTSTROKE_FIFTY_METERS("50м брасс"),
    BREASTSTROKE_ONE_HUNDRED_METERS("100м брасс"),
    BREASTSTROKE_TWO_HUNDRED_METERS("200м брасс"),

    FOOTBALL("Футбол"),
    BEACH_FOOTBALL("Пляжный футбол"),
    MINIMAL_FOOTBALL("Мини футбол"),

    BASKETBALL("Баскетбол"),
    BASKETBALL_3x3("Баскетбол 3x3"),
    MINIMAL_BASKETBALL("мини баскетбол"),

    HOCKEY("Хоккей");

    private final String displayName;

    Disciplines(String displayName) {
        this.displayName = displayName;
    }



}
