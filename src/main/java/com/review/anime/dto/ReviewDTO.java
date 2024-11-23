package com.review.anime.dto;

import com.review.anime.entites.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Integer animeId;
    private Float rating;
    private String comment;
    private String name;
    private Integer commentId;

    public ReviewDTO(Review review) {
        this.animeId = review.getAnimeId();
        this.rating = review.getRating();
        this. commentId = review.getCommentId();
        this.name = review.getUser().getName();
        this.comment = review.getComment();
    }
}
