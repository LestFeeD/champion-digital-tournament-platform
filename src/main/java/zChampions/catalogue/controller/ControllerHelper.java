package zChampions.catalogue.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import zChampions.catalogue.entity.*;
import zChampions.catalogue.exceptions.NotFoundException;
import zChampions.catalogue.repository.*;
import zChampions.catalogue.service.MyUserDetailsService;

@Tag(name = "class working to find entities")
@RequiredArgsConstructor
@Component
@Transactional
public class ControllerHelper {

    private final EventRepository eventEntityRepository;
    private final UserRepository userEntityRepository;
    private final ApplicationRepository applicationEventRepository;
    private final OrganizationRepository organizationEntityRepository;
    private final ApplicationOrganizationRepository applicationOrganizationRepository;
    private final AthleteProfileRepository athleteProfileRepository;
    private final CoachProfileRepository coachProfileRepository;
    private final JudgeProfileRepository judgeProfileRepository;
    private final StandingsRepository standingsRepository;
    private static final Logger logger = LoggerFactory.getLogger(ControllerHelper.class);



    public EventEntity getEventOrThrowException(Long eventId) {

        return eventEntityRepository
                .findById(eventId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "There is no event under the ID \"%s\".",
                                        eventId
                                )
                        )
                );
    }

    public UserEntity getUserOrThrowException(Long userId) {

        return userEntityRepository
                .findById(userId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "There is no user under the ID \"%s\".",
                                        userId
                                )
                        )
                );
    }

    public ApplicationEvent getApplicationEventOrThrowException(Long applicationId) {

        return applicationEventRepository
                .findById(applicationId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "The application under the ID \"%s\" does not exist.",
                                        applicationId
                                )
                        )
                );
    }

    public OrganizationEntity getOrganizationOrThrowException(Long organizationId) {
        return organizationEntityRepository
                .findById(organizationId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "The organization under the ID \"%s\" does not exist.",
                                        organizationId
                                )
                        )
                );
    }

    public ApplicationOrganization getApplicationOrganizationOrThrowException(Long applicationOrganizationId) {

        return applicationOrganizationRepository
                .findById(applicationOrganizationId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "The application under the ID \"%s\" does not exist.",
                                        applicationOrganizationId
                                )
                        )
                );
    }

    public AthleteProfile getAthleteProfileOrThrowException(Long athleteProfileId) {

        return athleteProfileRepository
                .findById(athleteProfileId)
                .orElseThrow(() -> {
                        logger.error("AthleteProfile with ID {} not found", athleteProfileId);
                    return new NotFoundException(
                            String.format(
                                    "The athletic profile under the ID \"%s\" does not exist.",
                                    athleteProfileId
                            )
                    );
                });
    }

    public CoachProfile getCoachProfileOrThrowException(Long coachProfileId) {

        return coachProfileRepository
                .findById(coachProfileId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "The coach's profile does not exist under the ID \"%s\".",
                                        coachProfileId
                                )
                        )
                );
    }

    public JudgeProfile getJudgeProfileOrThrowException(Long judgeProfile) {

        return judgeProfileRepository
                .findById(judgeProfile)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "The profile of the judge under the ID \"%s\" does not exist.",
                                        judgeProfile
                                )
                        )
                );
    }

    public Standings getStandingsOrThrowException(Long standingId) {

        return standingsRepository
                .findById(standingId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "There is no tournament grid under the ID \"%s\".",
                                        standingId
                                )
                        )
                );
    }


    }
