package com.tinystop.sjp.Review;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import static com.tinystop.sjp.Type.ErrorCode.ALREADY_EXIST_REVIEW;
import static com.tinystop.sjp.Type.ErrorCode.FAILED_TO_UPLOAD_IMAGE;
import static com.tinystop.sjp.Type.ErrorCode.NO_PERMISSION_TO_EDIT;
import static com.tinystop.sjp.Type.ErrorCode.PATH_TRAVERSAL_DETECTED;
import static com.tinystop.sjp.Type.ErrorCode.PRODUCT_NOT_FOUND;
import static com.tinystop.sjp.Type.ErrorCode.REVIEW_NOT_FOUND;
import static com.tinystop.sjp.Type.ErrorCode.TOO_MANY_IMAGES_TO_UPLOAD;
import static com.tinystop.sjp.Type.ErrorCode.UNSUPPORTED_FILE_TYPE;
import static com.tinystop.sjp.Type.ErrorCode.USER_NOT_FOUND;

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

@RequiredArgsConstructor
@Transactional
@Service
public class ReviewService {

    private final AccountRepository accountRepository; // 유저 repository
    private final ReviewRepository reviewRepository; // 리뷰 repository
    private final ProductRepository productRepository; // 물건 repository

    private static final List<String> allowedImageTypes= List.of("image/jpeg", "image/png", "image/webp", "image/gif"); // 허용되는 이미지 확장자 목록
    private static final List<String> executableFileTypes =List.of(".jsp",".html",".php", ".java"); // 파일 업로드 했을 때 문제될 수 있는 파일들 목록
    private static final int maxAllowedImages = 5;

