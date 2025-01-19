package zChampions.catalogue.enumsEntities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum KindOfSport {

    SWIMMING("Плавание"),
    FOOTBALL("Футбол"),
    BASKETBALL("Баскетбол"),
    HOCKEY("Хоккей");

    private final String displayName;

    KindOfSport(String displayName) {
        this.displayName = displayName;
    }

}
