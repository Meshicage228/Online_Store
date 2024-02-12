package com.example.coursework.controllers.users;

import com.example.coursework.clients.CartClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("/store/users/cart")
public class UsersCart {
    CartClient client;

    @PatchMapping("/{cart_id}/changeCount")
    public void changeCount(@PathVariable("cart_id") Integer cart_id,
                            @RequestParam(value = "option", required = false) String option) {
        client.changeCount(cart_id, option);
    }

    @DeleteMapping("/{cart_id}")
    public void deleteFromCart(@PathVariable Integer cart_id) {
        client.deleteFromCart(cart_id);
    }

    @PostMapping("/{prod_id}")
    public String addToCart(@AuthenticationPrincipal CurrentUser user,
                            @PathVariable("prod_id") Integer prod_id,
                            HttpServletRequest request) {
        client.addToCart(user.getId(), prod_id);
        return "redirect:/" + request.getHeader("referer").substring(22);
    }
}
