package com.tinystop.sjp.Admin;

import com.tinystop.sjp.Product.ProductEntity;
import com.tinystop.sjp.Type.ProductCategory;
import com.tinystop.sjp.Type.ProductStockStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminAddProductDto {
    
    @Null(message = "상품 등록 시 ID는 자동 생성됩니다.")
    private Long id;

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(min = 2, max = 30, message = "이름은 2자 이상 30자 이하로 입력해주세요.")
    private String name;

    @NotBlank(message = "내용을 입력해주세요.")
    @Size(max = 300, message = "내용은 300자 이하로 입력해주세요.")
    private String description;

    @NotNull(message = "가격을 입력해주세요. (쉼표, 단위 빼고 숫자만 입력해주세요.)")
    @Min(value = 100, message = "가격은 최소 100원 이상이어야 합니다.")
    @Max(value = 10000000, message = "가격은 1천만원 이하만 가능합니다.")
    private int price;

    @NotNull(message = "카테고리를 선택해주세요.")
    private ProductCategory component;

    @NotBlank(message = "소켓을 입력해주세요.")
    @Size(max = 20, message = "소켓 정보는 20자 이하로 입력해주세요.")
    private String socket;

    @NotNull(message = "수량을 입력해주세요.")
    @Min(value = 0, message = "수량은 음수가 될 수 없습니다.")
    private int quantity;

    private ProductStockStatus stockStatus;

    public ProductEntity toEntity() {
        if (this.quantity > 0) {stockStatus = ProductStockStatus.IN_STOCK;}
        else {stockStatus = ProductStockStatus.SOLD_OUT;}
        return ProductEntity.builder()
            .id(this.id)
            .name(this.name)
            .description(this.description)
            .price(this.price)
            .component(this.component)
            .socket(this.socket)
            .quantity(this.quantity)
            .stockStatus(this.stockStatus)
            .build();
    }
}

