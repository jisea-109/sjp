package com.tinystop.sjp.Admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.tinystop.sjp.Exception.CustomException;
import com.tinystop.sjp.Product.ProductEntity;
import com.tinystop.sjp.Product.Category.ProductCategoryEntity;
import com.tinystop.sjp.Product.Category.ProductCategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminservice;
    private final ProductCategoryService productCategoryService;

    /** admin.html로 이동 (Product 추가 기능 구현된 html)
     */
    @GetMapping("")
    public String AdminPage(Model model) {
        List<ProductCategoryEntity> allCategoryNames = productCategoryService.getProductCategoryList();
        model.addAttribute("productCategories", allCategoryNames);
        model.addAttribute("addProduct", new AdminAddProductDto());
        return "admin";
    }

    /** product 새로 추가 (admin.html)
     * @param addProductRequest 추가할 product 데이터 (String name, String description, String socket, ProductCategory component, int quantity, int price)
     * @param bindingResult 입력값 검증 결과를 담는 객체
     * @param uploadImages 추가할 이미지 (추가할 이미지가 없으면 null이 될 수도 있음)
     * @param model 클라이언트에 전달할 데이터를 담은 객체, 이 함수에서는 error message를 클라이언트에 전달
     * @return
     */
    @PostMapping("/add-product")
    public String AddProducts(@ModelAttribute("addProduct") @Valid AdminAddProductDto addProductRequest, 
                              BindingResult bindingResult,
                              @RequestParam("uploadImages") MultipartFile[] uploadImages,
                              Model model) {
        if (bindingResult.hasErrors()) { // 입력값 유효성 검사후 오류가 있을 경우
            List<String> errors = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList()); // 에러 메세지 리스트

            model.addAttribute("errorMessage", errors); // 에러 메세지 전달
            model.addAttribute("addProduct", addProductRequest); // 에러나기전 admin이 입력한 데이터 다시 전달용
            return "admin"; // admin으로 return
        }
        try {
            adminservice.addProduct(addProductRequest, uploadImages); // product 추가, 성공했을 시 메세지 넣어서 안헷갈리게 하는게 좋을듯?
        } catch(CustomException error) { 
            model.addAttribute("addProduct", addProductRequest); // 에러나기전 admin이 입력한 데이터 다시 전달용
            model.addAttribute("errorMessage", error.getMessage()); // 에러 메세지 전달
            return "admin"; // admin으로 return
        }
        return "redirect:/admin"; // admin으로 redirect
    }

    /** Product 제거
     * @param productId 제거할 product ID
     * @return find-product.html에서 이루어질 PostMapping이기에 다시 find-product.html로 redirect
     */
    @PostMapping("/remove-product")
    public String RemoveProduct(@RequestParam("id") Long productId) {
        adminservice.removeProduct(productId);
        return "redirect:/find-product";
    }
    
    /** 특정 Product의 정보를 update하기 위한 update-product.detail로 이동
     * @param productId 선택한 product ID
     * @param model Product Category와 EditProductDto 데이터를 전달
     * @return update-product-detail로 이동
     */
    @GetMapping("/update-product")
    public String UpdateProductPage(@RequestParam("id") Long productId, Model model) {
        ProductEntity product = adminservice.getProductById(productId);
        AdminEditProductDto editProductDto = AdminEditProductDto.from(product);
        
        model.addAttribute("editProduct", editProductDto);
        model.addAttribute("productCategories", productCategoryService.getProductCategoryList());
        return "update-product-detail";
    }

    /** 특정 Product의 정보를 update 처리
     * @param editProductRequest 추가 또는 수정할 product 데이터 (String name, String description, String socket, ProductCategory component, int quantity, int price)
     * @param bindingResult 입력값 검증 결과를 담는 객체
     * @param uploadImages 추가할 이미지 (추가할 이미지가 없으면 null이 될 수도 있음)
     * @param model EditProductDto 데이터를 전달, 에러났을 시 에러와 Product Category도 전달
     * @return find-product로 redirect, 실패시 다시 update-product-detail.html로 return
     */
    @PostMapping("/update-product-detail")
    public String UpdateProductDetailPage(@ModelAttribute("editProduct") @Valid AdminEditProductDto editProductRequest,
                                          BindingResult bindingResult,
                                          @RequestParam("uploadImages") MultipartFile[] uploadImages,
                                          Model model) {
        if (bindingResult.hasErrors()) { // 입력값 유효성 검사후 오류가 있을 경우
            List<String> errors = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList()); // 에러 메세지 리스트

            model.addAttribute("errorMessage", errors); // 에러 메세지 전달
            model.addAttribute("update-product-detail", editProductRequest); // 에러나기전 admin이 입력한 또는 기존 product 데이터로 다시 돌리기 위해 전달
            return "update-product-detail"; // update-product-detail로 이동
        }
        try {
            adminservice.updateProduct(editProductRequest, uploadImages); // product 업데이트 시도
        } catch(CustomException error) {
            model.addAttribute("update-product-detail", editProductRequest); // 에러나기전 admin이 입력한 또는 기존 product 데이터로 다시 돌리기 위해 전달ㄴㄴ
            model.addAttribute("errorMessage", error.getMessage()); // 에러 메세지 전달
            return "update-product-detail"; // update-product-detail로 이동
        }
        return "redirect:/find-product"; // find-product로 redirect
    }
    
}
