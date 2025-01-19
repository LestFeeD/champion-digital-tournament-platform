package zChampions.catalogue.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import zChampions.catalogue.controller.ControllerHelper;
import zChampions.catalogue.entity.*;
import zChampions.catalogue.enumsEntities.TypeStandingsEnum;
import zChampions.catalogue.factories.EventFactory;
import zChampions.catalogue.repository.*;
import zChampions.catalogue.requestDto.createRequest.CreateNextRoundStandingsDtoRequest;
import zChampions.catalogue.requestDto.createRequest.MoveUserToNextRoundDtoRequest;
import zChampions.catalogue.requestDto.createRequest.CreateStandingsResultDto;
import zChampions.catalogue.requestDto.updateRequest.StandingsResultUpdateDto;
import zChampions.catalogue.responseDto.StandingsResultResponseDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Transactional
@Service
@RequiredArgsConstructor
public class StandingsService {

    private final EventRepository eventEntityRepository;
    private final EventFactory eventDtoFactory;
    private final ControllerHelper controllerHelper;
    private final OrganizationRepository organizationEntityRepository;
    private final ValidationErrors validationErrors;
    private final AuthenticationService authenticationService;
    private final ApplicationRepository applicationEventRepository;
    private final UserRoleChecker userRoleChecker;
    private final UserRepository userRepository;
    private final StandingsRepository standingsRepository;
    private final ResultStandingsRepository resultStandingsRepository;
    private final TypeStandingsRepository typeStandingsRepository;
    private final StageStandingsRepository stageStandingsRepository;
    private  final static Logger logger = LoggerFactory.getLogger(StandingsService.class);

    public Set<StandingsResultResponseDto> resultStandings(Long eventId, Long standingId) {
        Set<Object[]> results = standingsRepository.findAllAboutStanding(eventId);
        Long typeStandingId = typeStandingsRepository.findTypeStandings(standingId);
        TypeStandings typeStandings = typeStandingsRepository.findById(typeStandingId).orElseThrow(() -> new IllegalArgumentException("TypeStandings not found"));;

            if (typeStandings.getNameType() == TypeStandingsEnum.BEST_TIME) {
                return processBestTime(results);
            } else if (typeStandings.getNameType() == TypeStandingsEnum.OLYMPIC) {
                return processOlympic(results, typeStandings);
            }

        return Collections.emptySet();
    }

