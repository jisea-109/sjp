package com.tinystop.sjp.Review;

import com.tinystop.sjp.Product.ProductEntity;
import com.tinystop.sjp.Auth.AccountEntity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddReviewDto {

    @NotBlank
    private String reviewTitle;
    @NotBlank
    private String reviewText;
    @NotNull
    @Min(1)
    @Max(10)
    private int rating;
    @NotNull
    private Long productId;

    public ReviewEntity toEntity(AccountEntity account, ProductEntity product) {
        return ReviewEntity.builder()
            .reviewTitle(this.reviewTitle)
            .reviewText(this.reviewText)
            .rating(this.rating)
            .product(product)
            .account(account)
            .build();
    }
}
