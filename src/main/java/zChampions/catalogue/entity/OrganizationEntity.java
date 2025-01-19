package zChampions.catalogue.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import zChampions.catalogue.enumsEntities.KindOfSport;
import zChampions.catalogue.enumsEntities.TypeOfOrganization;

import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "organization")
public class OrganizationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_id")
    private Long organizationId;

    private String title;

    private String country;

    private String region;

    private String city;

    @Column(name = "type_of_sport", nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private KindOfSport kindOfSport;

    private TypeOfOrganization typeOfOrganization;

    private String email;

    private String phoneNumber;

    private String linkWebsite;

    private String officialWebsite;

    @ManyToMany(mappedBy = "organizationEntityList", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<UserEntity> users;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<EventEntity> event;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<UserOrganizationRole> userOrganizationRoles;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ApplicationOrganization> applicationOrganizationSet;


}
