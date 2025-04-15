package com.tinystop.sjp.Review;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import static com.tinystop.sjp.type.ErrorCode.FAILED_TO_UPLOAD_IMAGE;

@RequiredArgsConstructor
@Transactional
@Service
public class ReviewService {

    private final AccountRepository accountRepository;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewEntity addReview(String username, AddReviewDto addReviewRequest, MultipartFile[] uploadImages) {
        AccountEntity account = this.accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "add-review"));
        ProductEntity product = this.productRepository.findById(addReviewRequest.getProductId()).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND, "add-review"));
        
        if (reviewRepository.existsByAccountAndProduct(account, product)) {
            throw new CustomException(ALREADY_EXIST_REVIEW, "");
        }

        if (uploadImages != null) {
            List<String> imagePaths = new ArrayList<>();
            imagePaths = uploadImages(uploadImages, "add-review");
            return reviewRepository.save(addReviewRequest.toEntity(account, product, imagePaths));
        }
        
        return reviewRepository.save(addReviewRequest.toEntity(account, product));
    }

    public ReviewEntity editReview(String username, EditReviewDto editReviewRequest, MultipartFile[] uploadImages) {
        AccountEntity account = this.accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "edit-review"));
        ReviewEntity review = reviewRepository.findById(editReviewRequest.getId()).orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND,"edit-review"));

        if (!review.getAccount().getUserID().equals(account.getUserID())) {
            throw new CustomException(NO_PERMISSION_TO_EDIT, "edit-review");
        }
        
        review.setReviewTitle(editReviewRequest.getReviewTitle());
        review.setReviewText(editReviewRequest.getReviewText());
        review.setRating(editReviewRequest.getRating());

        if (uploadImages != null) {
            List<String> uploaded = uploadImages(uploadImages, "edit-review");
            review.getImagePaths().addAll(uploaded); // 이미지 경로 추가
        }
        List<String> currentImages = review.getImagePaths();
        if (editReviewRequest.getDeleteImagePaths() != null) {
            for (String pathToDelete : editReviewRequest.getDeleteImagePaths()) {
                currentImages.remove(pathToDelete); // DB에서 제거

                // 실제 파일도 삭제
                File file = new File("src/main/resources/static" + pathToDelete);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        return reviewRepository.save(review);
    }

    public List<String> uploadImages(MultipartFile[] uploadImages, String page) {
        List<String> imagePaths = new ArrayList<>();
        for (MultipartFile image : uploadImages) {
            if (!image.isEmpty()) {
                try {
                    String originalFilename = image.getOriginalFilename();
                    String savedFilename = UUID.randomUUID() + "_" + originalFilename;

                    String uploadPath = new File("src/main/resources/static/reviews").getAbsolutePath();// 저장 경로
                    File dest = new File(uploadPath, savedFilename);
                    image.transferTo(dest);

                    imagePaths.add("/reviews/" + savedFilename);
                } catch (IOException e) {
                    throw new CustomException(FAILED_TO_UPLOAD_IMAGE, page);
                }
            }
        }
        return imagePaths;
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
        ReviewEntity review = reviewRepository.findById(reviewId).orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND, "review-list"));

        if (!review.getAccount().getUserID().equals(account.getUserID())) {
            throw new CustomException(NO_PERMISSION_TO_EDIT, "review-list");
        }

        if (review.getImagePaths() != null) {
            for (String path : review.getImagePaths()) {
                File file = new File("src/main/resources/static" + path);
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (!deleted) {
                        throw new CustomException(NO_PERMISSION_TO_EDIT, "review-list");
                    }
                }
            }
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
