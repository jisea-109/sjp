package com.tinystop.sjp.Cart;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinystop.sjp.Auth.AccountEntity;
import com.tinystop.sjp.Auth.AccountRepository;
import com.tinystop.sjp.Exception.CustomException;
import com.tinystop.sjp.Product.ProductEntity;
import com.tinystop.sjp.Product.ProductRepository;

import static com.tinystop.sjp.type.ErrorCode.PRODUCT_NOT_FOUND;
import static com.tinystop.sjp.type.ErrorCode.USER_NOT_FOUND;
import static com.tinystop.sjp.type.ErrorCode.CART_NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;

    public CartEntity addToCart(String username, AddtoCartDto addToCartDto) { // product를 cart에 담기
        AccountEntity user = accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "product-list")); // 유저 확인

        ProductEntity product = productRepository.findById(addToCartDto.getProductId()).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND, "product-list")); // Product 확인인

        CartEntity toCart = cartRepository.findByAccountAndProduct(user, product); // 기존 Cart에 추가되있는지 확인

        if (toCart != null) { // 추가 되어있으면 수량만 증가시키기
            toCart.setQuantity(toCart.getQuantity() + addToCartDto.getQuantity());
            return this.cartRepository.save(toCart);
        }
        return this.cartRepository.save(addToCartDto.toEntity(user, product, addToCartDto.getQuantity())); // 기존 Cart에 없는 신규 Cart면 새로 entity save
    }

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

    public List<CartEntity> cartList(String username) { // 장바구니 (cart) 목록 표시
        AccountEntity user = accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "cart-list"));
        List<CartEntity> carts = cartRepository.findAllByAccount(user);
        return carts;
    }
}
