package com.example.coursework.controllers.users;

import com.example.coursework.clients.CartClient;
import com.example.coursework.clients.OrderClient;
import com.example.coursework.clients.UsersClient;
import com.example.coursework.dto.product.ProductDto;
import com.example.coursework.dto.user.CurrentUserDto;
import com.example.coursework.dto.user.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import static java.util.Objects.isNull;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("/store/users/cart")
public class UsersCart {
    CartClient client;
    UsersClient usersClient;
    OrderClient orderClient;

    @GetMapping
    public ModelAndView getCart(@AuthenticationPrincipal CurrentUserDto user) {
        ModelAndView modelAndView = new ModelAndView("userCartPage");
        UserDto userDto = usersClient.personalUser(user.getId());

        double sum = userDto.getBasket().stream()
                .mapToDouble(ProductDto::getBill)
                .sum();
        double countProducts = userDto.getBasket().stream()
                .mapToInt(ProductDto::getCount)
                .sum();

        modelAndView.addObject("countProducts", countProducts);
        modelAndView.addObject("totalBill", sum);
        modelAndView.addObject("noCardFound", "");
        return modelAndView.addObject("userInfo", userDto);
    }

    @PostMapping("/order/create")
    public ModelAndView createPurchase(@AuthenticationPrincipal CurrentUserDto user) {
        ModelAndView cart = getCart(user);
        if (isNull(user.getCard())) {
            return cart.addObject("noCardFound", "Не найдена привзянная карта!");
        }
        if (!orderClient.createPurchase(user.getId())) {
            return cart.addObject("notEnoughMoney", "На карте недостаточно средств!");
        }
        ;
        cart = getCart(user);
        return cart;
    }

    @PatchMapping("/{cart_id}/changeCount")
    public String changeCount(@PathVariable("cart_id") Integer cart_id,
                              @RequestParam(value = "option", required = false) String option) {
        client.changeCount(cart_id, option);
        return "redirect:/store/users/cart";
    }

    @DeleteMapping("/{cart_id}")
    public String deleteFromCart(@PathVariable("cart_id") Integer cart_id) {
        client.deleteFromCart(cart_id);
        return "redirect:/store/users/cart";
    }

    @PostMapping("/{prod_id}")
    public String addToCart(@AuthenticationPrincipal CurrentUserDto user,
                            @PathVariable("prod_id") Integer prod_id,
                            HttpServletRequest request) {
        client.addToCart(user.getId(), prod_id);
        return "redirect:/" + request.getHeader("referer").substring(22);
    }
}
