package zChampions.catalogue.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import zChampions.catalogue.enumsEntities.KindOfSport;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    private String title;

    private KindOfSport kindOfSport;

    private String region;

    private String city;

    private LocalDate  createdAt;

    private LocalDate  endsAt;

    private String information;

    private String comments;

    @OneToMany(mappedBy = "event", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<ApplicationEvent> applicationEventList;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    @JsonIgnore
    private OrganizationEntity organization;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "event_participants",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private Set<UserEntity> userList;


    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "event_standings",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "standings_id")
    )
    @JsonIgnore
    private Set<Standings> standingsList;

    @OneToMany(mappedBy = "event", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<UserEventRole> userEventRoles;


}
