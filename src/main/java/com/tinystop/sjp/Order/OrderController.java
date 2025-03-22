package com.tinystop.sjp.Order;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@RequestMapping("/order/")
@Controller
public class OrderController {

    private final OrderService orderService;

    // @GetMapping("toConfirmation")
    // public String ToConfirmation() {
    //     return "confirmation";
    // }

    @PostMapping("add") 
    public String AddtoOrder(@ModelAttribute AddtoOrderDto addtoOrderDto, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        System.out.println(addtoOrderDto.getQuantity());
        orderService.addToOrder(username, addtoOrderDto);
        return "redirect:/order/list";
    }

    @PostMapping("remove")
    public String RemoveOrder(@RequestParam("productId") long productId, @RequestParam("orderId") long orderId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        orderService.cancelOrder(username, productId, orderId);
        return "redirect:/order/list";
    }
    
    @GetMapping("list")
    public String OrderList(Model model,@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<OrderEntity> orderList = orderService.orderList(username);
        model.addAttribute("orderlist", orderList);
        return "order-list";
    }
    


    
}
