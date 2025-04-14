package com.tinystop.sjp.Review;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.tinystop.sjp.Auth.AccountEntity;
import com.tinystop.sjp.Auth.AccountRepository;
import com.tinystop.sjp.Exception.CustomException;
import com.tinystop.sjp.Product.ProductEntity;
import com.tinystop.sjp.Product.ProductRepository;

import static com.tinystop.sjp.type.ErrorCode.USER_NOT_FOUND;
import static com.tinystop.sjp.type.ErrorCode.PRODUCT_NOT_FOUND;
import static com.tinystop.sjp.type.ErrorCode.ALREADY_EXIST_REVIEW;
import static com.tinystop.sjp.type.ErrorCode.NO_PERMISSION_TO_EDIT;
import static com.tinystop.sjp.type.ErrorCode.REVIEW_NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class ReviewService {

    private final AccountRepository accountRepository;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewEntity addReview(String username, AddReviewDto addReviewRequest) { // 나중에 order-list에서 AddReviewDto form 추가해야함
        AccountEntity account = this.accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "add-review"));
        ProductEntity product = this.productRepository.findById(addReviewRequest.getProductId()).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND, "add-review"));
        
        if (reviewRepository.existsByAccountAndProduct(account, product)) {
            throw new CustomException(ALREADY_EXIST_REVIEW, "");
        }
        return reviewRepository.save(addReviewRequest.toEntity(account, product));
    }

    public ReviewEntity editReview(String username, EditReviewDto editReviewRequest) {
        AccountEntity account = this.accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "edit-review"));
        ReviewEntity review = reviewRepository.findById(editReviewRequest.getId()).orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND,"edit-review"));

        if (!review.getAccount().getUserID().equals(account.getUserID())) {
            throw new CustomException(NO_PERMISSION_TO_EDIT, "edit-review");
        }
        
        review.setReviewTitle(editReviewRequest.getReviewTitle());
        review.setReviewText(editReviewRequest.getReviewText());
        review.setRating(editReviewRequest.getRating());

        reviewRepository.save(review);
        return review;
    }

    public ReviewEntity getReviewById(Long id) {
        return reviewRepository.findById(id).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND,"product-list"));
    }

    public BigDecimal getReviewAverage(Long productId) {
        ProductEntity product = this.productRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND, "product-list"));
        List<ReviewEntity> reviews = this.reviewRepository.findByProduct(product);
        
        if (reviews.isEmpty()) {
            throw new CustomException(REVIEW_NOT_FOUND, "product-list");
        }
        int sum = 0;
        for (int i = 0; i < reviews.size(); i++) {
            sum += reviews.get(i).getRating();
        }
        double average = (double) sum / reviews.size();
        return BigDecimal.valueOf(average).setScale(1, RoundingMode.HALF_UP);
    }
    public void removeReview(String username, Long reviewId) {
        AccountEntity account = this.accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "reivew-list"));
        ReviewEntity review = this.reviewRepository.findByAccountAndId(account,reviewId);

        if (review == null) {
            throw new CustomException(REVIEW_NOT_FOUND, "reivew-list");
        }
        this.reviewRepository.delete(review);
    }

    public List<ReviewEntity> reviewList(String username) {
        AccountEntity account = this.accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "reivew-list"));
        List<ReviewEntity> reviews = this.reviewRepository.findByAccount(account);
        return reviews;
    }

    public List<ReviewEntity> productReviewList(Long productId) {
        ProductEntity product = this.productRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND, "product-list"));
        List<ReviewEntity> reviews = this.reviewRepository.findByProduct(product);

        if (reviews.isEmpty()) {
            throw new CustomException(REVIEW_NOT_FOUND, "product-list");
        }

        return reviews;
    }
}
