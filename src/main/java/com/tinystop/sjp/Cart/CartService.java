package com.tinystop.sjp.Cart;

import static com.tinystop.sjp.Type.ErrorCode.CART_NOT_FOUND;
import static com.tinystop.sjp.Type.ErrorCode.INAPPROPRIATE_QUANTITY_VALUE;
import static com.tinystop.sjp.Type.ErrorCode.PRODUCT_NOT_FOUND;
import static com.tinystop.sjp.Type.ErrorCode.USER_NOT_FOUND;
import static com.tinystop.sjp.Type.OrderableStatus.IN_STOCK;
import static com.tinystop.sjp.Type.OrderableStatus.NOT_ORDERABLE;
import static com.tinystop.sjp.Type.ProductStockStatus.SOLD_OUT;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinystop.sjp.Auth.AccountEntity;
import com.tinystop.sjp.Auth.AccountRepository;
import com.tinystop.sjp.Exception.CustomException;
import com.tinystop.sjp.Product.ProductEntity;
import com.tinystop.sjp.Product.ProductRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;

    /** 특정 product를 cart에 담기
     * @param username 현재 로그인한 유저 username
     * @param addToCartDto cart에 담기 위한 데이터 (Long productId, int quantity)
     */
    public void addToCart(String username, AddToCartDto addToCartDto) { // product를 cart에 담기
        if (addToCartDto.getQuantity() <= 0) { // cart에 담을 product의 양이 음수이면 throw error
            throw new CustomException(INAPPROPRIATE_QUANTITY_VALUE, "product-list");
        }
        AccountEntity user = accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "product-list")); // 유저 확인

        ProductEntity product = productRepository.findById(addToCartDto.getProductId()).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND, "product-list")); // Product 확인

        CartEntity toCart = cartRepository.findByAccountAndProduct(user, product); // 기존 Cart에 추가되있는지 확인
        
        if (toCart != null) { // 추가 되어있으면 수량만 증가시키기
            if(toCart.getQuantity() < addToCartDto.getQuantity()) { // 장바구니 수량이 재고보다 많이 있으면 throw
                throw new CustomException(INAPPROPRIATE_QUANTITY_VALUE, "product-list");
            }
            // 기존에 있던 수량이랑 더해서 저장
            toCart.setQuantity(toCart.getQuantity() + addToCartDto.getQuantity()); 
            cartRepository.save(toCart);
            return;
        }
        if (addToCartDto.getQuantity() > product.getQuantity()) { // cart에 있는 양이 product에 있는 양보다 많으면 throw error
            throw new CustomException(INAPPROPRIATE_QUANTITY_VALUE, "product-list");
        }
        cartRepository.save(addToCartDto.toEntity(user, product, addToCartDto.getQuantity())); // 기존 Cart에 없는 신규 Cart면 새로 entity save
    }

    /** 특정 cart 제거
     * @param username 현재 로그인한 유저 username
     * @param productId 제거하는 cart의 product
     */
    public void removeFromCart(String username, Long productId) {
        AccountEntity user = accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "cart-list")); // 유저 확인

        ProductEntity product = productRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND, "cart-list")); // Product 확인인

        CartEntity toRemoveCart = cartRepository.findByAccountAndProduct(user, product);

        if (toRemoveCart == null) {
            throw new CustomException(CART_NOT_FOUND,"cart-list");
        }
        
        else {
            cartRepository.delete(toRemoveCart);
        }
    }

    /** 장바구니 (cart) 목록 표시
     * @param username 현재 로그인한 유저 username
     * @return List<CartEntity> carts (장바구니 목록)
     */
    public List<CartEntity> cartList(String username) {
        AccountEntity user = accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "cart-list"));
        List<CartEntity> carts = checkCartList(cartRepository.findAllByAccount(user)); // cart의 quantity 체크
        return carts;
    }

    /** 예전에 담아두었던 cart의 quantity가 현재 product quantity보다 더 많은지 적은지 체크. cart의 quantity 더 많으면 NOT_ORDERABLE로, 더 적으면 IN_STOCK
     * @param cartList 확인할 장바구니 목록
     * @return 확인한 장바구니 목록
     */
    public List<CartEntity> checkCartList(List<CartEntity> cartList) { // product quantity가 미리 있던 cart entity의 quantity보다 많은 것들 체크
        for (int i = 0; i < cartList.size(); i++) {
            ProductEntity product = cartList.get(i).getProduct();
            if (product.getQuantity() < cartList.get(i).getQuantity() || product.getQuantity() == 0 || product.getStockStatus() == SOLD_OUT) { //cart entity quantity가 더 많을때 NOT_ORDERABLE로 전환
                cartList.get(i).setStatus(NOT_ORDERABLE);
            }
            else { // 아닐 때 IN_STOCK
                cartList.get(i).setStatus(IN_STOCK);
            }
        }
        return cartList;
    }
}
