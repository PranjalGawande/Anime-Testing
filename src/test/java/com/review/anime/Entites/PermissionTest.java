package com.review.anime.Entites;

import com.review.anime.entites.Permission;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PermissionTest {

    @Test
    public void testPermissionValues() {
        assertEquals("admin:get", Permission.ADMIN_GET.getPermission());
        assertEquals("admin:post", Permission.ADMIN_POST.getPermission());
        assertEquals("user:get", Permission.USER_GET.getPermission());
        assertEquals("user:post", Permission.USER_POST.getPermission());
    }

    @Test
    public void testEnumName() {
        assertEquals("ADMIN_GET", Permission.ADMIN_GET.name());
        assertEquals("USER_POST", Permission.USER_POST.name());
    }
}