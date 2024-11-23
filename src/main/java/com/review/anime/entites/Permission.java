package com.review.anime.entites;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum Permission {
    ADMIN_GET("admin:get"),
    ADMIN_POST("admin:post"),
    USER_GET("user:get"),
    USER_POST("user:post");

    private String permission;
}
