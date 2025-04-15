package com.tinystop.sjp.Review;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.tinystop.sjp.BaseEntity;
import com.tinystop.sjp.Auth.AccountEntity;
import com.tinystop.sjp.Product.ProductEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@EntityListeners(AuditingEntityListener.class) // createdAt modifiedAt 자동 업데이트
@Table(name = "REVIEW_TABLE")
public class ReviewEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "REVIEW_ID")
    private long id;

    @Column(name = "REVIEW_TITLE", nullable = false, length = 50)
    private String reviewTitle;

    @Column(name = "REVIEW_TEXT", nullable = false, length = 300)
    private String reviewText;

    @Column(name = "RATING", nullable = false)
    @Min(1)
    @Max(10)
    private int rating;

    @ElementCollection
    @CollectionTable(name = "REVIEW_IMAGES", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "IMAGE_PATH")
    private List<String> imagePaths = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "PRODUCT", nullable = false)
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "ACCOUNT", nullable = false)
    private AccountEntity account;

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
}
