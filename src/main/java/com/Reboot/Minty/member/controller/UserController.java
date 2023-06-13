package com.Reboot.Minty.member.controller;

import com.Reboot.Minty.member.dto.JoinDto;
import com.Reboot.Minty.member.dto.JoinLocationDto;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/join")
    public String joinForm(Model model, HttpSession session) {
        JoinDto joinDto = (JoinDto) session.getAttribute("joinDto");
        session.removeAttribute("joinDto");

        if (joinDto != null) {
            String mobile = joinDto.getMobile();
            if (mobile != null) {
                mobile = mobile.replace("+82", "0").replaceAll("\\s|-", "");
                joinDto.setMobile(mobile);
            }
            System.out.println(joinDto);
            model.addAttribute("joinDto", joinDto);
            model.addAttribute("readOnly", true);
        } else {
            model.addAttribute("joinDto", new JoinDto());
            model.addAttribute("readOnly", false);
        }

        return "member/join";
    }


    @PostMapping("/join")
    public String joinSubmit(@Valid JoinDto joinDto, BindingResult bindingResult, Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "member/join";
        }
        try {
            User user = User.saveUser(joinDto, passwordEncoder);
            userService.saveUser(user);
            Long userId = userService.getUserId(user.getEmail());
            session.removeAttribute("joinDto");
            session.setAttribute("userId", userId);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/join";
        }
        session.removeAttribute("joinDto");
        return "redirect:/map";
    }

    @GetMapping("/login")
    public String login() { return "member/login";}


    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "member/login";
    }


    @PostMapping("/saveLocation")
    public String saveLocation(@ModelAttribute JoinLocationDto joinLocationDto, HttpSession session , CsrfToken csrfToken) {
        // Get location and address information
        String csrfTokenValue = csrfToken.getToken();
        String csrfHeaderName = csrfToken.getHeaderName();

        // 토큰과 헤더를 사용하여 POST 요청에 CSRF 토큰 포함
        HttpHeaders headers = new HttpHeaders();
        headers.set(csrfHeaderName, csrfTokenValue);
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        String latitude = joinLocationDto.getLatitude();
        String longitude = joinLocationDto.getLongitude();
        String address = joinLocationDto.getAddress();

        // Get userId
        Long userId = (Long)session.getAttribute("userId");

        // Delegate the saving logic to the UserService
        userService.saveUserLocation(userId, latitude, longitude, address);

        return "redirect:/";
    }

    @GetMapping("/api/isLoggedIn")
    @ResponseBody
    public boolean isLoggedIn(HttpServletRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.isAuthenticated()? true : false;
    }
}
