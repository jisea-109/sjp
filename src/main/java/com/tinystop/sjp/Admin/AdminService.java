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
import com.tinystop.sjp.Product.Category.ProductCategoryEntity;
import com.tinystop.sjp.Product.Category.ProductCategoryRepository;
import com.tinystop.sjp.Type.ProductStockStatus;
import static com.tinystop.sjp.Type.ErrorCode.CATEGORY_NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class AdminService {
    
    private final AdminRepository adminRepository;
    private final ProductCategoryRepository productCategoryRepository;

    private static final List<String> allowedImageTypes= List.of("image/jpeg", "image/png", "image/webp", "image/gif"); // 허용되는 이미지 확장자 목록
    private static final List<String> executableFileTypes =List.of(".jsp",".html",".php", ".java"); // 파일 업로드 했을 때 문제될 수 있는 파일들 목록
    private static final int maxAllowedImages = 20; // product 등록할 때 최대 사진개수

    /** Product 추가 함수 (ADMIN 전용, 일반 User 접근 X)
     * - 이미 있거나, 이미지가 20개 초과할 시 throw error
     * @param addProductRequest admin이 작성한 product 데이터 (String name, String description, String socket, ProductCategory component, int quantity, int price)
     * @param uploadImages admin이 업로드한 이미지 (null이 될 수도 있음)
     */
    public void addProduct(AdminAddProductDto addProductRequest, MultipartFile[] uploadImages) {
        boolean exists = adminRepository.existsByName(addProductRequest.getName());
        
        if (exists) { 
            throw new CustomException(ALREADY_EXIST_PRODUCT,"admin");
        }
        ProductCategoryEntity component = productCategoryRepository.findById(addProductRequest.getCategoryId()).orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND, "admin"));
        if (uploadImages != null && Arrays.stream(uploadImages).anyMatch(file -> !file.isEmpty())) { // product에 이미지 올리는게 거의 필수지만 만약 이미지가 있으면 이미지 체크 후 업로드, 없을 시 넘기기
            if (uploadImages.length > maxAllowedImages) { // 20개 이상 올리는지 체크 (어드민이 업로드 하는거지만 혹시 모를 대비)
                throw new CustomException(TOO_MANY_IMAGES_TO_UPLOAD, "admin");
            }
            checkImages(uploadImages, "admin"); // 이미지 형태 체크
            List<String> imagePaths = new ArrayList<>();
            imagePaths = uploadImages(uploadImages, "admin"); // 이미지 업로드
            adminRepository.save(addProductRequest.toEntity(imagePaths, component)); // 업로드 후 review entity에 저장
            return;
        }
        adminRepository.save(addProductRequest.toEntity(component));
    }

    /** Product 정보 가져오는 함수, update-product-detail.html 에 product를 가져오는데 쓰임
     * @param productId 선택된 product ID
     * @return product ID를 기반으로 찾은 product
     */
    public ProductEntity getProductById(Long productId) {
        return adminRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND,"product-list"));
    }
    
    /** Product 정보 업데이트 함수 (Admin만 가능)
     * - 이미 있거나, 이미지가 20개 초과할 시 throw error
     * @param editProductRequest admin이 작성한, 혹은 수정이 필요없는 product 데이터 (String name, String description, String socket, ProductCategory component, int quantity, int price)
     * @param uploadImages admin이 업로드한, 혹은 이미 전에 업로드된 이미지 (null이 될 수도 있음)
     */
    public void updateProduct(AdminEditProductDto editProductRequest, MultipartFile[] uploadImages) {
        // 업데이트할 product 검색, 발견 안될 시 throw error
        ProductEntity toUpdateProduct = adminRepository.findById(editProductRequest.getId()).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND,"product-list"));

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

        if (Arrays.stream(uploadImages).anyMatch(file -> !file.isEmpty())) { // 사진이 있을 경우
            if (uploadImages.length > maxAllowedImages) { // 20개 이상 올리는지 체크 (어드민이 업로드 하는거지만 혹시 모를 대비)
                throw new CustomException(TOO_MANY_IMAGES_TO_UPLOAD, "update-product-detail");
            }
            checkTheNumberOfImages(currentImages, uploadImages); // 기존에 저장되있던 이미지 수량과 새로 업로드 하는 사진 수량 비교 (삭제되는 사진 개수도 현재 이미지 수량에 포함되있음)
            checkImages(uploadImages, "update-product-detail"); // 이미지 파일 보안 체크
            List<String> uploaded = uploadImages(uploadImages, "update-product-detail");
            toUpdateProduct.getImagePaths().addAll(uploaded); // 이미지 경로 추가
        }

        // product 모든 정보 update
        ProductCategoryEntity component = productCategoryRepository.findById(editProductRequest.getCategoryId()).orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND, "product-list"));
        toUpdateProduct.setName(editProductRequest.getName());
        toUpdateProduct.setDescription(editProductRequest.getDescription());
        toUpdateProduct.setPrice(editProductRequest.getPrice());
        toUpdateProduct.setComponent(component);
        toUpdateProduct.setSocket(editProductRequest.getSocket());
        toUpdateProduct.setQuantity(editProductRequest.getQuantity());
        if(editProductRequest.getQuantity() > 0) { // 물량이 1보다 많을 시 재고 있다고 표시
            toUpdateProduct.setStockStatus(ProductStockStatus.IN_STOCK);
        }
        else { // 물량이 0일 때 재고 없다고 표시
            toUpdateProduct.setStockStatus(ProductStockStatus.SOLD_OUT);
        }

        adminRepository.save(toUpdateProduct); // 수정된 product 저장
    }

    /** 보안을 위해 product에 등록될 이미지들 검사, 만약 문제 있을 시 'page' 에 throw error
     * @param uploadImages 업로드될 이미지
     * @param page 에러메세지 표시할 페이지 이름 (예시로는 'update-product-detail' 또는 'admin')
     */
    public void checkImages(MultipartFile[] uploadImages, String page) { // 이미지 파일 보안 체크
        for (MultipartFile image : uploadImages) { 
            if (!allowedImageTypes.contains(image.getContentType())) { // "image/jpeg", "image/png", "image/webp", "image/gif" 파일이 있는지 확인
                throw new CustomException(UNSUPPORTED_FILE_TYPE, page);
            }
            String originalFileName = image.getOriginalFilename(); // 이미지 이름 가져오기 (확장명 포함)
            if (originalFileName != null) {
                for (String fileExtension : executableFileTypes) {  
                    if (originalFileName.endsWith(fileExtension)) { // 파일 확장명 체크 (jsp, html, php, java 등)
                        throw new CustomException(UNSUPPORTED_FILE_TYPE, page);
                    }
                }
            }
        }
    }

    /** 현재 등록된 이미지 개수 검사 
     * @param currentImages 현재 이미지 개수
     * @param uploadImages  업로드할 이미지 개수
     */
    public void checkTheNumberOfImages(List<String> currentImages, MultipartFile[] uploadImages) {
        int existingImageCount = 0; // 이전에 저장되있던 사진 개수
        if (currentImages != null) {existingImageCount = currentImages.size();}

        int uploadedImageCount = 0; // 새로 올릴 사진 개수
        if (uploadImages != null) {uploadedImageCount = uploadImages.length;}

        if (existingImageCount + uploadedImageCount > maxAllowedImages) { // 만약 합쳐서 20개 이상이면 업로드 막기
            throw new CustomException(TOO_MANY_IMAGES_TO_UPLOAD, "edit-review");
        }
    }

    /** 이미지 업로드 (addProduct, editProduct에 사용됨)
     * @param uploadImages 업로드할 이미지 파일
     * @param page 오류났을 시 return할 페이지 이름
     * @return 이미지 경로 return
     */
    public List<String> uploadImages(MultipartFile[] uploadImages, String page) {
        List<String> imagePaths = new ArrayList<>();
        String uploadPath = new File("src/main/resources/static/ProductPhoto").getAbsolutePath(); // 저장 경로
        for (MultipartFile image : uploadImages) {
            if (!image.isEmpty()) {
                try {
                    String originalFileName = image.getOriginalFilename(); // 파일명
                    String savedFilename = UUID.randomUUID() + "_" + originalFileName; // 파일 덮어쓰기 발생 방지

                    File dest = new File(uploadPath, savedFilename);
                    
                    String canonicalBase = new File(uploadPath).getCanonicalPath(); // '../'같은 상대경로를 절대경로로 변경, 방지 못할 시 파일 덮어쓰기 또는 데이터 노출 가능성 있음
                    String canonicalTarget = dest.getCanonicalPath();

                    if (!canonicalTarget.startsWith(canonicalBase)) { // 경로우회 발견
                        throw new CustomException(PATH_TRAVERSAL_DETECTED, page);
                    }

                    image.transferTo(dest); // 실제 파일 저장
                    imagePaths.add("/ProductPhoto/" + savedFilename);
                } catch (IOException e) {
                    throw new CustomException(FAILED_TO_UPLOAD_IMAGE, page);
                }
            }
        }
        return imagePaths;
    }

    /** Product 삭제 함수 (Admin만 가능)
     * @param productId 삭제할 product id
     */
    public void removeProduct(Long productId) {
        ProductEntity toRemoveProduct = adminRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND, "product-list"));

        if (toRemoveProduct.getImagePaths() != null) {  // product에 사진이 있으면 삭제
            for (String path : toRemoveProduct.getImagePaths()) { // 하나하나 삭제하게끔 for loop 사용
                File file = new File("src/main/resources/static" + path);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        adminRepository.delete(toRemoveProduct);
    }
}