    @Transactional
    public Set<StandingsResultResponseDto> createStanding(Long eventId, CreateStandingsResultDto standingsResultCreateDto) {
        EventEntity eventEntity = eventEntityRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        TypeStandings typeStandings = typeStandingsRepository.findById(standingsResultCreateDto.getTypeStandingsId())
                .orElseThrow(() -> new IllegalArgumentException("TypeStandings not found"));

        StageStandings stageStandings = stageStandingsRepository.findById(standingsResultCreateDto.getStageStandingsId())
                .orElseThrow(() -> new IllegalArgumentException("StageStandings not found"));

        Standings standings = new Standings();
        standings.setEventsList(new HashSet<>(Collections.singletonList(eventEntity)));
        standings.setTypeStandings(typeStandings);
        standings.setStartMatchTime(standingsResultCreateDto.getStartMatchTime());
        standings.setEndMatchTime(standingsResultCreateDto.getEndMatchTime());
        standings.setStageStandings(stageStandings);
        standings.setResultStandingsSet(new HashSet<>());

        Set<UserEntity> sportsmen = userRepository.findAllByEventIdAndRole(eventId, "SPORTSMAN");

        if (typeStandings.getNameType() == TypeStandingsEnum.OLYMPIC) {
            List<UserEntity> sportsmenList = new ArrayList<>(sportsmen);
            int groupNumber = 1;

            for (int i = 0; i < sportsmenList.size(); i += 2) {
                ResultStandings resultStandings1 = new ResultStandings();
                resultStandings1.setUser(sportsmenList.get(i));
                resultStandings1.setScore(0);
                resultStandings1.setNumberStandings(groupNumber);
                resultStandings1.setPlayerPosition(1);

                standings.setResultStandingsSet(new HashSet<>(Collections.singletonList(resultStandings1)));
                resultStandings1.setStandings(standings);

                resultStandingsRepository.save(resultStandings1);
                standingsRepository.save(standings);



                if (i + 1 < sportsmenList.size()) {
                    ResultStandings resultStandings2 = new ResultStandings();
                    resultStandings2.setUser(sportsmenList.get(i + 1));
                    resultStandings2.setScore(0);
                    resultStandings2.setNumberStandings(groupNumber);
                    resultStandings2.setPlayerPosition(2);

                    if (resultStandings2.getStandings() == null) {
                        resultStandings2.setStandings(null);
                    }
                    standings.setResultStandingsSet(new HashSet<>(Collections.singletonList(resultStandings2)));
                    resultStandings1.setStandings(standings);
                    resultStandingsRepository.save(resultStandings2);
                    standingsRepository.save(standings);

                }

                groupNumber++;
            }

            eventEntity.setStandingsList(new HashSet<>(Collections.singletonList(standings)));
            standings.setEventsList(new HashSet<>(Collections.singletonList(eventEntity)));

            standingsRepository.save(standings);
            eventEntityRepository.save(eventEntity);

            return standings.getResultStandingsSet().stream()
                    .map(result -> new StandingsResultResponseDto(
                            stageStandings.getNameStage(),
                            stageStandings.getOrderStage(),
                            typeStandings.getNameType(),
                            standingsResultCreateDto.getStartMatchTime(),
                            standingsResultCreateDto.getEndMatchTime(),
                            result.getScore(),
                            null,
                            extractFirstName(result.getUser()),
                            extractLastName(result.getUser())
                    ))
                    .collect(Collectors.toSet());

        } else if (typeStandings.getNameType() == TypeStandingsEnum.BEST_TIME) {

            for (UserEntity sportsman : sportsmen) {
                ResultStandings newResultStandings = new ResultStandings();
                newResultStandings.setUser(sportsman);
                newResultStandings.setScore(0);
                newResultStandings.setStandings(null);
                newResultStandings.setTimeParticipant(LocalTime.of(0, 0, 0));

                standings.setResultStandingsSet(new HashSet<>(Collections.singletonList(newResultStandings)));
                newResultStandings.setStandings(standings);

                resultStandingsRepository.save(newResultStandings);
            }

            eventEntity.setStandingsList(new HashSet<>(Collections.singletonList(standings)));
            standings.setEventsList(new HashSet<>(Collections.singletonList(eventEntity)));

            standingsRepository.save(standings);
            eventEntityRepository.save(eventEntity);

            return standings.getResultStandingsSet().stream()
                    .map(result -> new StandingsResultResponseDto(
                            stageStandings.getNameStage(),
                            stageStandings.getOrderStage(),
                            typeStandings.getNameType(),
                            standingsResultCreateDto.getStartMatchTime(),
                            standingsResultCreateDto.getEndMatchTime(),
                            result.getScore(),
                            result.getTimeParticipant(),
                            extractFirstName(result.getUser()),
                            extractLastName(result.getUser())
                    ))
                    .collect(Collectors.toSet());
        }

        return Collections.emptySet();
    }

