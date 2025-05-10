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

    /** cart에 담긴 product 주문
     * @param addtoOrderDto order하기에 필요한 데이터 (Long productId, int quantity)
     * @param userDetails 현재 로그인 해있는 유저 정보
     * @return order-list.html로 redirect
     */
    @PostMapping("add") 
    public String AddtoOrder(@ModelAttribute AddtoOrderDto addtoOrderDto, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        orderService.addToOrder(username, addtoOrderDto);
        return "redirect:/order/list";
    }

    /** Order 취소하기
     * @param removeOrderRequest 캔슬하기에 필요한 데이터 (Long productId, Long orderI
     * @param bindingResult 입력값 검증 결과를 담는 객체
     * @param model 에러났을 시 에러 전달
     * @param userDetails 현재 로그인 해있는 유저 정보
     * @return 성공 실패 상관없이 order-list.html로 이동 또는 redirect
     */
    @PostMapping("remove")
    public String RemoveOrder(@ModelAttribute("removeOrder") @Valid RemoveOrderDto removeOrderRequest, 
                              BindingResult bindingResult,
                              Model model,
                              @AuthenticationPrincipal UserDetails userDetails) {
                                
        String username = userDetails.getUsername();

        if (bindingResult.hasErrors()) {
            model.addAttribute("order-list", "예상치 못한 에러가 생겼습니다.");
            return "order-list";
        }
        try { 
            orderService.cancelOrder(username, removeOrderRequest);
        } catch (CustomException error) {
            model.addAttribute("errorMessage", error.getMessage());
            return "order-list";
        }
        return "redirect:/order/list";
    }
    
    /** order-list.html로 이동
     * @param model RemoveOrderDto와 현재 로그인 해 있는 유저의 order 목록 전달
     * @param userDetails 현재 로그인 해있는 유저 정보
     * @return  order-list.html로 이동
     */
    @GetMapping("list")
    public String OrderList(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<OrderEntity> orderList = orderService.orderList(username);
        model.addAttribute("removeOrder", new RemoveOrderDto());
        model.addAttribute("orderlist", orderList);
        return "order-list";
    }
}
