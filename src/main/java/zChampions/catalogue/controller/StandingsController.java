package zChampions.catalogue.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import zChampions.catalogue.entity.TypeStandings;
import zChampions.catalogue.enumsEntities.TypeStandingsEnum;
import zChampions.catalogue.repository.TypeStandingsRepository;
import zChampions.catalogue.requestDto.createRequest.CreateNextRoundStandingsDtoRequest;
import zChampions.catalogue.requestDto.createRequest.MoveUserToNextRoundDtoRequest;
import zChampions.catalogue.requestDto.createRequest.CreateStandingsResultDto;
import zChampions.catalogue.requestDto.updateRequest.StandingsResultUpdateDto;
import zChampions.catalogue.responseDto.StandingsResultResponseDto;
import zChampions.catalogue.service.StandingsService;

import java.util.Collections;
import java.util.Set;

@Tag(name = "class working with standings")
@RestController
@RequiredArgsConstructor
@Transactional
public class StandingsController {
    private final StandingsService standingsService;
    private final TypeStandingsRepository typeStandingsRepository;

    public static final String FIND_STANDINGS = "/event/{event_id}/event-standings/{standing_id}";
    public static final String POST_STANDINGS = "/event/{event_id}/add-standings";
    public static final String POST_NEXT_STANDINGS = "/events/{event_id}/standings/{standing_id}/add-next-standings";
    public static final String POST_NEW_TABLE = "/standings/{standing_id}/add-table-standings";


    public static final String UPDATE_STANDINGS = "/api/update-standings/{standing_id}";
    public static final String UPDATE_NEXT_STANDINGS = "/api/event/{event_id}/update-next-standings/{standing_id}";

    public static final String DELETE_STANDINGS = "/api/delete-standings/{standing_id}";

    @Operation(
            summary = "Finds the tournament grid of the event.",
            description = "It writes the specified parameters to the dto, passes them to the service and writes them to the class, saves them, and returns them."
    )
    @GetMapping(FIND_STANDINGS)
    public Set<StandingsResultResponseDto> getStandings(@PathVariable(value = "event_id")Long eventId, @PathVariable(value = "standing_id")Long standingId) {
        return standingsService.resultStandings(eventId, standingId);
    }

    @Operation(
            summary = "Creating a tournament grid for an event.",
            description = "Receives the ID of the screw for which the tournament grid and the DTO with the parameters are being created and sends it to the service for creation and saving."
    )
    @PostMapping(POST_STANDINGS)
    public Set<StandingsResultResponseDto> createStanding(@PathVariable(value = "event_id")Long eventId, @RequestBody CreateStandingsResultDto standingsResultCreateDto) {
        return standingsService.createStanding(eventId, standingsResultCreateDto);
    }

    @Operation(
            summary = "Updating a tournament grid for an event.",
            description = "Receives the ID of the tournament grid and the ATT with the parameters to change, and sends it to the service to update it.."
    )
    @PatchMapping(UPDATE_STANDINGS)
    public Set<StandingsResultResponseDto> updateStanding(@PathVariable(value = "standing_id")Long standingId, @RequestBody StandingsResultUpdateDto standingsResultUpdateDto) {
        Long typeStanding = typeStandingsRepository.findTypeStandings(standingId);
        TypeStandings typeStandings = typeStandingsRepository.findById(typeStanding).orElseThrow();
        if(typeStandings.getNameType() == TypeStandingsEnum.BEST_TIME) {
             standingsService.updateResultStandingsUser(standingId, standingsResultUpdateDto);
        } else if (typeStandings.getNameType() == TypeStandingsEnum.OLYMPIC) {
            standingsService.updateResultStandingsOlympicTypeUser(standingId, standingsResultUpdateDto);
        }
        return Collections.emptySet();

    }

    @Operation(
            summary = "Updates the following grid.",
            description = "Gets the ID of the event, tournament grid, and DTO with the parameters of the new tournament grid to create."
    )
    @PostMapping(POST_NEXT_STANDINGS)
    public Set<StandingsResultResponseDto> createNextStanding(@PathVariable(value = "event_id")Long eventId, @PathVariable(value = "standing_id")Long standingId, @RequestBody CreateNextRoundStandingsDtoRequest nextRoundStandingsDtoRequest) {
        return standingsService.createNextRoundStandings(eventId, standingId, nextRoundStandingsDtoRequest);
    }

    @Operation(
            summary = "Updates the following grid.",
            description = "Get the event ID, the tournament grid, and follow the steps with the parameters to move users across the tables."
    )
    @PatchMapping(UPDATE_NEXT_STANDINGS)
    public void moveUserToNextRound(@PathVariable(value = "event_id")Long eventId, @PathVariable(value = "standing_id")Long standingId, @RequestBody MoveUserToNextRoundDtoRequest moveUserToNextRoundDtoRequest) {
         standingsService.moveUserToNextRound(eventId, moveUserToNextRoundDtoRequest, standingId);
    }

    @Operation(
            summary = "Creating a new table in the tournament grid.",
            description = "Gets the ID of the tournament grid for which you need to create a table."
    )
    @PostMapping(POST_NEW_TABLE)
    public Set<StandingsResultResponseDto> createNewTable( @PathVariable(value = "standing_id")Long standingId, @RequestBody CreateNextRoundStandingsDtoRequest nextRoundStandingsDtoRequest) {
        return standingsService.addNewRoundStandings( standingId, nextRoundStandingsDtoRequest);
    }

    @Operation(
            summary = "Removing the tournament grid.",
            description = "Gets the tournament grid's ID and deletes it."
    )
    @DeleteMapping(DELETE_STANDINGS)
    public void deleteStanding(@PathVariable(value = "standing_id")Long standingId) {
        standingsService.deleteStandings(standingId);
    }

}
