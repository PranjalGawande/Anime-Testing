package com.review.anime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExtraDTO {
    private Integer animeId;
    private Float rating;
    private String comment;

    private String newPassword;
    private String oldPassword;
}
