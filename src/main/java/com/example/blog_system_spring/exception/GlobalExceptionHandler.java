package com.example.blog_system_spring.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException e,
                                        HttpServletRequest request,
                                        RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/blog/list");
    }

    @ExceptionHandler(SecurityException.class)
    public String handleSecurity(SecurityException e,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/blog/list";
    }
}