package com.tinystop.sjp.Review;

import com.tinystop.sjp.Auth.Admin.ModifyProductDto;
import com.tinystop.sjp.Product.ProductEntity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditReviewDto {

    @NotNull
    private Long id;

    @NotBlank
    private String reviewTitle;

    @NotBlank
    private String reviewText;
    
    @NotNull
    @Min(1)
    @Max(10)
    private int rating;
    
    public static EditReviewDto from(ReviewEntity review) {
        EditReviewDto dto = new EditReviewDto();
        dto.setId(review.getId());
        dto.setReviewTitle(review.getReviewTitle());
        dto.setReviewText(review.getReviewText());
        dto.setRating(review.getRating());
        return dto;
    }
}
