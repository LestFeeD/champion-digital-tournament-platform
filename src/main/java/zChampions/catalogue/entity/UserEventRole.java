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
@Table(name = "user_event_role", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "organization_id"}))
public class UserEventRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_role_id")
    private Long eventRoleId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventEntity event;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleSport role;
}
