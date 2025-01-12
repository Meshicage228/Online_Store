package com.example.coursework.controllers.users;

import com.example.coursework.clients.OrderClient;
import com.example.coursework.clients.ProductClient;
import com.example.coursework.clients.UsersClient;
import com.example.coursework.domain.OrderStatus;
import com.example.coursework.dto.product.OrderDto;
import com.example.coursework.dto.product.ProductDto;
import com.example.coursework.dto.user.CurrentUserDto;
import com.example.coursework.dto.user.UserCard;
import com.example.coursework.dto.user.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("/store/users")
public class UsersController {
    UsersClient client;
    OrderClient orderClient;
    ProductClient productClient;

    @GetMapping("/profile")
    public ModelAndView getProfile(@NotNull @AuthenticationPrincipal CurrentUserDto user) {
        UserDto userDto = client.personalUser(user.getId());
        return new ModelAndView("userProfilePage").addObject("userInfo", userDto);
    }

    @GetMapping("/favorite")
    public ModelAndView getFavorite(@NotNull @AuthenticationPrincipal CurrentUserDto user) {
        UserDto userDto = client.personalUser(user.getId());
        return new ModelAndView("userFavoriteProductsPage").addObject("userInfo", userDto);
    }

    @PostMapping("/addCard")
    public String addCard(@NotNull @AuthenticationPrincipal CurrentUserDto user) {
        UserCard card = client.addNewCard(user.getId());
        user.setCard(card);
        return "redirect:/store/users/cart";
    }

    @GetMapping("/add_favorite/{prod_id}")
    public String addToFavorite(@NotNull @AuthenticationPrincipal CurrentUserDto user,
                                @PathVariable("prod_id") Integer prod_id,
                                HttpServletRequest request) {
        client.addToFavorite(user.getId(), prod_id);
        return "redirect:/" + request.getHeader("referer").substring(22);
    }

    @GetMapping("/remove_favorite/{prod_id}")
    public String removeFavorite(@NotNull @AuthenticationPrincipal CurrentUserDto userDto,
                                 @PathVariable("prod_id") Integer prod_id) {
        client.removeFavorite(userDto.getId(), prod_id);

        return "redirect:/store/users/favorite";
    }

    @PostMapping("/comment/{product_id}")
    public ModelAndView leaveCommentary(@RequestParam("commentary") String comment,
                                        @NotNull @AuthenticationPrincipal CurrentUserDto user,
                                        @PathVariable("product_id") Integer prod_id) {
        ModelAndView error = new ModelAndView("personalProductPage");
        ProductDto productById = productClient.findProductById(prod_id);
        error.addObject("product", productById);
        if (isNull(comment) || isBlank(comment)) {
            return error.addObject("emptyCommentary", "Пустой комментарий");
        }
        else if(orderClient.haveBoughtProduct(user.getId(), prod_id) || user.getRole().name().equals("ADMIN")) {
            client.leaveCommentary(user.getId(), prod_id, comment);
            return new ModelAndView("redirect:/store/catalog/" + prod_id);
        }
        return error
                .addObject("userNotBought", "Нельзя комментировать некупленный продукт");
    }

    @GetMapping("/history/{page}/{size}")
    public ModelAndView getOrders(@PathVariable("page") Integer page,
                                  @PathVariable("size") Integer size,
                                  @RequestParam(name = "status", required = false) OrderStatus status,
                                  @RequestParam(name = "name", required = false) String name,
                                  @RequestParam(name = "title", required = false) String title,
                                  @AuthenticationPrincipal CurrentUserDto user,
                                  @RequestParam(name = "sortedBy", required = false, defaultValue = "default") String sortedBy) {

        ModelAndView modelAndView = new ModelAndView("userPurchaseHistoryPage");
        Page<OrderDto> pageContent = orderClient.getOrders(page, size, status, name, title, user.getId(), sortedBy);

        modelAndView.addObject("userHistory", pageContent);
        int totalPages = pageContent.getTotalPages();
        if (totalPages > 0) {
            List<Integer> countOfButtons = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .toList();
            modelAndView.addObject("countPages", countOfButtons);
        }
        modelAndView.addObject("sort", sortedBy);
        modelAndView.addObject("status", status);
        modelAndView.addObject("searchTitle", title);
        modelAndView.addObject("searchName", name);
        return modelAndView;
    }
}
