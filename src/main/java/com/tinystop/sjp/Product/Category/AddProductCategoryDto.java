package com.tinystop.sjp.Product.Category;

import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddProductCategoryDto {

    @Null(message = "상품 등록 시 ID는 자동 생성됩니다.")
    private Long id;

    @Size(min = 2, max = 30, message = "이름은 2자 이상 30자 이하로 입력해주세요.")
    private String name;

    public ProductCategoryEntity toEntity() {
        return ProductCategoryEntity.builder()
            .id(this.id)
            .name(this.name)
            .build();
    }
}
