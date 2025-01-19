package zChampions.catalogue.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zChampions.catalogue.controller.ControllerHelper;
import zChampions.catalogue.entity.*;
import zChampions.catalogue.enumsEntities.TypeStandingsEnum;
import zChampions.catalogue.repository.*;
import zChampions.catalogue.requestDto.createRequest.CreateNextRoundStandingsDtoRequest;
import zChampions.catalogue.requestDto.createRequest.CreateStandingsResultDto;
import zChampions.catalogue.requestDto.createRequest.MoveUserToNextRoundDtoRequest;
import zChampions.catalogue.requestDto.updateRequest.StandingsResultUpdateDto;
import zChampions.catalogue.responseDto.StandingsResultResponseDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StandingsServiceTest {

    @Mock
    private  EventRepository eventEntityRepository;
    @Mock
    private  ControllerHelper controllerHelper;
    @Mock
    private  UserRepository userRepository;
    @Mock
    private  StandingsRepository standingsRepository;
    @Mock
    private  ResultStandingsRepository resultStandingsRepository;
    @Mock
    private  TypeStandingsRepository typeStandingsRepository;
    @Mock
    private  StageStandingsRepository stageStandingsRepository;
    @InjectMocks
    private StandingsService standingsService;

    @Test
    void resultStandings_showResultStandingsWithTypeBestTime_returnStandingsResultResponseDtoWithBestTime() {
        Long eventId = 1L;
        Long standingId = 1L;
        Long typeStandingId = 1L;
        Set<Object[]> results = new HashSet<>();
        TypeStandings typeStandings = new TypeStandings();
        typeStandings.setTypeStandingId(typeStandingId);
        typeStandings.setNameType(TypeStandingsEnum.BEST_TIME);

        when(standingsRepository.findAllAboutStanding(eventId)).thenReturn(results);
        when(typeStandingsRepository.findTypeStandings(standingId)).thenReturn(typeStandingId);
        when(typeStandingsRepository.findById(typeStandingId)).thenReturn(Optional.of(typeStandings));

        Set<StandingsResultResponseDto> result = standingsService.resultStandings(eventId, standingId);

    }

    @Test
    void resultStandings_showResultStandingsWithTypeOlympic_returnStandingsResultResponseDtoWithOlympic() {
        Long eventId = 1L;
        Long standingId = 1L;
        Long typeStandingId = 1L;
        Set<Object[]> results = new HashSet<>();
        TypeStandings typeStandings = new TypeStandings();
        typeStandings.setTypeStandingId(typeStandingId);
        typeStandings.setNameType(TypeStandingsEnum.OLYMPIC);

        when(standingsRepository.findAllAboutStanding(eventId)).thenReturn(results);
        when(typeStandingsRepository.findTypeStandings(standingId)).thenReturn(typeStandingId);
        when(typeStandingsRepository.findById(typeStandingId)).thenReturn(Optional.of(typeStandings));

        Set<StandingsResultResponseDto> result = standingsService.resultStandings(eventId, standingId);

    }

    @Test
    void createStanding_createStandingsForEventWithTypeBestTime_returnStandingsResultResponseDto() {
        Long eventId = 1L;
        Long stageStandingId = 1L;
        Long typeStandingId = 1L;
        CreateStandingsResultDto standingsResultCreateDto =  new CreateStandingsResultDto();
        standingsResultCreateDto.setTypeStandingsId(typeStandingId);
        standingsResultCreateDto.setStageStandingsId(stageStandingId);

        TypeStandings typeStandings = new TypeStandings();
        typeStandings.setNameType(TypeStandingsEnum.BEST_TIME);

        EventEntity event = new EventEntity();

        StageStandings stageStandings = new StageStandings();

        Set<UserEntity> sportsmen = new HashSet<>();
        UserEntity user = new UserEntity();
        sportsmen.add(user);

        when(eventEntityRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(typeStandingsRepository.findById(standingsResultCreateDto.getTypeStandingsId())).thenReturn(Optional.of(typeStandings));
        when(stageStandingsRepository.findById(standingsResultCreateDto.getStageStandingsId())).thenReturn(Optional.of(stageStandings));
        when(userRepository.findAllByEventIdAndRole(eventId, "SPORTSMAN")).thenReturn(sportsmen);

        Set<StandingsResultResponseDto> result = standingsService.createStanding(eventId, standingsResultCreateDto);

        verify(resultStandingsRepository, times(1)).save(any(ResultStandings.class));
        verify(standingsRepository, times(1)).save(any(Standings.class));

    }

    @Test
    void createStanding_createStandingsForEventWithTypeOlympic_returnStandingsResultResponseDto() {
        Long eventId = 1L;
        Long stageStandingId = 1L;
        Long typeStandingId = 1L;
        CreateStandingsResultDto standingsResultCreateDto =  new CreateStandingsResultDto();
        standingsResultCreateDto.setTypeStandingsId(typeStandingId);
        standingsResultCreateDto.setStageStandingsId(stageStandingId);

        TypeStandings typeStandings = new TypeStandings();
        typeStandings.setNameType(TypeStandingsEnum.OLYMPIC);

        EventEntity event = new EventEntity();

        StageStandings stageStandings = new StageStandings();

        Set<UserEntity> sportsmen = new HashSet<>();
        UserEntity user = new UserEntity();
        UserEntity anotherUser = new UserEntity();
        sportsmen.add(anotherUser);
        sportsmen.add(user);

        when(eventEntityRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(typeStandingsRepository.findById(standingsResultCreateDto.getTypeStandingsId())).thenReturn(Optional.of(typeStandings));
        when(stageStandingsRepository.findById(standingsResultCreateDto.getStageStandingsId())).thenReturn(Optional.of(stageStandings));
        when(userRepository.findAllByEventIdAndRole(eventId, "SPORTSMAN")).thenReturn(sportsmen);

        Set<StandingsResultResponseDto> result = standingsService.createStanding(eventId, standingsResultCreateDto);

        verify(resultStandingsRepository, times(2)).save(any(ResultStandings.class));
        verify(standingsRepository, times(3)).save(any(Standings.class));

    }

    @Test
    void createNextRoundStandings_createNextRoundStandingsForStanding_returnStandingsResultResponseDto() {

        Long eventId = 1L;
        Long stageStandingId = 1L;
        Long typeStandingId = 1L;
        Long previousStageId = 1L;
        CreateNextRoundStandingsDtoRequest standingsDtoRequest =  new CreateNextRoundStandingsDtoRequest();
        standingsDtoRequest.setNextStageId(typeStandingId);

        TypeStandings typeStandings = new TypeStandings();

        EventEntity event = new EventEntity();

        StageStandings stageStandings = new StageStandings();

        Set<ResultStandings> nextRoundResultStandings = new HashSet<>();


        when(eventEntityRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(typeStandingsRepository.findOlympicTypeStandings()).thenReturn(typeStandingId);
        when(typeStandingsRepository.findById(typeStandingId)).thenReturn(Optional.of(typeStandings));
        when(stageStandingsRepository.findById(stageStandingId)).thenReturn(Optional.of(stageStandings));

        Set<StandingsResultResponseDto> result = standingsService.createNextRoundStandings(eventId,previousStageId,  standingsDtoRequest);

        verify(resultStandingsRepository, times(1)).saveAll(nextRoundResultStandings);
        verify(standingsRepository, times(1)).save(any(Standings.class));
        verify(eventEntityRepository, times(1)).save(event);

    }

    @Test
    void updateResultStandingsUser_createNextRoundStandingsForStanding() {
        Long resultStandingId = 1L;
        Long standingId = 1L;
        ResultStandings resultStandings = new ResultStandings();
        StandingsResultUpdateDto standingsResultUpdateDto = new StandingsResultUpdateDto();
        standingsResultUpdateDto.setScore(12);

        when(resultStandingsRepository.findResultStandings(resultStandingId)).thenReturn(resultStandingId);
        when(resultStandingsRepository.findById(resultStandingId)).thenReturn(Optional.of(resultStandings));

        standingsService.updateResultStandingsUser(standingId, standingsResultUpdateDto);

        assertEquals(12, standingsResultUpdateDto.getScore());

        verify(resultStandingsRepository, times(1)).saveAndFlush(resultStandings);

    }

    @Test
    void updateResultStandingsOlympicTypeUser_createNextRoundStandingsForStanding() {
        Long resultStandingId = 1L;
        Long standingId = 1L;
        ResultStandings resultStandings = new ResultStandings();
        StandingsResultUpdateDto standingsResultUpdateDto = new StandingsResultUpdateDto();
        standingsResultUpdateDto.setScore(12);

        when(resultStandingsRepository.findResultStandings(resultStandingId)).thenReturn(resultStandingId);
        when(resultStandingsRepository.findById(resultStandingId)).thenReturn(Optional.of(resultStandings));

        standingsService.updateResultStandingsUser(standingId, standingsResultUpdateDto);

        assertEquals(12, standingsResultUpdateDto.getScore());

        verify(resultStandingsRepository, times(1)).saveAndFlush(resultStandings);

    }


    @Test
    void moveUserToNextRound_moveUserToNextRoundFromOneStandingToAnotherStanding() {
        Long eventId = 1L;
        Long nextStandingId = 1L;
        Long currentStandingId = 1L;
        Long resultStandingId = 1L;
        Long userId = 1L;

        Standings nextStanding = new Standings();

        EventEntity event = new EventEntity();

        UserEntity user = new UserEntity();
        Standings currentStanding = new Standings();

        ResultStandings resultStandings = new ResultStandings();

        MoveUserToNextRoundDtoRequest moveUserToNextRoundDtoRequest = new MoveUserToNextRoundDtoRequest();
        moveUserToNextRoundDtoRequest.setCurrentStandingId(currentStandingId);
        moveUserToNextRoundDtoRequest.setUserId(userId);
        moveUserToNextRoundDtoRequest.setNumberStandings(2);

        when(eventEntityRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(standingsRepository.findById(moveUserToNextRoundDtoRequest.getCurrentStandingId())).thenReturn(Optional.of(currentStanding));
        when(resultStandingsRepository.findByUserIdAndStandingId(moveUserToNextRoundDtoRequest.getNumberStandings(), nextStandingId)).thenReturn(resultStandingId);
        when(resultStandingsRepository.findById(resultStandingId)).thenReturn(Optional.of(resultStandings));
        when(userRepository.findById(moveUserToNextRoundDtoRequest.getUserId())).thenReturn(Optional.of(user));
        when(standingsRepository.findById(nextStandingId)).thenReturn(Optional.of(nextStanding));

        standingsService.moveUserToNextRound(eventId, moveUserToNextRoundDtoRequest, nextStandingId);

        verify(resultStandingsRepository, times(1)).save(resultStandings);
        verify(standingsRepository, times(1)).save(nextStanding);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void addNewRoundStandings_addNewRoundStandings() {
        Long previousStageId = 1L;

        Standings standings = new Standings();
        standings.setResultStandingsSet(new HashSet<>());

        ResultStandings resultStandings = new ResultStandings();
        resultStandings.setUser(new UserEntity());
        standings.getResultStandingsSet().add(resultStandings);

        CreateNextRoundStandingsDtoRequest nextRoundStandingsDtoRequest = new CreateNextRoundStandingsDtoRequest();
        nextRoundStandingsDtoRequest.setNumberOfTables(2);

        when(standingsRepository.findById(previousStageId)).thenReturn(Optional.of(standings));

        standingsService.addNewRoundStandings(previousStageId,nextRoundStandingsDtoRequest);

        verify(standingsRepository, times(1)).save(standings);


    }

    @Test
    void deleteStandings_deleteStandingsById() {
        Long standingsId = 1L;

        when(controllerHelper.getStandingsOrThrowException(standingsId)).thenReturn(ArgumentMatchers.any(Standings.class));

        standingsService.deleteStandings(standingsId);

        verify(controllerHelper, times(1)).getStandingsOrThrowException(standingsId);
        verify(standingsRepository, times(1)).deleteById(standingsId);


    }

    /*@Test
    void testProcessBestTime() {
        // Подготовка тестовых данных (Object[])
        Set<Object[]> results = new HashSet<>();

        Object[] row1 = new Object[]{
                "Stage 1", // stage name
                1, // stage order
                TypeStandingsEnum.BEST_TIME, // type
                LocalDate.of(2024, 12, 17), // start date
                LocalDate.of(2024, 12, 18), // end date
                10, // score
                LocalTime.of(10, 0), // time
                Arrays.asList("John"), // first names
                Arrays.asList("Doe") // last names
        };

        Object[] row2 = new Object[]{
                "Stage 2", // stage name
                2, // stage order
                TypeStandingsEnum.BEST_TIME, // type
                LocalDate.of(2024, 12, 19), // start date
                LocalDate.of(2024, 12, 20), // end date
                20, // score
                LocalTime.of(9, 0), // time
                Arrays.asList("Jane"), // first names
                Arrays.asList("Smith") // last names
        };

        Object[] row3 = new Object[]{
                "Stage 3", // stage name
                3, // stage order
                TypeStandingsEnum.BEST_TIME, // type
                LocalDate.of(2024, 12, 21), // start date
                LocalDate.of(2024, 12, 22), // end date
                30, // score
                LocalTime.of(11, 0), // time
                Arrays.asList("Alice"), // first names
                Arrays.asList("Johnson") // last names
        };

        results.add(row1);
        results.add(row2);
        results.add(row3);

        // Вызов метода
        Set<StandingsResultResponseDto> result = standingsService.processBestTime(results);

        // Проверка сортировки (по времени участников)
        List<StandingsResultResponseDto> sortedResults = new ArrayList<>(result);
        assertEquals(LocalTime.of(9, 0), sortedResults.get(0).getTimeParticipant()); // Jane Smith (время 09:00)
        assertEquals(LocalTime.of(10, 0), sortedResults.get(1).getTimeParticipant()); // John Doe (время 10:00)
        assertEquals(LocalTime.of(11, 0), sortedResults.get(2).getTimeParticipant()); // Alice Johnson (время 11:00)

        // Проверка данных в результатах
        StandingsResultResponseDto dto1 = sortedResults.get(0);
        assertEquals("Stage 2", dto1.getNameStage());
        assertEquals(2, dto1.getScore());
        assertEquals(TypeStandingsEnum.BEST_TIME, dto1.getNameType());
        assertEquals("Jane", dto1.getFirstName());
        assertEquals("Smith", dto1.getLastName());

        StandingsResultResponseDto dto2 = sortedResults.get(1);
        assertEquals("Stage 1", dto2.getNameStage());
        assertEquals(1, dto2.getScore());
        assertEquals(TypeStandingsEnum.BEST_TIME, dto2.getNameType());
        assertEquals("John", dto2.getFirstName());
        assertEquals("Doe", dto2.getLastName());

        StandingsResultResponseDto dto3 = sortedResults.get(2);
        assertEquals("Stage 3", dto3.getNameStage());
        assertEquals(3, dto3.getScore());
        assertEquals(TypeStandingsEnum.BEST_TIME, dto3.getNameType());
        assertEquals("Alice", dto3.getFirstName());
        assertEquals("Johnson", dto3.getLastName());
    }*/
}



