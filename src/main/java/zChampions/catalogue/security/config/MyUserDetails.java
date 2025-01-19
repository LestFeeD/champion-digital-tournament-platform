package zChampions.catalogue.security.config;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import zChampions.catalogue.entity.UserEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyUserDetails implements UserDetails {

    private Long userId;
    private String email;
    private String password;
    private Collection<GrantedAuthority> authorities;



    public static MyUserDetails buildUserDetails(UserEntity user){
     List<GrantedAuthority> authorities = user.getUserRoleSystemEntities()
             .stream()
             .map(role -> new SimpleGrantedAuthority(role.getUserRole().name()))
             .collect(Collectors.toList());

     return new MyUserDetails(
             user.getUserId(),
             user.getEmail(),
             user.getPassword(),
             authorities);

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;

    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
