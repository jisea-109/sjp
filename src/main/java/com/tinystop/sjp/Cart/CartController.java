package com.tinystop.sjp.Cart;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;



@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@RequestMapping("/cart/")
@Controller
public class CartController {

    private final CartService cartService;
    
    /** 장바구니 담기 (product-list.html에 사용)
     * @param addToCartDto cart에 담기 위한 데이터 (Long productId, int quantity)
     * @param userDetails 현재 로그인한 유저 정보
     * @return cart-list.html로 redirect
     */
    @PostMapping("add")
    public String AddToCart(@ModelAttribute("addToCart") @Valid AddToCartDto addToCartDto, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        cartService.addToCart(username, addToCartDto);
        return "redirect:/cart/list";
    }
    
    /** 특정 장바구니 제거
     * @param productId 제거하는 cart의 product
     * @param userDetails 현재 로그인한 유저 정보
     * @return cart-list.html로 redirect
     */
    @PostMapping("remove")
    public String RemoveFromCart(@RequestParam("productId") long productId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        cartService.removeFromCart(username, productId);
        return "redirect:/cart/list";
    }

    /** 장바구니 목록 확인, 담은 장바구니가 아무것도 없을 시 클라이언트에서 없다고 표시했음
     * @param userDetails 현재 로그인한 유저 정보
     * @param model addToCart에 필요한 데이터, cartlist 데이터 전달
     * @return cart-list.html로 이동
     */
    @GetMapping("list")
    public String CartList(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        List<CartEntity> cartList = cartService.cartList(username);
        model.addAttribute("cartlist", cartList);
        model.addAttribute("addToCart", new AddToCartDto());
        return "cart-list";
    }
    
}
