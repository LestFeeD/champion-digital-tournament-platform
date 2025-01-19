package zChampions.catalogue.enumsEntities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.Getter;

import java.io.IOException;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Experience {
    BEGINNER("Начинающий(занимаюсь меньше года)"),
    EXPERIENCED("Опытный(занимаюсь 1-4 года)"),
    PROFESSIONAL("Профи(занимаюсь 4-9 лет)"),
    MASTER("Мастер(занимаюсь более 10 лет)");

    private final String displayName;

    Experience(String displayName) {
        this.displayName = displayName;
    }

}
