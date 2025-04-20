package com.tinystop.sjp.Admin;

import static com.tinystop.sjp.Type.ErrorCode.ALREADY_EXIST_PRODUCT;
import static com.tinystop.sjp.Type.ErrorCode.PRODUCT_NOT_FOUND;
import static com.tinystop.sjp.Type.ErrorCode.TOO_MANY_IMAGES_TO_UPLOAD;
import static com.tinystop.sjp.Type.ErrorCode.UNSUPPORTED_FILE_TYPE;
import static com.tinystop.sjp.Type.ErrorCode.PATH_TRAVERSAL_DETECTED;
import static com.tinystop.sjp.Type.ErrorCode.FAILED_TO_UPLOAD_IMAGE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import com.tinystop.sjp.Exception.CustomException;
import com.tinystop.sjp.Product.ProductEntity;
import com.tinystop.sjp.Type.ProductStockStatus;

@RequiredArgsConstructor
@Transactional
@Service
public class AdminService {
    
    private final AdminRepository adminRepository;

    private static final List<String> allowedImageTypes= List.of("image/jpeg", "image/png", "image/webp", "image/gif"); // 허용되는 이미지 확장자 목록
    private static final List<String> executableFileTypes =List.of(".jsp",".html",".php", ".java"); // 파일 업로드 했을 때 문제될 수 있는 파일들 목록
    private static final int maxAllowedImages = 20;

    public ProductEntity addProduct(AdminAddProductDto addProductRequest, MultipartFile[] uploadImages) {
        boolean exists = this.adminRepository.existsByName(addProductRequest.getName());
        
        if (exists) { 
            throw new CustomException(ALREADY_EXIST_PRODUCT,"admin");
        }
        if (uploadImages != null) { // product에 이미지 올리는게 거의 필수지만 만약 이미지가 있으면 이미지 체크 후 업로드, 없을 시 넘기기
            if (uploadImages.length > maxAllowedImages) { // 20개 이상 올리는지 체크 (어드민이 업로드 하는거지만 혹시 모를 대비)
                throw new CustomException(TOO_MANY_IMAGES_TO_UPLOAD, "admin");
            }
            checkImages(uploadImages, "admin"); // 이미지 형태 체크
            List<String> imagePaths = new ArrayList<>();
            imagePaths = uploadImages(uploadImages, "admin"); // 이미지 업로드
            return adminRepository.save(addProductRequest.toEntity(imagePaths)); // 업로드 후 review entity에 저장
        }
        return adminRepository.save(addProductRequest.toEntity());
    }

    public ProductEntity getProductById(Long id) {
        return adminRepository.findById(id).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND,"product-list"));
    }
    
    public ProductEntity updateProduct(AdminEditProductDto editProductRequest, MultipartFile[] uploadImages) {
        ProductEntity toUpdateProduct = this.adminRepository.findById(editProductRequest.getId()).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND,"product-list"));

        List<String> currentImages = toUpdateProduct.getImagePaths(); // 현재 저장되있는 이미지
        if (editProductRequest.getDeleteImagePaths() != null) { // 삭제할 이미지 있는지 체크
            for (String pathToDelete : editProductRequest.getDeleteImagePaths()) {
                currentImages.remove(pathToDelete); // DB에서 제거
                File file = new File("src/main/resources/static" + pathToDelete); // 실제 파일 삭제
                if (file.exists()) { // 파일 삭제
                    file.delete();
                }
            }
        }

        if (Arrays.stream(uploadImages).anyMatch(file -> !file.isEmpty())) {
            if (uploadImages.length > maxAllowedImages) { // 20개 이상 올리는지 체크 (어드민이 업로드 하는거지만 혹시 모를 대비)
                throw new CustomException(TOO_MANY_IMAGES_TO_UPLOAD, "update-product-detail");
            }
            System.out.println(uploadImages.length);
            checkTheNumberOfImages(currentImages, uploadImages); // 기존에 저장되있던 이미지 수량과 새로 업로드 하는 사진 수량 비교 (삭제되는 사진 갯수도 현재 이미지 수량에 포함되있음)
            checkImages(uploadImages, "update-product-detail"); // 이미지 파일 보안 체크
            List<String> uploaded = uploadImages(uploadImages, "update-product-detail");
            toUpdateProduct.getImagePaths().addAll(uploaded); // 이미지 경로 추가
        }

        toUpdateProduct.setName(editProductRequest.getName());
        toUpdateProduct.setDescription(editProductRequest.getDescription());
        toUpdateProduct.setPrice(editProductRequest.getPrice());
        toUpdateProduct.setComponent(editProductRequest.getComponent());
        toUpdateProduct.setSocket(editProductRequest.getSocket());
        toUpdateProduct.setQuantity(editProductRequest.getQuantity());
        if(editProductRequest.getQuantity() > 0) {
            toUpdateProduct.setStockStatus(ProductStockStatus.IN_STOCK);
        }
        else {
            toUpdateProduct.setStockStatus(ProductStockStatus.SOLD_OUT);
        }

        return adminRepository.save(toUpdateProduct);
    }

    public void checkImages(MultipartFile[] uploadImages, String page) { // 이미지 파일 보안 체크
        for (MultipartFile image : uploadImages) { 
            if (!allowedImageTypes.contains(image.getContentType())) { // "image/jpeg", "image/png", "image/webp", "image/gif" 파일이 있는지 확인
                throw new CustomException(UNSUPPORTED_FILE_TYPE, page);
            }
            String originalFileName = image.getOriginalFilename();
            if (originalFileName != null) {
                for (String fileExtension : executableFileTypes) {  
                    if (originalFileName.endsWith(fileExtension)) { // 파일 확장명 체크 (jsp, html, php, java 등)
                        throw new CustomException(UNSUPPORTED_FILE_TYPE, page);
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

     public List<String> uploadImages(MultipartFile[] uploadImages, String page) { // addProduct, editProduct 함수에 쓰이는 이미지 업로드 함수 (이미지 파일, 오류났을 시 return할 페이지 이름) 
        List<String> imagePaths = new ArrayList<>();
        String uploadPath = new File("src/main/resources/static/ProductPhoto").getAbsolutePath(); // 저장 경로
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
                    imagePaths.add("/ProductPhoto/" + savedFilename);
                } catch (IOException e) {
                    throw new CustomException(FAILED_TO_UPLOAD_IMAGE, page);
                }
            }
        }
        return imagePaths;
    }

    public void removeProduct(Long productId) {
        ProductEntity toRemoveProduct = this.adminRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND, "product-list"));

        if (toRemoveProduct.getImagePaths() != null) {  // 리뷰가 사진이 있으면 삭제
            for (String path : toRemoveProduct.getImagePaths()) { // 하나하나 삭제하게끔 for loop 사용
                File file = new File("src/main/resources/static" + path);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        this.adminRepository.delete(toRemoveProduct);
    }
}
