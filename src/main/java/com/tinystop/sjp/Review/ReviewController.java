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

    @GetMapping("list")
    public String GetReivewList(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        List<ReviewEntity> reviewList = reviewService.userReviewList(userDetails.getUsername());
        model.addAttribute("reviewList", reviewList);
        return "review-list";
    }

    @GetMapping("add")
    public String AddReview(@RequestParam("productId") Long productId, Model model) {
        AddReviewDto addReviewDto = new AddReviewDto();
        addReviewDto.setProductId(productId);
        model.addAttribute("addReview", addReviewDto);
        return "add-review";
    }

    @GetMapping("edit")
    public String EditReview(@RequestParam("id") Long id, Model model) {
        ReviewEntity review = reviewService.getReviewById(id);
        EditReviewDto editReviewDto = EditReviewDto.from(review);

        model.addAttribute("editReview", editReviewDto);
        return "edit-review";
    }
    
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
