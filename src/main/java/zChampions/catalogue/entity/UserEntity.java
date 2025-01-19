package zChampions.catalogue.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    private String firstName;

    private String lastName;

    private String patronymic;

    private String gender;

    private String email;

    private String password;

    private String information;

    private LocalDate dateOfBirth;

    private Double height;

    private Double weight;

    private String country;

    private String region;

    private String city;
    private Boolean locked = false;
    private Boolean enabled = false;


    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ApplicationEvent> applicationEventList;



    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "user_organization",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "organization_id")
    )
    @JsonIgnore
    private Set<OrganizationEntity> organizationEntityList;


    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserOrganizationRole> userOrganizationRoles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserEventRole> userEventRoles;


    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ApplicationOrganization> applicationOrganizations;


    @ManyToMany(mappedBy = "userList", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<EventEntity> eventList;


    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<UserRoleSystem> userRoleSystemEntities = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    @JsonIgnore
    public List<ConfirmationToken> confirmationTokens;


    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<AthleteProfile> athleteProfiles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<CoachProfile> coachProfiles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<JudgeProfile> judgeProfiles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ResultStandings> resultStandingsSet;



}
