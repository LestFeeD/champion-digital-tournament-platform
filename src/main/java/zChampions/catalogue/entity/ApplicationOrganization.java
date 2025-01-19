package zChampions.catalogue.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import zChampions.catalogue.enumsEntities.RoleSport;
import zChampions.catalogue.enumsEntities.Status;

import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "application_organization")
public class ApplicationOrganization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_organization_id")
    private Long applicationOrganizationId;

    private RoleSport roleSport;

    private Status status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    @JsonIgnore
    private OrganizationEntity organization;
}
