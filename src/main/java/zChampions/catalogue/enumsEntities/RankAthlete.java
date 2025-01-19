package zChampions.catalogue.enumsEntities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RankAthlete {
    NO_CATEGORY("Нет разряда"),
    THIRD_YOUTH_CATEGORY("3 юношевский"),
    SECOND_YOUTH_CATEGORY("2 юношевский"),
    ONE_YOUTH_CATEGORY("1 юношевский"),
    THIRD_ADULT_CATEGORY("3 взрослый"),
    SECOND_ADULT_CATEGORY("2 взрослый "),
    ONE_ADULT_CATEGORY("1 взрослый"),
    CANDIDATE_MASTER_SPORTS("КМС"),
    MASTER_SPORTS("МС"),
    HONORED_MASTER_SPORTS("ЗМС"),
    MASTER_SPORTS_INTERNATIONAL_CLASS("МСМК");


    private final String displayName;

    RankAthlete(String displayName) {
        this.displayName = displayName;
    }

}
