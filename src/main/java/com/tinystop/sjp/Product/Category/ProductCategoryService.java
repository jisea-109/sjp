package com.tinystop.sjp.Product.Category;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import com.tinystop.sjp.Exception.CustomException;
import com.tinystop.sjp.Type.ProductCategory;
import static com.tinystop.sjp.Type.ErrorCode.CATEGORY_ALREADY_EXIST;
import static com.tinystop.sjp.Type.ErrorCode.CATEGORY_NOT_FOUND;


@Transactional
@RequiredArgsConstructor
@Service
public class ProductCategoryService {
    
    private final ProductCategoryRepository productCategoryRepository;

    public List<ProductCategoryEntity> getProductCategoryList() {
        List<ProductCategoryEntity> categories = productCategoryRepository.findAll();
        return categories;
    }

    public Set<String> getAllProductCategoryList() {
        List<String> enumNames = Arrays.stream(ProductCategory.values()).map(Enum::name).toList();
        List<String> entityNames = getProductCategoryList().stream().map(ProductCategoryEntity::getName).toList();
        
        Set<String> allCategoryNames = new LinkedHashSet<>();
        allCategoryNames.addAll(enumNames);
        allCategoryNames.addAll(entityNames);

        return allCategoryNames;
    }
    /** Product Category 추가하는 함수
     * @param addProductCategoryDto admin이 작성한 product category 데이터 (String name)
     */
    public void addProductCategory(AddProductCategoryDto addProductCategoryDto) {
        boolean category = productCategoryRepository.existsByName(addProductCategoryDto.getName());

        if (category) {
            throw new CustomException(CATEGORY_ALREADY_EXIST, "admin-product-category");
        }
        productCategoryRepository.save(addProductCategoryDto.toEntity());
    }

    /** Product Category 삭제하는 함수
     * @param productCategoryId 삭제할 Product Category ID
     */
    public void removeProductCategory(Long productCategoryId) {
        ProductCategoryEntity category = productCategoryRepository.findById(productCategoryId).orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND, "admin-product-category"));

        productCategoryRepository.delete(category);
    }
}
