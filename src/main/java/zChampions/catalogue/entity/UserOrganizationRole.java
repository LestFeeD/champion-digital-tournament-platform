package zChampions.catalogue.entity;

import jakarta.persistence.*;
import lombok.*;
import zChampions.catalogue.enumsEntities.RoleSport;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_organization_role", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "organization_id"}))
public class UserOrganizationRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_role_id")
    private Long organizationRoleId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private OrganizationEntity organization;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleSport role;
}
