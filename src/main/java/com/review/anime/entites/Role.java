package com.review.anime.entites;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.review.anime.entites.Permission.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum Role {
    USER(
            Set.of(
                    Permission.USER_GET,
                    Permission.USER_POST,
                    Permission.ADMIN_GET,
                    Permission.ADMIN_POST
            )
    ),
    ADMIN(
            Set.of(
                    Permission.ADMIN_GET,
                    Permission.ADMIN_POST
            )
    );

    private Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getUserAuthorities() {
        var authorities = new java.util.ArrayList<>(getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .toList());

        authorities.add(new SimpleGrantedAuthority("ROLE_"+this.name()));
        return authorities;
    }
}
