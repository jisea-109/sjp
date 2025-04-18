package com.tinystop.sjp.Order;

import static com.tinystop.sjp.Type.ErrorCode.NOT_ENOUGH_STOCK;
import static com.tinystop.sjp.Type.ErrorCode.ORDER_NOT_FOUND;
import static com.tinystop.sjp.Type.ErrorCode.OUT_OF_STOCK;
import static com.tinystop.sjp.Type.ErrorCode.PRODUCT_NOT_FOUND;
import static com.tinystop.sjp.Type.ErrorCode.USER_NOT_FOUND;

import java.util.List;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.tinystop.sjp.Auth.AccountEntity;
import com.tinystop.sjp.Auth.AccountRepository;
import com.tinystop.sjp.Cart.CartEntity;
import com.tinystop.sjp.Cart.CartRepository;
import com.tinystop.sjp.Exception.CustomException;
import com.tinystop.sjp.Product.ProductEntity;
import com.tinystop.sjp.Product.ProductRepository;
import com.tinystop.sjp.Type.ProductStockStatus;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    public OrderEntity addToOrder(String username, AddtoOrderDto addToOrderDto) {
        AccountEntity user = accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND,"product-list"));
        ProductEntity product = productRepository.findById(addToOrderDto.getProductId()).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND,"product-list"));
        
        if(product.getStockStatus() == ProductStockStatus.SOLD_OUT) {
            throw new CustomException(OUT_OF_STOCK, "product-list");
        }

        if (addToOrderDto.getQuantity() > product.getQuantity()) {
            throw new CustomException(NOT_ENOUGH_STOCK, "product-list");
        }

        CartEntity cart = cartRepository.findByAccountAndProduct(user, product);

        if (cart != null) { // 카트에 있으면 cart entity 삭제하고 order entity에 저장, 나중에 payment 결제창에서 실행하게 만들어야함
            cartRepository.delete(cart); 
        }

        if (product.getQuantity() == addToOrderDto.getQuantity()) {  // 재고랑 주문할라는 수량이 같을 때
            product.setQuantity(0); // 물건 수량 0
            product.setStockStatus(ProductStockStatus.SOLD_OUT); // 물건 SOLD OUT 상태로 처리
        }
        else {
            product.setQuantity(product.getQuantity() - addToOrderDto.getQuantity()); // 재고 - 주문할라는 수량
        }
        
        return orderRepository.save(addToOrderDto.toEntity(user, product, addToOrderDto.getQuantity())); // order entity 저장
    }   
    
    public void cancelOrder(String username, RemoveOrderDto removeOrderRequest) { 
        AccountEntity user = accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND,"product-list"));
        ProductEntity product = productRepository.findById(removeOrderRequest.getProductId()).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND,"product-list"));

        OrderEntity order = orderRepository.findByIdAndAccountAndProduct(removeOrderRequest.getOrderId(),user,product).orElseThrow(() -> new CustomException(ORDER_NOT_FOUND,"product-list"));
        if (product.getStockStatus() == ProductStockStatus.SOLD_OUT) { // 주문 취소했을 때 product 재고 다시 채우기
            product.setStockStatus(ProductStockStatus.IN_STOCK);
        }
        product.setQuantity(product.getQuantity() + order.getQuantity());
        orderRepository.delete(order);
    }

    public List<OrderEntity> orderList(String username) {
        AccountEntity user = accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "order-list"));
        List<OrderEntity> orderList = orderRepository.findAllByAccount(user);

        return orderList;
    }

}
