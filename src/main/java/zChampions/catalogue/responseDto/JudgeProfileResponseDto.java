package zChampions.catalogue.responseDto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.enumsEntities.KindOfSport;
import zChampions.catalogue.enumsEntities.Qualification;
import zChampions.catalogue.enumsEntities.RoleSport;

import java.util.Set;

@Data
@Builder
public class JudgeProfileResponseDto {
    private Long judgeProfileId;
    private RoleSport roleSport;
    private Qualification qualification;
    private Long userId;
    private KindOfSport typeOfSport;
}
