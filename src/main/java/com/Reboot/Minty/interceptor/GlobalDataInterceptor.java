package com.Reboot.Minty.interceptor;

import com.Reboot.Minty.support.entity.Ad;
import com.Reboot.Minty.support.repository.AdRepository;
import com.Reboot.Minty.support.service.AdService;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class GlobalDataInterceptor implements HandlerInterceptor {

    private final AdRepository adRepository;

    public GlobalDataInterceptor(AdService adService, AdRepository adRepository) {
        this.adRepository = adRepository;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView != null && !modelAndView.isEmpty()) {
            List<Ad> approvedAds = adRepository.findByStatus("승인");
            int size = approvedAds.size();
            Random random = new Random();
            List<Ad> approvedImages = new ArrayList<>();
            for (Ad ad : approvedAds) {
                String imagePath = "adimage/" + ad.getImage();
                Resource resource = new ClassPathResource("static/" + imagePath);
                try {
                    if (resource.exists()) {
                        approvedImages.add(ad);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!approvedImages.isEmpty()) {
                int randomIndex = (int) (Math.random() * approvedImages.size());
                Ad ad = approvedImages.get(randomIndex);
                modelAndView.addObject("ad", ad);

                String imagePath = "adimage/";
            }
        }
    }
}