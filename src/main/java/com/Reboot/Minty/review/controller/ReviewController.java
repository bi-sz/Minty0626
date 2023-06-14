package com.Reboot.Minty.review.controller;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.service.UserService;
import com.Reboot.Minty.review.dto.ReviewDto;
import com.Reboot.Minty.review.entity.Review;
import com.Reboot.Minty.review.service.ReviewService;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.trade.service.TradeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final TradeService tradeService;

    @Autowired
    public ReviewController(ReviewService reviewService, UserService userService, TradeService tradeService) {
        this.reviewService = reviewService;
        this.userService = userService;
        this.tradeService = tradeService;
    }

    // 리뷰 작성 폼을 보여줌
    @GetMapping("/review")
    public String showReviewForm(ReviewDto reviewDto, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");
        User user = userService.getUserInfo(userEmail);
        reviewDto.setNickname(user.getNickName());
        reviewDto.setId(user.getId());

        model.addAttribute("reviewDto", reviewDto);
        return "review/review-form";
    }

    // 리뷰를 생성함
    @PostMapping("/")
    public String createReview(@ModelAttribute("reviewDto") @Valid ReviewDto reviewDto, BindingResult bindingResult, Principal principal, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");
        User user = userService.getUserInfo(userEmail);
        reviewDto.setNickname(user.getNickName());

        if (bindingResult.hasErrors()) {
            // 유효성 검사 실패 시 처리 로직
            return "redirect/";
        }

        MultipartFile imageFile = reviewDto.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            String originalFilename = imageFile.getOriginalFilename();
            // 파일을 저장하는 로직을 구현해야 합니다. (예: Amazon S3, 로컬 디렉토리 등)
            // reviewDto.setImageFile(저장된 파일 경로 또는 파일명);
        }

        Long userId = user.getId();
        Long tradeId = reviewDto.getTradeBoard().getId();
        Trade trade = tradeService.getTradeById(tradeId);

        if (trade == null || (trade.getSellerId().getId() != userId && trade.getBuyerId().getId() != userId)) {
            // trade가 존재하지 않거나 현재 사용자가 해당 거래의 판매자 또는 구매자가 아닌 경우
            // 처리할 로직을 작성하세요.
            return "error";
        }

        if (trade.getSellerId().getId().equals(userId)) {
            reviewDto.setSellerId(trade.getSellerId());
            reviewDto.setBuyerId(trade.getBuyerId());
        } else if (trade.getBuyerId().getId().equals(userId)) {
            reviewDto.setSellerId(trade.getSellerId());
            reviewDto.setBuyerId(trade.getBuyerId());
        }

        reviewService.createReview(reviewDto);
        userService.increaseExp(userEmail, 10);

        return "redirect:/";
    }

    // 특정 ID에 해당하는 리뷰를 삭제함
    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return "redirect:/";
    }

    // 내가 쓴 후기만 조회
    @GetMapping("/my-review")
    public String showMyReview(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");
        User user = userService.getUserInfo(userEmail);
        Long userId = user.getId();

        List<Review> myReviews = reviewService.getReviewsByUserId(userId);

        model.addAttribute("myReviews", myReviews);
        return "review/my-review";
    }

    // 내가 받은 후기 조회
    @GetMapping("/reviews-received")
    public String showReceivedReviews(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");
        User user = userService.getUserInfo(userEmail);
        Long userId = user.getId();

        List<Review> receivedReviews = reviewService.getReceivedReviews(userId);

        model.addAttribute("receivedReviews", receivedReviews);
        return "review/reviews-received";
    }

}
