package com.tinystop.sjp.Review;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.tinystop.sjp.Exception.CustomException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;





@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@RequestMapping("/review/")
public class ReviewController {

    private final ReviewService reviewService;

    /** 현재 로그인해있는 유저가 본인이 작성한 리뷰 list가 나오는 review-list.html로 이동
     * @param userDetails 현재 로그인한 사용자 정보
     * @param model review list 전달
     * @return review-list.html
     */
    @GetMapping("list")
    public String GetReivewList(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        List<ReviewEntity> reviewList = reviewService.userReviewList(userDetails.getUsername());
        model.addAttribute("reviewList", reviewList);
        return "review-list";
    }

    /** 리뷰 작성 페이지로 이동
     * @param productId 선택한 product Id
     * @param model addReviewDto 전달
     * @return add-review.html
     */
    @GetMapping("add")
    public String AddReview(@RequestParam("productId") Long productId, Model model) {
        AddReviewDto addReviewDto = new AddReviewDto();
        addReviewDto.setProductId(productId);
        model.addAttribute("addReview", addReviewDto);
        return "add-review";
    }

    /** 리뷰 수정 페이지로 이동
     * @param productId 선택한 product Id
     * @param model EditReviewDto 전달
     * @return edit-review.html
     */
    @GetMapping("edit")
    public String EditReview(@RequestParam("id") Long productId, Model model) {
        ReviewEntity review = reviewService.getReviewByProductId(productId);
        EditReviewDto editReviewDto = EditReviewDto.from(review);

        model.addAttribute("editReview", editReviewDto);
        return "edit-review";
    }
    
    /** Review 새로 추가 (add-review.html)
     * @param addReviewRequest 유저가 작성한 리뷰 데이터 (String reviewTitle, String reviewText, int rating)
     * @param bindingResult 입력값 검증 결과를 담는 객체
     * @param userDetails 현재 로그인한 유저 정보 (구매기록 있는지 확인용)
     * @param uploadImages 추가할 이미지 (추가할 이미지가 없으면 null이 될 수도 있음)
     * @param model addReviewRequest 데이터를 전달, 에러났을 시 에러도 클라이언트에 전달
     * @return review-list.html로 redirect, 실패했을 시 add-review.html로 return
     */
    @PostMapping("add")
    public String AddReview(@ModelAttribute("addReview") @Valid AddReviewDto addReviewRequest,
                            BindingResult bindingResult,
                            @AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam("uploadImages") MultipartFile[] uploadImages,
                            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("addReview", addReviewRequest);
            return "add-review";
        }
        try {
            reviewService.addReview(userDetails.getUsername(), addReviewRequest, uploadImages);
        } catch (CustomException error) {
            model.addAttribute("addReview", addReviewRequest);
            model.addAttribute("errorMessage", error.getMessage());
            return "add-review";
        }
        
        return "redirect:/review/list";
    }
    
    /** Review 수정 (edit-review.html)
     * @param editReviewRequest 유저가 작성 또는 수정한 리뷰 데이터 (String reviewTitle, String reviewText, int rating) + imagePaths
     * @param bindingResult 입력값 검증 결과를 담는 객체
     * @param userDetails 현재 로그인한 유저 정보 (구매기록 있는지 또는 다른 사람의 리뷰를 수정하는지 확인용)
     * @param uploadImages 추가할 이미지 (추가할 이미지가 없으면 null이 될 수도 있음)
     * @param model editReviewRequest 데이터를 전달, 에러났을 시 에러도 클라이언트에 전달
     * @return review-list.html로 redirect, 실패했을 시 edit-review.html로 return
     */
    @PostMapping("edit")
    public String EditReview(@ModelAttribute("editReview") @Valid EditReviewDto editReviewRequest,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam("uploadImages") MultipartFile[] uploadImages,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editReview", editReviewRequest);
            return "edit-review";
        }
        try {
            reviewService.editReview(userDetails.getUsername(), editReviewRequest, uploadImages);
        } catch (CustomException error) {
            model.addAttribute("editReview", editReviewRequest);
            model.addAttribute("errorMessage", error.getMessage());
            return "edit-review";
        }
        return "redirect:/review/list";
    }

    @PostMapping("remove")
    public String RemoveReview(@RequestParam("id") Long reviewId, @AuthenticationPrincipal UserDetails userDetails) {
        reviewService.removeReview(userDetails.getUsername(), reviewId); 
        return "redirect:/review/list";
    }
}
