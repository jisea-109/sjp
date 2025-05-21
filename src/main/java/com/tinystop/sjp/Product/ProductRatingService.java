package com.tinystop.sjp.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinystop.sjp.Exception.CustomException;
import com.tinystop.sjp.Review.ReviewEntity;
import com.tinystop.sjp.Review.ReviewRepository;
import static com.tinystop.sjp.Type.ErrorCode.PRODUCT_NOT_FOUND;
import static com.tinystop.sjp.Type.ErrorCode.REVIEW_NOT_FOUND;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductRatingService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    /**
     * 특정 상품의 리뷰 평균을 계산하는 함수
     * - 리뷰가 없는 경우 CustomException throw
     * - REQUIRES_NEW 트랜잭션으로 별도 실행됨 or Transaction silently rolled back 오류 발생
     *
     * @param productId 평균을 계산할 상품 ID
     * @return 평점 평균 (소수점 1자리 반올림)
     */
    @Transactional
    public BigDecimal getReviewAverage(Long productId) {
        ProductEntity product = productRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND, "main"));

        List<ReviewEntity> reviews = reviewRepository.findByProduct(product);

        if (reviews.isEmpty()) {
            throw new CustomException(REVIEW_NOT_FOUND, "main");
        }

        int sum = 0;
        for (ReviewEntity review : reviews) {
            sum += review.getRating();
        }

        double average = (double) sum / reviews.size();
        return BigDecimal.valueOf(average).setScale(1, RoundingMode.HALF_UP);
    }

    /**
     * 상품 목록에 대한 평균 평점 맵 생성하는 함수
     *
     * @param productRatings 점수 맵 (여기에 각 product 평균 주입)
     * @param products 페이지 형태의 상품 목록
     * @return 평균 평점 맵
     */
    public Map<Long, BigDecimal> getAverageRating(Map<Long, BigDecimal> productRatings, Page<LoadProductDto> products) {
         for (LoadProductDto product : products) {
            try {
                BigDecimal rating = getReviewAverage(product.getId());
                productRatings.put(product.getId(), rating);
            } catch (CustomException error) {
                productRatings.put(product.getId(), BigDecimal.ZERO);
            }
        }
        return productRatings;
    }
}
