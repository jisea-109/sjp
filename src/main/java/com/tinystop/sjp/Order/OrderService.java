package com.tinystop.sjp.Order;

import static com.tinystop.sjp.Type.ErrorCode.NOT_ENOUGH_STOCK;
import static com.tinystop.sjp.Type.ErrorCode.ORDER_NOT_FOUND;
import static com.tinystop.sjp.Type.ErrorCode.OUT_OF_STOCK;
import static com.tinystop.sjp.Type.ErrorCode.PRODUCT_NOT_FOUND;
import static com.tinystop.sjp.Type.ErrorCode.USER_NOT_FOUND;

import java.util.List;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.tinystop.sjp.S3Service;
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
    private final S3Service s3Service;
    /** cart에 담긴 product를 주문하기
     * @param username 주문하는 유저 username
     * @param addToOrderDto order하기에 필요한 데이터 (Long productId, int quantity)
     */
    public void addToOrder(String username, AddtoOrderDto addToOrderDto) {
        AccountEntity user = accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND,"product-list"));
        ProductEntity product = productRepository.findById(addToOrderDto.getProductId()).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND,"product-list"));
        
        if (product.getStockStatus() == ProductStockStatus.SOLD_OUT) {
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
        
        orderRepository.save(addToOrderDto.toEntity(user, product, addToOrderDto.getQuantity())); // order entity 저장
    }   
    
    /** Order 취소하기
     * @param username 주문했던 유저 username
     * @param removeOrderRequest 캔슬하기에 필요한 데이터 (Long productId, Long orderId)
     */
    public void cancelOrder(String username, RemoveOrderDto removeOrderRequest) { 
        // user 확인, 만약 없을 시 throw error
        AccountEntity user = accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND,"product-list"));
        // product 확인, 만약 없을 시 throw error
        ProductEntity product = productRepository.findById(removeOrderRequest.getProductId()).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND,"product-list"));
        // orderId랑 user, product를 통해서 order 확인
        OrderEntity order = orderRepository.findByIdAndAccountAndProduct(removeOrderRequest.getOrderId(),user,product).orElseThrow(() -> new CustomException(ORDER_NOT_FOUND,"product-list"));
        if (product.getStockStatus() == ProductStockStatus.SOLD_OUT) { // 주문 취소했을 때 만약 product의 재고 상태가 SOLD_OUT 상태였으면 product 재고 상태를 IN_STOCK로 바꾸기
            product.setStockStatus(ProductStockStatus.IN_STOCK);
        }
        product.setQuantity(product.getQuantity() + order.getQuantity()); // 캔슬한 product 만큼 quantity 다시 추가
        orderRepository.delete(order); // order 삭제
    }

    /** 특정 유저의 order를 모아둔 list return
     * @param username 주문했던 유저 username
     * @return List<OrderEntity> orderList (order 목록)
     */
    public List<OrderEntity> orderList(String username) {
        AccountEntity user = accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "order-list"));
        List<OrderEntity> orderList = orderRepository.findAllByAccount(user);

        orderList.forEach(order -> {
            ProductEntity product = order.getProduct();
            product.setImagePaths(s3Service.getFileUrls(product.getImagePaths()));
        });

        return orderList;
    }

}
