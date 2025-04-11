package com.tinystop.sjp.Auth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tinystop.sjp.Auth.AuthDto.ChangePasswordDto;
import com.tinystop.sjp.Auth.AuthDto.DeleteAccountDto;
import com.tinystop.sjp.Auth.AuthDto.SignUpDto;
import com.tinystop.sjp.Auth.AuthDto.SigninDto;
import com.tinystop.sjp.Exception.CustomException;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("")
    public String home() {
        return "index";
    }

    @GetMapping("signupPage") // signup page 
    public String SignupPage(Model model) {
        model.addAttribute("signup", new SignUpDto());
        return "signup";
    }
    
    @PostMapping("signup")
    public String SigUp(@ModelAttribute("signup") @Valid SignUpDto signupRequest, BindingResult bindingResult, Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("signup", signupRequest);
            return "signup";
        }
        this.authService.signUp(signupRequest, session);
        return "redirect:/signinPage";
    }

    @GetMapping("signinPage") // signin page 
    public String SigninPage(Model model) {
        model.addAttribute("signin", new SigninDto());
        return "signin";
    }
    
    @PostMapping("signin")
    public String SignIn(@ModelAttribute("signin") @Valid SigninDto signinRequest, BindingResult bindingResult, Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("signin", signinRequest);
            return "signin";
        }
        try {
            authService.signIn(signinRequest, session);
        } catch (CustomException error) {
            model.addAttribute("signin", signinRequest);
            model.addAttribute("errorMessage", error.getMessage());
            return "signin";
        }
        return "redirect:/";
    }
    
    @GetMapping("signout")
    public String SignOut(HttpServletRequest request, HttpServletResponse response) { //(클라이언트에서 보낸 HTTP 요청, 서버가 클라이언트에게 응답을 보낼 객체)
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication()); // SecurityContext에서 인증 정보 삭제
        return "redirect:/";
    }
    
    @GetMapping("change-info")
    public String ChangeInfoPage(Model model) {
        model.addAttribute("changePassword", new ChangePasswordDto());
        return "change-info";
    }
    
    @PostMapping("change-password")
    public String ChangePassword(@ModelAttribute("changePassword") @Valid ChangePasswordDto changePasswordRequest,
                                 BindingResult bindingResult,
                                 Model model,
                                 @AuthenticationPrincipal UserDetails userDetails, 
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "비밀번호 형식이 맞지 않습니다."); 
            return "change-info";
        }
        try {
            authService.changePassword(changePasswordRequest, userDetails.getUsername());
            new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
            redirectAttributes.addFlashAttribute("message", "비밀번호가 성공적으로 변경되었습니다. 다시 로그인해주세요.");
        } catch (CustomException error) {
            model.addAttribute("errorMessage", error.getMessage());
            return "change-info";
        }
        return "redirect:/";
    }

    @GetMapping("deleteAccountPage")
    public String DeleteAccountPage(Model model) {
        model.addAttribute("deleteAccount", new DeleteAccountDto());
        return "delete-account";
    }
    
    @PostMapping("deleteAccount") 
    public String DeleteAccount(@ModelAttribute("deleteAccount") @Valid DeleteAccountDto DeleteAccountRequest,
                                BindingResult bindingResult,
                                Model model,
                                @AuthenticationPrincipal UserDetails userDetails,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "비밀번호 형식이 맞지 않습니다."); 
            return "change-info";
        }
        try {
            authService.deleteAccount(DeleteAccountRequest, userDetails.getUsername());
            new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
            redirectAttributes.addFlashAttribute("message", "회원탈퇴가 성공적으로 마무리 되었습니다.");
        } catch (CustomException error) {
            model.addAttribute("errorMessage", error.getMessage());
            return "change-info"; // 에러메세지 띄우고 다시 보여주기
        }
        return "redirect:/";
    }

}