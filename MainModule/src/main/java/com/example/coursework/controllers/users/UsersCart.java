package com.example.coursework.controllers.users;

import com.example.coursework.clients.CartClient;
import com.example.coursework.clients.OrderClient;
import com.example.coursework.clients.UsersClient;
import com.example.coursework.dto.product.ProductDto;
import com.example.coursework.dto.user.CurrentUserDto;
import com.example.coursework.dto.user.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import static java.util.Objects.isNull;

@Controller
@RequiredArgsConstructor
@RequestMapping("/store/users/cart")
public class UsersCart {
    private final CartClient client;
    private final UsersClient usersClient;
    private final OrderClient orderClient;

    @GetMapping
    public ModelAndView getCart(@NotNull @AuthenticationPrincipal final CurrentUserDto user) {
        final ModelAndView modelAndView = new ModelAndView("userCartPage");
        final UserDto userDto = usersClient.personalUser(user.getId());

        final double sum = userDto.getBasket().stream()
                .mapToDouble(ProductDto::getBill)
                .sum();
        final double countProducts = userDto.getBasket().stream()
                .mapToInt(ProductDto::getCount)
                .sum();

        modelAndView.addObject("countProducts", countProducts);
        modelAndView.addObject("totalBill", sum);
        modelAndView.addObject("noCardFound", "");
        return modelAndView.addObject("userInfo", userDto);
    }

    @PostMapping("/order/create")
    public ModelAndView createPurchase(@NotNull @AuthenticationPrincipal final CurrentUserDto user) {
        ModelAndView cart = getCart(user);
        if (isNull(user.getCard())) {
            return cart.addObject("noCardFound", "Не найдена привзянная карта!");
        }
        if (!orderClient.createPurchase(user.getId())) {
            return cart.addObject("notEnoughMoney", "На карте недостаточно средств!");
        }
        cart = getCart(user);
        return cart;
    }

    @PatchMapping("/{cart_id}/changeCount")
    public String changeCount(@PathVariable("cart_id") final Integer cart_id,
                              @RequestParam(value = "option", required = false) final String option) {
        client.changeCount(cart_id, option);
        return "redirect:/store/users/cart";
    }

    @DeleteMapping("/{cart_id}")
    public String deleteFromCart(@PathVariable("cart_id") final Integer cart_id) {
        client.deleteFromCart(cart_id);
        return "redirect:/store/users/cart";
    }

    @PostMapping("/{prod_id}")
    public String addToCart(@NotNull @AuthenticationPrincipal final CurrentUserDto user,
                            @PathVariable("prod_id") final Integer prod_id,
                            final HttpServletRequest request) {
        String substring = request.getHeader("referer").substring(22);
        client.addToCart(user.getId(), prod_id);
        if (substring.equals("store/users/comment/" + prod_id)) {
            substring = "store/catalog/" + prod_id;
        }
        return "redirect:/" + substring;
    }
}
