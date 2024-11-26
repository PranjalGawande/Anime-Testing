package com.review.anime.Entites;

import com.review.anime.entites.Review;
import com.review.anime.entites.Role;
import com.review.anime.entites.User;
import com.review.anime.entites.WatchList;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void testGettersAndSetters() {
        User user = new User();

        user.setUserId(1);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setRole(Role.ADMIN);
        user.setStatus(true);

        assertEquals(Integer.valueOf(1), user.getUserId());
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(Role.ADMIN, user.getRole());
        assertTrue(user.isStatus());
    }

    @Test
    public void testConstructor() {
        User user = new User(1, "John Doe", "john.doe@example.com", "password123", Role.USER, true, null, null);

        assertEquals(Integer.valueOf(1), user.getUserId());
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(Role.USER, user.getRole());
        assertTrue(user.isStatus());
    }

    @Test
    public void testIsStatus() {
        User user = new User();

        user.setStatus(true);
        assertTrue(user.isStatus());

        user.setStatus(false);
        assertFalse(user.isStatus());
    }

    @Test
    public void testGetCommentList() {
        User user = new User();

        assertNull(user.getCommentList()); // Initially null
        List<Review> comments = List.of(new Review());
        user.setCommentList(comments);
        assertEquals(comments, user.getCommentList());
    }

    @Test
    public void testAuthorities() {
        User user = new User();
        user.setRole(Role.ADMIN);

        List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>) user.getAuthorities();

        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("admin:get")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("admin:post")));
    }

    @Test
    public void testIsUserAdmin() {
        User user = new User();
        user.setRole(Role.ADMIN);

        assertTrue(user.isUserAdmin());

        user.setRole(Role.USER);
        assertFalse(user.isUserAdmin());
    }

    @Test
    public void testGetWatchLists() {
        User user = new User();

        assertNull(user.getWatchLists());
        List<WatchList> watchLists = List.of(new WatchList());
        user.setWatchLists(watchLists);
        assertEquals(watchLists, user.getWatchLists());
    }

    @Test
    public void testSpringSecurityMethods() {
        User user = new User();

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());

        user.setStatus(true);
        assertTrue(user.isEnabled());

        user.setStatus(false);
        assertFalse(user.isEnabled());
    }

    @Test
    public void testGetAuthorities() {
        User user = new User();
        user.setRole(Role.USER);

        List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>) user.getAuthorities();

        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertFalse(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    public void testGetUsername() {
        User user = new User();
        user.setEmail("john.doe@example.com");

        assertEquals("john.doe@example.com", user.getUsername());
    }

    @Test
    public void testIsUserAdminEdgeCase() {
        User user = new User();
        user.setRole(null);

        assertFalse(user.isUserAdmin());
    }

    @Test
    public void testRoleEdgeCases() {
        User user = new User();
        user.setRole(null);

        assertEquals(List.of(), user.getAuthorities());
    }





}