    public ReviewEntity addReview(String username, AddReviewDto addReviewRequest, MultipartFile[] uploadImages) { // 리뷰 추가하기 (유저 이름, 리뷰 구성요소 (제목, 본문, 점수), 이미지 파일)
        AccountEntity account = this.accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "add-review"));
        ProductEntity product = this.productRepository.findById(addReviewRequest.getProductId()).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND, "add-review"));
        
        if (reviewRepository.existsByAccountAndProduct(account, product)) { // 이미 유저가 리뷰 작성했는지 체크
            throw new CustomException(ALREADY_EXIST_REVIEW, "");
        }
        if (uploadImages != null) { // 리뷰에 이미지 올리는게 선택이기에 만약 이미지가 있으면 이미지 체크 후 업로드, 없을 시 넘기기
            if (uploadImages.length > 5) { // 6개 이상 올리는지 체크
                throw new CustomException(TOO_MANY_IMAGES_TO_UPLOAD, "add-review");
            }
            checkImages(uploadImages); // 이미지 형태 체크
            List<String> imagePaths = new ArrayList<>();
            imagePaths = uploadImages(uploadImages, "add-review"); // 이미지 업로드
            return reviewRepository.save(addReviewRequest.toEntity(account, product, imagePaths)); // 업로드 후 review entity에 저장
        }
        
        return reviewRepository.save(addReviewRequest.toEntity(account, product)); // 이미지 없이 review entity 에 저장
    }

    public ReviewEntity editReview(String username, EditReviewDto editReviewRequest, MultipartFile[] uploadImages) {// 리뷰 수정하기 (유저 이름, 리뷰 구성요소 (제목, 본문, 점수), 이미지 파일)
        AccountEntity account = this.accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "edit-review"));
        ReviewEntity review = this.reviewRepository.findById(editReviewRequest.getId()).orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND,"edit-review"));

        if (!review.getAccount().getUserID().equals(account.getUserID())) { // 해당 리뷰를 작성하지 않은 사람이 리뷰를 수정할려고 시도한 경우 체크
            throw new CustomException(NO_PERMISSION_TO_EDIT, "edit-review");
        }

        List<String> currentImages = review.getImagePaths(); // 현재 저장되있는 이미지
        if (editReviewRequest.getDeleteImagePaths() != null) { // 삭제할 이미지 있는지 체크
            for (String pathToDelete : editReviewRequest.getDeleteImagePaths()) {
                currentImages.remove(pathToDelete); // DB에서 제거
                File file = new File("src/main/resources/static" + pathToDelete); // 실제 파일 삭제
                if (file.exists()) { // 파일 삭제
                    file.delete();
                }
            }
        }

        if (uploadImages != null) {
            checkTheNumberOfImages(currentImages, uploadImages); // 기존에 저장되있던 이미지 수량과 새로 업로드 하는 사진 수량 비교 (삭제되는 사진 갯수도 현재 이미지 수량에 포함되있음)
            checkImages(uploadImages); // 이미지 파일 보안 체크
            List<String> uploaded = uploadImages(uploadImages, "edit-review");
            review.getImagePaths().addAll(uploaded); // 이미지 경로 추가
        }
        
        review.setReviewTitle(editReviewRequest.getReviewTitle()); // 제목 수정
        review.setReviewText(editReviewRequest.getReviewText()); // 본문 수정
        review.setRating(editReviewRequest.getRating()); // 평가 점수 수정

        return reviewRepository.save(review);
    }

    public void checkImages(MultipartFile[] uploadImages) { // 이미지 파일 보안 체크
        for (MultipartFile image : uploadImages) { 
            if (!allowedImageTypes.contains(image.getContentType())) { // "image/jpeg", "image/png", "image/webp", "image/gif" 파일이 있는지 확인
                throw new CustomException(UNSUPPORTED_FILE_TYPE, "add-review");
            }
            String originalFileName = image.getOriginalFilename();
            if (originalFileName != null) {
                for (String fileExtension : executableFileTypes) {  
                    if (originalFileName.endsWith(fileExtension)) { // 파일 확장명 체크 (jsp, html, php, java 등)
                        throw new CustomException(UNSUPPORTED_FILE_TYPE, "add-review");
                    }
                }
            }
        }
    }
    
    public void checkTheNumberOfImages(List<String> currentImages, MultipartFile[] uploadImages) { // editReview 함수에 쓰이는 이미지 갯수 확인 함수 (현재 이미지 갯수, 업로드할 이미지 갯수)
        int existingImageCount = 0; // 이전에 저장되있던 사진 갯수
        if (currentImages != null) {existingImageCount = currentImages.size();}

        int uploadedImageCount = 0; // 새로 올릴 사진 갯수
        if (uploadImages != null) {uploadedImageCount = uploadImages.length;}

        if (existingImageCount + uploadedImageCount > maxAllowedImages) { // 만약 합쳐서 6개 이상이면 업로드 막기
            throw new CustomException(TOO_MANY_IMAGES_TO_UPLOAD, "edit-review");
        }
    }

    public List<String> uploadImages(MultipartFile[] uploadImages, String page) { // addReview, editReview 함수에 쓰이는 이미지 업로드 함수 (이미지 파일, 오류났을 시 return할 페이지 이름) 
        List<String> imagePaths = new ArrayList<>();
        String uploadPath = new File("src/main/resources/static/reviews").getAbsolutePath(); // 저장 경로
        for (MultipartFile image : uploadImages) {
            if (!image.isEmpty()) {
                try {
                    String originalFileName = image.getOriginalFilename();
                    String savedFilename = UUID.randomUUID() + "_" + originalFileName; // 파일 덮어쓰기 발생 방지

                    File dest = new File(uploadPath, savedFilename);
                    
                    String canonicalBase = new File(uploadPath).getCanonicalPath(); // '../'같은 상대경로를 절대경로로 변경, 방지 못할 시 파일 덮어쓰기 또는 데이터 노출 가능성 있음
                    String canonicalTarget = dest.getCanonicalPath();

                    if (!canonicalTarget.startsWith(canonicalBase)) {
                        throw new CustomException(PATH_TRAVERSAL_DETECTED, page);
                    }

                    image.transferTo(dest);
                    imagePaths.add("/reviews/" + savedFilename);
                } catch (IOException e) {
                    throw new CustomException(FAILED_TO_UPLOAD_IMAGE, page);
                }
            }
        }
        return imagePaths;
    }

    public ReviewEntity getReviewById(Long productId) { // edit-review.html에서 수정할 product 값들을 가져오기 위한 함수 (product id)
        return reviewRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND,"product-list"));
    }

    public BigDecimal getReviewAverage(Long productId) { // product-list.html에서 리뷰값 평균값 내기 위한 함수 (product id) 
        ProductEntity product = this.productRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND, "product-list"));
        List<ReviewEntity> reviews = this.reviewRepository.findByProduct(product);
        
        if (reviews.isEmpty()) { // product 리뷰가 제대로 가져와졌는지 체크
            throw new CustomException(REVIEW_NOT_FOUND, "product-list");
        }
        int sum = 0;
        for (int i = 0; i < reviews.size(); i++) {
            sum += reviews.get(i).getRating(); // 리뷰값 다 더하기
        }
        double average = (double) sum / reviews.size(); // 평균값 double값으로 변수 저장
        return BigDecimal.valueOf(average).setScale(1, RoundingMode.HALF_UP); // 반올림해서 return
    }

    public void removeReview(String username, Long reviewId) { // 리뷰 지우기 (유저 이름, 리뷰 아이디)
        AccountEntity account = this.accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "reivew-list"));
        ReviewEntity review = this.reviewRepository.findById(reviewId).orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND, "review-list"));

        if (!review.getAccount().getUserID().equals(account.getUserID())) { // 해당 리뷰를 작성하지 않은 사람이 리뷰를 삭제할려고 시도한 경우 체크
            throw new CustomException(NO_PERMISSION_TO_EDIT, "review-list");
        }

        if (review.getImagePaths() != null) {  // 리뷰가 사진이 있으면 삭제
            for (String path : review.getImagePaths()) { // 하나하나 삭제하게끔 for loop 사용
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

    public List<ReviewEntity> userReviewList(String username) { // 유저가 작성한 리뷰 가져오기 (유저 이름)
        AccountEntity account = this.accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "reivew-list"));
        List<ReviewEntity> reviews = this.reviewRepository.findByAccount(account);
        return reviews;
    }

    public List<ReviewEntity> productReviewList(Long productId) { // 특정 product에 작성된 리뷰 가져오기 (product id)
        ProductEntity product = this.productRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND, "product-list"));
        List<ReviewEntity> reviews = this.reviewRepository.findByProduct(product);

        if (reviews.isEmpty()) {
            throw new CustomException(REVIEW_NOT_FOUND, "product-list");
        }

        return reviews;
    }
}
