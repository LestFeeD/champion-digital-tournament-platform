package zChampions.catalogue.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import zChampions.catalogue.controller.ControllerHelper;
import zChampions.catalogue.entity.*;
import zChampions.catalogue.exceptions.RoleNotAllowedException;
import zChampions.catalogue.factories.AllOrganizationEntityDtoFactory;
import zChampions.catalogue.repository.ApplicationOrganizationRepository;
import zChampions.catalogue.requestDto.*;
import zChampions.catalogue.enumsEntities.RoleSport;
import zChampions.catalogue.exceptions.BadRequestException;
import zChampions.catalogue.exceptions.NotFoundException;
import zChampions.catalogue.factories.OrganizationEntityDtoFactory;
import zChampions.catalogue.repository.OrganizationRepository;
import zChampions.catalogue.repository.UserRepository;
import zChampions.catalogue.repository.UserOrganizationRoleRepository;
import zChampions.catalogue.requestDto.createRequest.CreateOrganizationRequestDto;
import zChampions.catalogue.requestDto.createRequest.CreateOrganizationDomainRequestDto;
import zChampions.catalogue.requestDto.updateRequest.UpdateOrganizationContactSettingsRequestDto;
import zChampions.catalogue.requestDto.updateRequest.UpdateOrganizationGeneralSettingsRequestDto;
import zChampions.catalogue.responseDto.AllOrganizationResponseDto;
import zChampions.catalogue.responseDto.OrganizationCreateResponseDto;
import zChampions.catalogue.responseDto.UserInOrganizationResponseDto;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class OrganizationEntityService {

    private final OrganizationRepository organizationEntityRepository;
    private final OrganizationEntityDtoFactory organizationEntityDtoFactory;
    private final AllOrganizationEntityDtoFactory allOrganizationEntityDtoFactory;
    private final ControllerHelper controllerHelper;
    private final UserRepository userEntityRepository;
    private final UserOrganizationRoleRepository userOrganizationRoleRepository;
    private final ValidationErrors validationErrors;
    private final AuthenticationService authenticationService;
    private final ApplicationOrganizationRepository applicationOrganizationRepository;
    private final UserRoleChecker userRoleChecker;
    private static final Logger logger = LoggerFactory.getLogger(OrganizationEntityService.class);


    public List<AllOrganizationResponseDto> findOfAllOrganizations() throws NotFoundException {

        List<OrganizationEntity> listOrganization = organizationEntityRepository.findAll();
        if (listOrganization.isEmpty()) {
            logger.warn("No events were found for the findAllEvent query");
            return new ArrayList<>();
        } else {
            logger.info("Found {} events", listOrganization.size());
            return listOrganization.stream()
                    .map(allOrganizationEntityDtoFactory::makeOrganizations)
                    .collect(Collectors.toList());
        }
    }

    public List<AllOrganizationResponseDto> findByParameters(UpdateOrganizationFetchRequestDto requestDto) {
        List<OrganizationEntity> listOrganization;

        logger.info("Title: {}", requestDto.getTitle());
        logger.info("Country: {}", requestDto.getCountry());
        logger.info("Region: {}", requestDto.getRegion());

        if (requestDto.getTitle() != null && requestDto.getCountry() != null && requestDto.getRegion() != null) {
            listOrganization =  organizationEntityRepository.findByCountryAndRegionAndTitle( requestDto.getCountry(), requestDto.getRegion(), requestDto.getTitle());
        }  else if(requestDto.getTitle() != null && requestDto.getRegion() != null) {
        listOrganization = organizationEntityRepository.findByTitleAndRegion(requestDto.getTitle(), requestDto.getRegion());
        }  else if(requestDto.getRegion() != null && requestDto.getCountry() != null) {
            listOrganization = organizationEntityRepository.findByRegionAndCountry(requestDto.getRegion(), requestDto.getCountry());
        }  else if(requestDto.getTitle() != null && requestDto.getCountry() != null) {
            listOrganization = organizationEntityRepository.findByTitleAndCountry(requestDto.getTitle(), requestDto.getCountry());
        } else if (requestDto.getTitle() != null) {
            listOrganization =  organizationEntityRepository.findByTitle(requestDto.getTitle());
        } else if (requestDto.getCountry() != null) {
            listOrganization =  organizationEntityRepository.findByCountry(requestDto.getCountry());
        } else if (requestDto.getRegion() != null) {
            listOrganization = organizationEntityRepository.findByRegion(requestDto.getRegion());
        }  else {
            logger.warn("There are no organizations with the specified parameters.");
            listOrganization = new ArrayList<>();
        }

        logger.info("Found organizations: {}", listOrganization);
        return listOrganization.stream()
                .map(allOrganizationEntityDtoFactory::makeOrganizations)
                .collect(Collectors.toList());
    }

    public List<AllOrganizationResponseDto> findOrganizationByUser(Long userId) throws NotFoundException {
        List<OrganizationEntity> listOrganization = organizationEntityRepository.findAllByUsers_UserId(userId);

        if (listOrganization == null ||listOrganization.isEmpty()) {
            logger.warn("The user has an ID: {},  there are no organizations", userId);
            listOrganization = new ArrayList<>();
        }

        return listOrganization.stream()
                .map(allOrganizationEntityDtoFactory::makeOrganizations)
                .collect(Collectors.toList());
    }

    public OrganizationCreateResponseDto createOrganization(@Valid @RequestBody CreateOrganizationRequestDto organizationDtoRequest, BindingResult bindingResult) throws BadRequestException {
        Long userId = authenticationService.getCurrentUserId();

        UserEntity user = userEntityRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Method createOrganization: User with ID {} not found",userId );
                    return new NotFoundException("User not found");
                });


        logger.info("Creating organization with parameters: title {}, country {}, region {}, city {}, email {},  kindOfSport {}, phoneNumber {}, user {}",
                organizationDtoRequest.getTitle(), organizationDtoRequest.getCountry(), organizationDtoRequest.getRegion(), organizationDtoRequest.getCity(),
                organizationDtoRequest.getEmail(), organizationDtoRequest.getKindOfSport(), organizationDtoRequest.getPhoneNumber(), userId);


        if(bindingResult.hasErrors() ) {
            String result = validationErrors.getValidationErrors(bindingResult);
            logger.error("Validation errors occurred while creating organization: {}", result);
            throw new BadRequestException(result);

        }

        OrganizationEntity organization = OrganizationEntity.builder()
                .title(organizationDtoRequest.getTitle())
                .country(organizationDtoRequest.getCountry())
                .kindOfSport(organizationDtoRequest.getKindOfSport())
                .region(organizationDtoRequest.getRegion())
                .city(organizationDtoRequest.getCity())
                .email(organizationDtoRequest.getEmail())
                .phoneNumber(organizationDtoRequest.getPhoneNumber())
                .users(new HashSet<>(Collections.singletonList(user)))
                .build();
         organizationEntityRepository.save(organization);

        user.getOrganizationEntityList().add(organization);

        UserOrganizationRole userRole = UserOrganizationRole.builder()
                .user(user)
                .organization(organization)
                .role(RoleSport.ORGANIZER)
                .build();
        userOrganizationRoleRepository.save(userRole);

        user.setUserOrganizationRoles(new ArrayList<>(Collections.singletonList(userRole)));
        userEntityRepository.save(user);

        logger.info("Successfully saved organization with ID: {}", organization.getOrganizationId());
        return organizationEntityDtoFactory.makeOrganization(organization);
    }

    public OrganizationCreateResponseDto createDomainOrganization(Long organizationId,  CreateOrganizationDomainRequestDto organizationDtoRequest) throws RoleNotAllowedException {

        Long userId = authenticationService.getCurrentUserId();

        logger.info("Creating Domain organization with parameters: linkWebsite {}, userId {}", organizationDtoRequest.getLinkWebsite(), userId);

        UserEntity user = userEntityRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Method createDomainOrganization: User with ID {} not found",userId );
                    return new NotFoundException("User not found");
                });


        OrganizationEntity organization = organizationEntityRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("Мероприятие не найдено"));




        if (!userRoleChecker.isOrganizer(userId, organization)) {
            logger.warn("The organization under the id {} does not belong to the user under the id {} in the method: createDomainOrganization ", organizationId, userId);
            throw new RoleNotAllowedException("The organization does not belong to the user");
        }

        organization.setLinkWebsite(organizationDtoRequest.getLinkWebsite());


        organization = organizationEntityRepository.saveAndFlush(organization);
        logger.info("Successfully saved domain organization with ID: {}", organization.getOrganizationId());
        return organizationEntityDtoFactory.makeOrganization(organization);
    }

    public OrganizationCreateResponseDto updateGeneralSettingsOrganization(Long organizationId, @Valid UpdateOrganizationGeneralSettingsRequestDto organizationDtoRequest,
                                                                   BindingResult bindingResult) throws BadRequestException, AccessDeniedException, RoleNotAllowedException {

        Long userId = authenticationService.getCurrentUserId();

        UserEntity user = userEntityRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Method updateGeneralSettingsOrganization: User with ID {} not found",userId );
                    return new NotFoundException("User not found");
                });

        logger.info("Updating organization with parameters: title {}, country {}, region {}, city {},  kindOfSport {}, userId {}",
                organizationDtoRequest.getTitle(), organizationDtoRequest.getCountry(), organizationDtoRequest.getRegion(), organizationDtoRequest.getCity(),
                organizationDtoRequest.getKindOfSport(), userId);
        if(bindingResult.hasErrors() ) {
            String result = validationErrors.getValidationErrors(bindingResult);
            logger.error("Validation errors occurred while update general settings organization: {}", result);
            throw new BadRequestException(result);

        }

        OrganizationEntity organization = controllerHelper.getOrganizationOrThrowException(organizationId);

        if (!userRoleChecker.isOrganizer(userId, organization)) {
            logger.warn("The organization under the id {} does not belong to the user under the id {} in the method: updateGeneralSettingsOrganization ", organizationId, userId);
            throw new RoleNotAllowedException("The organization does not belong to the user");
        }

        if (organizationDtoRequest.getTitle() != null) {
            organization.setTitle(organizationDtoRequest.getTitle());
        }
        if (organizationDtoRequest.getCountry() != null) {
            organization.setCountry(organizationDtoRequest.getCountry());
        } if (organizationDtoRequest.getRegion() != null) {
            organization.setRegion(organizationDtoRequest.getRegion());
        }
        if (organizationDtoRequest.getCity() != null) {
            organization.setCity(organizationDtoRequest.getCity());
        } if (organizationDtoRequest.getKindOfSport() != null) {
            organization.setKindOfSport(organizationDtoRequest.getKindOfSport());
        }

        organization = organizationEntityRepository.saveAndFlush(organization);

        logger.info("Successfully update general settings organization with ID: {}", organizationId);
        return organizationEntityDtoFactory.makeOrganization(organization);

    }

    public OrganizationCreateResponseDto updateContactSettingsOrganization(Long organizationId, @Valid UpdateOrganizationContactSettingsRequestDto organizationContactSettings) throws BadRequestException, AccessDeniedException, RoleNotAllowedException {
        Long userId = authenticationService.getCurrentUserId();

        UserEntity user = userEntityRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Method updateContactSettingsOrganization: User with ID {} not found",userId );
                    return new NotFoundException("User not found");
                });

        logger.info("Updating organization with parameters: email {}, phoneNumber {}, officialWebsite {}, userId {}",
                organizationContactSettings.getEmail(), organizationContactSettings.getPhoneNumber(), organizationContactSettings.getOfficialWebsite(), userId);
        OrganizationEntity organization = controllerHelper.getOrganizationOrThrowException(organizationId);


        if (!userRoleChecker.isOrganizer(userId, organization)) {
            logger.warn("The organization under the id {} does not belong to the user under the id {} in the method: updateContactSettingsOrganization ", organizationId, userId);
            throw new RoleNotAllowedException("The organization does not belong to the user");
        }

        if (organizationContactSettings.getEmail() != null) {
            organization.setEmail(organizationContactSettings.getEmail());
        } if (organizationContactSettings.getPhoneNumber() != null) {
            organization.setTitle(organizationContactSettings.getPhoneNumber());
        }
        if (organizationContactSettings.getOfficialWebsite() != null) {
            organization.setOfficialWebsite(organizationContactSettings.getOfficialWebsite());
        }

        OrganizationEntity savedOrganization =  organizationEntityRepository.saveAndFlush(organization);

        logger.info("Successfully update contact settings organization with ID: {}", savedOrganization.getOrganizationId());
        return organizationEntityDtoFactory.makeOrganization(savedOrganization);

    }

    public List<UserInOrganizationResponseDto> getSportsmenUsersInOrganization(Long organizationId) throws BadRequestException {


        OrganizationEntity organization = controllerHelper.getOrganizationOrThrowException(organizationId);

        List<UserInOrganizationResponseDto> sportsmen = organization.getUsers().stream()
                .filter(user -> user.getUserOrganizationRoles() != null && user.getUserOrganizationRoles().stream()
                        .anyMatch(role -> role.getRole() == RoleSport.SPORTSMAN))
                .map(user -> {
                    // Получаем первую роль SPORTSMAN из списка ролей для DTO
                    RoleSport roleSport = user.getUserOrganizationRoles().stream()
                            .map(UserOrganizationRole::getRole)
                            .filter(role -> role == RoleSport.SPORTSMAN)
                            .findFirst()
                            .orElse(null);

                    return new UserInOrganizationResponseDto(user.getFirstName(), roleSport); // Создаем DTO
                })
                .collect(Collectors.toList());

        if (sportsmen.isEmpty()) {
            logger.warn("No sportsmen found in organization with ID: {}", organizationId);
        }

        return sportsmen;

    }

    public List<UserInOrganizationResponseDto> getCoachUsersInOrganization(Long organizationId) throws BadRequestException {
        OrganizationEntity organization = controllerHelper.getOrganizationOrThrowException(organizationId);

        List<UserInOrganizationResponseDto> coaches =  organization.getUsers().stream()
                .filter(user ->  user.getUserOrganizationRoles() != null && user.getUserOrganizationRoles().stream()
                        .anyMatch(role -> role.getRole() == RoleSport.COACH))
                .map(user -> {
                    // Получаем первую роль SPORTSMAN из списка ролей для DTO
                    RoleSport roleSport = user.getUserOrganizationRoles().stream()
                            .map(UserOrganizationRole::getRole)
                            .filter(role -> role == RoleSport.COACH)
                            .findFirst()
                            .orElse(null); // Если нет, то null

                    return new UserInOrganizationResponseDto(user.getFirstName(), roleSport); // Создаем DTO
                })
                .collect(Collectors.toList());


        if (coaches.isEmpty()) {
            logger.warn("No coach found in organization with ID: {}", organizationId);
        }

        return coaches;
    }

    public void deleteUserInOrganization(Long organizationId, Long userId) throws RoleNotAllowedException {
        Long organizerId = authenticationService.getCurrentUserId();

        UserEntity user = userEntityRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Method deleteUserInOrganization: User with ID {} not found",userId );
                    return new NotFoundException("User not found");
                });

        OrganizationEntity organization = controllerHelper.getOrganizationOrThrowException(organizationId);

        if (!userRoleChecker.isOrganizer(userId, organization)) {
            logger.error("Insufficient rights for organizer {} to modify organization {}", organizerId, organization.getOrganizationId());
            throw new RoleNotAllowedException("Organizer is not authorized to remove users from this organization");
        }

        // Находим заявку, которая принадлежит пользователю
        ApplicationOrganization applicationOrganization = organization.getApplicationOrganizationSet().stream()
                .filter(app -> app.getUser() != null && app.getUser().getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> {
                    logger.warn("Application not found for user: {}", userId);
                    throw new NotFoundException("Application not found for user " + userId + " in organization " + organizationId);
                });


        organization.getApplicationOrganizationSet().remove(applicationOrganization);
        applicationOrganizationRepository.delete(applicationOrganization);
        organization.getUsers().removeIf(app -> app.getUserId().equals(userId));

        organizationEntityRepository.save(organization);

    }




    public void deleteOrganization(Long organizationId) {
        controllerHelper.getOrganizationOrThrowException(organizationId);
        logger.info("The organisation under the id: {} has been deleted", organizationId);
        organizationEntityRepository.deleteById(organizationId);
        ResponseEntity.noContent()
                .build();
    }

   private boolean isUserLinkedToOrganization(OrganizationEntity organization, UserEntity user) {
      return organization.getUsers().stream()
               .anyMatch(u -> u.getUserId().equals(user.getUserId()));
   }



}
