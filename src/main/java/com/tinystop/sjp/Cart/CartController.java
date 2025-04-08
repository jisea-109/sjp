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
    
    @PostMapping("add")
    public String AddToCart(@ModelAttribute("addToCart") @Valid AddToCartDto addToCartDto, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        cartService.addToCart(username, addToCartDto);
        return "redirect:/cart/list";
    }
    
    @PostMapping("remove")
    public String RemoveFromCart(@RequestParam("productId") long productId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        cartService.removeFromCart(username, productId);
        return "redirect:/cart/list";
    }

    @GetMapping("list")
    public String CartList(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        List<CartEntity> cartList = cartService.cartList(username);
        model.addAttribute("cartlist", cartList);
        model.addAttribute("addToCart", new AddToCartDto());
        return "cart-list";
    }
    
}