    //Creating a new stage in the grid
    public Set<StandingsResultResponseDto> createNextRoundStandings(Long eventId, Long previousStageId, CreateNextRoundStandingsDtoRequest nextRoundStandingsDtoRequest) {

        EventEntity eventEntity = eventEntityRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));


        // Creating a new stage (for example, 1/4 finals)
        StageStandings nextStage = stageStandingsRepository.findById(nextRoundStandingsDtoRequest.getNextStageId()).orElseThrow();


        Long typeStandingsId = typeStandingsRepository.findOlympicTypeStandings();
        TypeStandings typeStandings = typeStandingsRepository.findById(typeStandingsId)
                .orElseThrow(() -> new IllegalArgumentException("TypeStandings not found"));

        Standings newStandings = new Standings();
        newStandings.setStageStandings(nextStage);
        newStandings.setResultStandingsSet(new HashSet<>());
        newStandings.setTypeStandings(typeStandings);
        newStandings.setStartMatchTime(nextRoundStandingsDtoRequest.getStartMatchTime());
        newStandings.setEndMatchTime(nextRoundStandingsDtoRequest.getEndMatchTime());

        // Creating empty tables for the next stage
        Set<ResultStandings> nextRoundResultStandings = new HashSet<>();
        for (int i = 0; i < nextRoundStandingsDtoRequest.getNumberOfTables(); i++) {
            ResultStandings resultStandings = new ResultStandings();
            resultStandings.setScore(0);
            resultStandings.setNumberStandings(i + 1);


            nextRoundResultStandings.add(resultStandings);
            resultStandings.setStandings(newStandings);


            for (int position = 1; position <= 2; position++) {
                // Each position is filled with the value: 1 - for the top player, 2 - for the bottom player
                // The positions are still empty, but they are ready for players to fill later
                ResultStandings positionSlot = new ResultStandings();
                positionSlot.setPlayerPosition(position);
                positionSlot.setScore(0);

                resultStandings.setUser(null);
            }
        }

        newStandings.setResultStandingsSet(nextRoundResultStandings);



        resultStandingsRepository.saveAll(nextRoundResultStandings);

        eventEntity.setStandingsList(new HashSet<>(Collections.singletonList(newStandings)));
        newStandings.setEventsList(new HashSet<>(Collections.singletonList(eventEntity)));

        standingsRepository.save(newStandings);
        eventEntityRepository.save(eventEntity);
        // Возвращаем ответ (например, информация о созданных таблицах)
        return nextRoundResultStandings.stream()
                .map(standings -> new StandingsResultResponseDto(
                        nextStage.getNameStage(),
                        standings.getNumberStandings(),
                        typeStandings.getNameType(),
                        null,
                        null,
                        0,
                        null,
                        null,
                        null
                ))
                .collect(Collectors.toSet());
    }

    public void updateResultStandingsUser( Long standingId, StandingsResultUpdateDto standingsResultUpdateDto) {
        Long resultStandingId = resultStandingsRepository.findResultStandings(standingId);
        ResultStandings resultStandings = resultStandingsRepository.findById(resultStandingId).orElseThrow();

        if(standingsResultUpdateDto.getTimeParticipant() != null) {
            resultStandings.setTimeParticipant(standingsResultUpdateDto.getTimeParticipant());
        }
        if(standingsResultUpdateDto.getNumberStandings() != null) {
            resultStandings.setNumberStandings(standingsResultUpdateDto.getNumberStandings());
        }
        if(standingsResultUpdateDto.getScore() != null) {
            resultStandings.setScore(standingsResultUpdateDto.getScore());
        }
        resultStandingsRepository.saveAndFlush(resultStandings);


    }

    public void updateResultStandingsOlympicTypeUser( Long standingId, StandingsResultUpdateDto standingsResultUpdateDto) {
        Long resultStandingId = resultStandingsRepository.findResultStandings(standingId);
        ResultStandings resultStandings = resultStandingsRepository.findById(resultStandingId).orElseThrow();

        if(standingsResultUpdateDto.getNumberStandings() != null) {
            resultStandings.setNumberStandings(standingsResultUpdateDto.getNumberStandings());
        }
        if(standingsResultUpdateDto.getScore() != null) {
            resultStandings.setScore(standingsResultUpdateDto.getScore());
        }

        resultStandingsRepository.saveAndFlush(resultStandings);


    }

    public void moveUserToNextRound(Long eventId, MoveUserToNextRoundDtoRequest moveUserToNextRoundDtoRequest, Long nextStandingId) {

        EventEntity eventEntity = eventEntityRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        Standings currentStanding = standingsRepository.findById(moveUserToNextRoundDtoRequest.getCurrentStandingId())
                .orElseThrow(() -> new IllegalArgumentException("Current standings not found"));

        Long resultStandingId = resultStandingsRepository.findByUserIdAndStandingId(moveUserToNextRoundDtoRequest.getNumberStandings(),nextStandingId);

        ResultStandings resultStandings = resultStandingsRepository.findById(resultStandingId).orElseThrow();

        UserEntity user = userRepository.findById(moveUserToNextRoundDtoRequest.getUserId()).orElseThrow();


        Standings nextStanding = standingsRepository.findById(nextStandingId)
                .orElseThrow(() -> new IllegalArgumentException("Next standings not found"));


        resultStandings.setUser(user);
        resultStandings.setPlayerPosition(moveUserToNextRoundDtoRequest.getPlayerPosition());

        user.setResultStandingsSet(new HashSet<>(Collections.singletonList(resultStandings)));

        userRepository.save(user);
        resultStandingsRepository.save(resultStandings);
        standingsRepository.save(nextStanding);

    }

    public Set<StandingsResultResponseDto> addNewRoundStandings( Long previousStageId, CreateNextRoundStandingsDtoRequest nextRoundStandingsDtoRequest) {

        Standings standings = standingsRepository.findById(previousStageId)
                .orElseThrow(() -> new IllegalArgumentException("Previous stage not found"));

        Set<Integer> existingTableNumbers = standings.getResultStandingsSet().stream()
                .map(ResultStandings::getNumberStandings)
                .collect(Collectors.toSet());

        Set<ResultStandings> nextRoundResultStandings = new HashSet<>();

        for (int i = 0; i < nextRoundStandingsDtoRequest.getNumberOfTables(); i++) {
            int tableNumber = i + 1;
            while (existingTableNumbers.contains(tableNumber)) {
                tableNumber++;
            }

            ResultStandings resultStandings = new ResultStandings();
            resultStandings.setScore(0);
            resultStandings.setNumberStandings(tableNumber);

            resultStandings.setStandings(standings);

            Set<UserEntity> usersInTable = new HashSet<>();

            for (int position = 1; position <= 2; position++) {

                ResultStandings positionSlot = new ResultStandings();
                positionSlot.setPlayerPosition(position);
                positionSlot.setScore(0);
                resultStandings.setUser(null);

            }

            nextRoundResultStandings.add(resultStandings);

            existingTableNumbers.add(tableNumber);
        }

        nextRoundResultStandings.forEach(resultStandingsRepository::save);

        standings.setResultStandingsSet(nextRoundResultStandings);
        standingsRepository.save(standings);

        return nextRoundResultStandings.stream()
                .map(standing -> new StandingsResultResponseDto(
                        null,
                        standing.getNumberStandings(),
                        null,
                        null,
                        null,
                        0,
                        null,
                        null,
                        null
                ))
                .collect(Collectors.toSet());
    }


    public void deleteStandings(Long standingsId){
        controllerHelper.getStandingsOrThrowException(standingsId);
        standingsRepository.deleteById(standingsId);
        ResponseEntity.noContent()
                .build();
    }




    private String extractFirstName(UserEntity user) {
        return user != null ? user.getFirstName() : "";
    }

    private String extractLastName(UserEntity user) {
        return user != null ? user.getLastName() : "";
    }

    private Set<StandingsResultResponseDto> processBestTime(Set<Object[]> results) {
        return results.stream()
                .sorted(Comparator.comparing(row -> (LocalTime) row[6]))
                .flatMap(row -> {
                    @SuppressWarnings("unchecked")
                    List<String> firstNames = (List<String>) row[7];
                    List<String> lastNames = (List<String>) row[8];

                    return IntStream.range(0, Math.min(firstNames.size(), lastNames.size()))
                            .mapToObj(i -> new StandingsResultResponseDto(
                                    (String) row[0],
                                    (Integer) row[1],
                                    (TypeStandingsEnum) row[2],
                                    (LocalDate) row[3],
                                    (LocalDate) row[4],
                                    (Integer) row[5],
                                    (LocalTime) row[6],
                                    firstNames.get(i),
                                    lastNames.get(i)
                            ));
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));


    }

    private Set<StandingsResultResponseDto> processOlympic(Set<Object[]> results, TypeStandings typeStandings) {
        return results.stream()
                .sorted(Comparator.comparing(row -> (Integer) row[0]))
                .flatMap(row -> {
                    @SuppressWarnings("unchecked")
                    List<String> firstNames = (List<String>) row[7];
                    List<String> lastNames = (List<String>) row[8];

                    return IntStream.range(0, Math.min(firstNames.size(), lastNames.size()))
                            .mapToObj(i -> new StandingsResultResponseDto(
                                    (String) row[0],
                                    (Integer) row[1],
                                    typeStandings.getNameType(),
                                    (LocalDate) row[3],
                                    (LocalDate) row[4],
                                    (Integer) row[5],
                                    (LocalTime ) row[6],
                                    firstNames.get(i),
                                    lastNames.get(i)
                            ));
                })
                .collect(Collectors.toSet());
    }

}
