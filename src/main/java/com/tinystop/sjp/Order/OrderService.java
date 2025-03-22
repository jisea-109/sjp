package com.tinystop.sjp.Order;

import java.util.List;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.tinystop.sjp.Auth.AccountEntity;
import com.tinystop.sjp.Auth.AccountRepository;
import com.tinystop.sjp.Cart.CartEntity;
import com.tinystop.sjp.Cart.CartRepository;
import com.tinystop.sjp.Product.ProductEntity;
import com.tinystop.sjp.Product.ProductRepository;
import com.tinystop.sjp.exception.CustomException;
import com.tinystop.sjp.type.ProductStockStatus;
import static com.tinystop.sjp.type.ErrorCode.USER_NOT_FOUND;
import static com.tinystop.sjp.type.ErrorCode.PRODUCT_NOT_FOUND;
import static com.tinystop.sjp.type.ErrorCode.OUT_OF_STOCK;
import static com.tinystop.sjp.type.ErrorCode.NOT_ENOUGH_STOCK;
import static com.tinystop.sjp.type.ErrorCode.ORDER_NOT_FOUND;

// cart에 있는 quantity 그대로 가져와서 order entity에 넣어야함
// product entity에 quantity를 추가해서 stock이 있는지 없는지 확인하게 기능 만들기
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    public OrderEntity addToOrder(String username, AddtoOrderDto addToOrderDto) {
        AccountEntity user = accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        ProductEntity product = productRepository.findById(addToOrderDto.getProductId()).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
        
        if(product.getStockStatus() == ProductStockStatus.SOLD_OUT) {
            throw new CustomException(OUT_OF_STOCK);
        }

        if (addToOrderDto.getQuantity() > product.getQuantity()) {
            throw new CustomException(NOT_ENOUGH_STOCK);
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
    
    public void cancelOrder(String username, Long productId, Long orderId) { 
        AccountEntity user = accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        ProductEntity product = productRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        //OrderEntity order = orderRepository.findByAccountAndProduct(user, product);
        boolean exists = orderRepository.existsByIdAndAccountAndProduct(orderId,user,product);
        if (!exists) { // order entity에 없으면 exception 처리
            throw new CustomException(ORDER_NOT_FOUND);
        }
        else {
            OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));
            if (product.getStockStatus() == ProductStockStatus.SOLD_OUT) { // 주문 취소했을 때 product 재고 다시 채우기
                product.setStockStatus(ProductStockStatus.IN_STOCK);
            }
            product.setQuantity(product.getQuantity() + order.getQuantity());
            orderRepository.delete(order);
        }
    }

    public List<OrderEntity> orderList(String username) {
        AccountEntity user = accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        List<OrderEntity> orderList = orderRepository.findAllByAccount(user);

        return orderList;
    }

}
