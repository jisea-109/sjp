package com.tinystop.sjp.Order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveOrderDto {
    private Long productId;
    private Long orderId;
}
