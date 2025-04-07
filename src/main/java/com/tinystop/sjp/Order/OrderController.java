package com.tinystop.sjp.Order;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tinystop.sjp.Exception.CustomException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;

@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@RequestMapping("/order/")
@Controller
public class OrderController {

    private final OrderService orderService;

    @PostMapping("add") 
    public String AddtoOrder(@ModelAttribute AddtoOrderDto addtoOrderDto, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        orderService.addToOrder(username, addtoOrderDto);
        return "redirect:/order/list";
    }

    @PostMapping("remove")
    public String RemoveOrder(@ModelAttribute("removeOrder") @Valid RemoveOrderDto removeOrderRequest, 
                              BindingResult bindingResult,
                              Model model,
                              @AuthenticationPrincipal UserDetails userDetails) {
                                
        String username = userDetails.getUsername();

        if (bindingResult.hasErrors()) {
            model.addAttribute("order-list", "예상치 못한 에러가 생겼습니다.");
        }
        try { 
            orderService.cancelOrder(username, removeOrderRequest);
        } catch (CustomException error) {
            model.addAttribute("errorMessage", error.getMessage());
        }
        
        return "redirect:/order/list";
    }
    
    @GetMapping("list")
    public String OrderList(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<OrderEntity> orderList = orderService.orderList(username);
        model.addAttribute("removeOrder", new RemoveOrderDto());
        model.addAttribute("orderlist", orderList);
        return "order-list";
    }
    


    
}
