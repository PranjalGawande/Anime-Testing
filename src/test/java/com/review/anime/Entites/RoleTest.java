package com.review.anime.Entites;

import com.review.anime.entites.Permission;
import com.review.anime.entites.Role;
import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class RoleTest {

    @Test
    public void testRolePermissions() {
        Role userRole = Role.USER;
        Role adminRole = Role.ADMIN;

        Set<Permission> userPermissions = userRole.getPermissions();
        Set<Permission> adminPermissions = adminRole.getPermissions();

        assertTrue(userPermissions.contains(Permission.USER_GET));
        assertTrue(userPermissions.contains(Permission.USER_POST));
        assertTrue(userPermissions.contains(Permission.ADMIN_GET));
        assertTrue(userPermissions.contains(Permission.ADMIN_POST));

        assertTrue(adminPermissions.contains(Permission.ADMIN_GET));
        assertTrue(adminPermissions.contains(Permission.ADMIN_POST));
        assertFalse(adminPermissions.contains(Permission.USER_GET));
        assertFalse(adminPermissions.contains(Permission.USER_POST));
    }

    @Test
    public void testUserAuthorities() {
        Role role = Role.USER;

        List<SimpleGrantedAuthority> authorities = role.getUserAuthorities();

        assertTrue(authorities.contains(new SimpleGrantedAuthority("user:get")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("user:post")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("admin:get")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("admin:post")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    public void testAdminAuthorities() {
        Role role = Role.ADMIN;

        List<SimpleGrantedAuthority> authorities = role.getUserAuthorities();

        assertTrue(authorities.contains(new SimpleGrantedAuthority("admin:get")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("admin:post")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertFalse(authorities.contains(new SimpleGrantedAuthority("user:get")));
    }
}