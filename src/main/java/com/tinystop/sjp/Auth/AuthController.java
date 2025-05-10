package com.tinystop.sjp.Auth;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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

    /** signup.html로 이동
     */
    @GetMapping("signupPage")
    public String SignupPage(Model model) {
        model.addAttribute("signup", new SignUpDto()); // SignUpDto 전달
        return "signup"; // signup.html로 return
    }
    
    /** 회원가입 요청 처리
     * - 입력값 유효성 검사후 오류가 있을 경우 singnup 으로 반환
     * - 유효할 시 account entity에 유저 정보 저장 후 signin 페이지로 redirect
     * @param signupRequest 사용자 입력한 데이터 -> username, password, email 포함되어있음
     * @param bindingResult 입력값 검증 결과를 담는 객체 (에러가 있는 경우 처리용)
     * @param model 클라이언트에 전달할 데이터를 담은 객체, 이 함수에서는 error message를 클라이언트에 전달
     * @return 회원가입 실패 시 "signup" 뷰 반환, 성공 시 signinPage로 리다이렉트
     */
    @PostMapping("signup")
    public String SigUp(@ModelAttribute("signup") @Valid SignUpDto signupRequest, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) { // 입력값에 오류가 있을 경우
            List<String> errors = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList()); // 에러 메세지 리스트
            model.addAttribute("errorMessage", errors); // 에러 메세지 전달
            model.addAttribute("signup", signupRequest); // 에러나기전 유저가 입력한 데이터 다시 전달용, 단 패스워드는 제외
            return "signup"; // 원인이 담긴 에러메세지를 signup.html에 띄워서 보여주기
        }
        authService.signUp(signupRequest); // 데이터에 오류가 없을 시 회원가입
        return "redirect:/signinPage"; // sign.html로 redirect
    }

    /** Signin.html로 이동
     */
    @GetMapping("signinPage") 
    public String SigninPage(Model model) {
        model.addAttribute("signin", new SigninDto()); // SigninDto 전달
        return "signin"; // singin.html로 return
    }
    
    /** 로그인 요청 처리
     * - 입력값 유효성 검사후 오류가 있을 경우 singnin 으로 다시 반환
     * - 유효할 시 session에 유저 정보 저장 후 main 으로 redirect
     * @param signinRequest 로그인 form에서 전달된 사용자 입력 데이터 -> username, password 포함되어있음
     * @param bindingResult 입력값 검증 결과를 담는 객체 (에러가 있는 경우 처리용)
     * @param model 클라이언트에 전달할 데이터를 담은 객체
     * @param session 로그인 성공 시 사용자 정보를 저장하기 위한 HttpSession 객체
     * @return 성공시 메인페이지로 이동, 실패시 다시 signin 페이지로 이동
     */
    @PostMapping("signin")
    public String SignIn(@ModelAttribute("signin") @Valid SigninDto signinRequest, BindingResult bindingResult, Model model, HttpSession session) {
        if (bindingResult.hasErrors()) { // 입력값 유효성 검사후 오류가 있을 경우
            List<String> errors = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList()); // 에러 메세지 리스트
            model.addAttribute("errorMessage", errors); // 에러 메세지 전달
            model.addAttribute("signin", signinRequest); // 에러나기전 유저가 입력한 데이터 다시 전달용
            return "signin"; // 다시 singin.html로 return
        }
        try {
            authService.signIn(signinRequest, session); // 유저가 입력한 데이터를 통해 로그인 시도
        } catch (CustomException error) { // 문제가 있을 경우
            model.addAttribute("signin", signinRequest); // 에러나기전 유저가 입력한 데이터 다시 전달용
            model.addAttribute("errorMessage", error.getMessage()); // 에러 메세지 전달
            return "signin"; // 원인이 담긴 에러메세지를 signin.html에 띄워서 보여주기
        }
        return "redirect:/"; // 메인페이지로 redirect
    }
    /** 로그아웃 처리
     * - 현재 인증 정보를 SecurityContext에서 제거하고 세션을 무효화한 뒤 메인페이지로 이동
     */
    @GetMapping("signout")
    public String SignOut(HttpServletRequest request, HttpServletResponse response) { //(클라이언트에서 보낸 HTTP 요청, 서버가 클라이언트에게 응답을 보낼 객체)
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication()); // SecurityContext에서 인증 정보 삭제
        return "redirect:/"; // 메인페이지로 redirect
    }
    
    /** change-info.html로 이동
     */
    @GetMapping("change-info")
    public String ChangeInfoPage(Model model) {
        model.addAttribute("changePassword", new ChangePasswordDto()); //ChangePasswordDto 전달
        return "change-info"; // change-info.html로 return
    }
    
    /** 비밀번호 변경 처리
     * - 입력값 유효성 검사 후, 유저의 비밀번호 변경한 다음 현재 세션 종료시키고 메인페이지로 리다이렉트함.
     * @param changePasswordRequest 사용자가 입력한 변경할 비밀번호 데이터 -> (현재 비밀번호, 새 비밀번호)
     * @param bindingResult 입력값 검증 결과를 담는 객체 (에러가 있는 경우 처리용)
     * @param model 클라이언트에 전달할 데이터를 담은 객체
     * @param userDetails 현재 로그인한 사용자 정보
     * @param request 현재 HTTP 요청
     * @param response 현재 HTTP 응답
     * @param redirectAttributes 비밀번호 변경 성공시 flash message 출력
     * @return 성공시 메인페이지, 실패시 change-info.html
     */
    @PostMapping("change-password")
    public String ChangePassword(@ModelAttribute("changePassword") @Valid ChangePasswordDto changePasswordRequest,
                                 BindingResult bindingResult,
                                 Model model,
                                 @AuthenticationPrincipal UserDetails userDetails, 
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) { // 입력값 유효성 검사후 오류가 있을 경우
            model.addAttribute("errorMessage", "비밀번호 형식이 맞지 않습니다."); // 에러 메세지 전송
            return "change-info"; // change-info.html로 return
        }
        try {
            authService.changePassword(changePasswordRequest, userDetails.getUsername()); // 유저가 입력한 데이터가 담긴 ChangePasswordDto를 통해 비밀번호 변경
            new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication()); // 세션 로그아웃
            redirectAttributes.addFlashAttribute("message", "비밀번호가 성공적으로 변경되었습니다. 다시 로그인해주세요."); // flash message 전달
        } catch (CustomException error) { // 문제가 있을 경우
            model.addAttribute("errorMessage", error.getMessage()); // 에러 메세지 전달
            return "change-info"; // 원인이 담긴 에러메세지를 change-info.html에 띄워서 보여주기
        }
        return "redirect:/"; // 메인페이지로 redirect
    }

    /** delete-account.html로 이동
     */
    @GetMapping("deleteAccountPage")
    public String DeleteAccountPage(Model model) {
        model.addAttribute("deleteAccount", new DeleteAccountDto()); // DeleteAccountDto 전달
        return "delete-account"; //delete-accounto.html로 return
    }
    
    /**
     * 아이디 삭제 처리
     * - 입력값 유효성 검사 후, 유저의 회원탈퇴한 다음 메인페이지로 리다이렉트함.
     * @param deleteAccountRequest 사용자가 입력한 데이터 (현재 비밀번호, 비밀번호 재확인)
     * @param bindingResult 입력값 검증 결과를 담는 객체
     * @param model 클라이언트에 전달할 데이터를 담은 객체
     * @param userDetails 현재 로그인한 사용자 정보 
     * @param request 현재 HTTP 요청
     * @param response 현재 HTTP 응답
     * @param redirectAttributes 회원탈퇴 성공시 flash message 출력
     * @return 성공시 메인페이지, 실패시 delete-account.html
     */
    @PostMapping("deleteAccount") 
    public String DeleteAccount(@ModelAttribute("deleteAccount") @Valid DeleteAccountDto deleteAccountRequest,
                                BindingResult bindingResult,
                                Model model,
                                @AuthenticationPrincipal UserDetails userDetails,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) { // 입력값 유효성 검사후 오류가 있을 경우
            model.addAttribute("errorMessage", "비밀번호 형식이 맞지 않습니다.");  // 에러 메세지 전송
            return "delete-account"; //delete-accounto.html로 return
        }
        try {
            authService.deleteAccount(deleteAccountRequest, userDetails.getUsername()); // 유저가 입력한 데이터가 담긴 DeleteAccountDto 통해 회원탈퇴
            new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication()); // 세션 로그아웃
            redirectAttributes.addFlashAttribute("message", "회원탈퇴가 성공적으로 마무리 되었습니다."); // flash message 전달
        } catch (CustomException error) { // 문제가 있을 경우
            model.addAttribute("errorMessage", error.getMessage()); // 에러 메세지 전달
            return "delete-account"; // 원인이 담긴 에러메세지를 delete-account.html에 띄워서 보여주기
        }
        return "redirect:/"; // 메인페이지로 redirect
    }

}