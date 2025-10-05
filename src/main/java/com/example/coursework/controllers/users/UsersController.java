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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Controller
@RequiredArgsConstructor
@RequestMapping("/store/users")
public class UsersController {
    private final UsersClient client;
    private final OrderClient orderClient;
    private final ProductClient productClient;

    @GetMapping("/profile")
    public ModelAndView getProfile(@NotNull @AuthenticationPrincipal final CurrentUserDto user) {
        final UserDto userDto = client.personalUser(user.getId());
        return new ModelAndView("userProfilePage").addObject("userInfo", userDto);
    }

    @GetMapping("/favorite")
    public ModelAndView getFavorite(@NotNull @AuthenticationPrincipal final CurrentUserDto user) {
        final UserDto userDto = client.personalUser(user.getId());
        return new ModelAndView("userFavoriteProductsPage").addObject("userInfo", userDto);
    }

    @PostMapping("/addCard")
    public String addCard(@NotNull @AuthenticationPrincipal final CurrentUserDto user) {
        final UserCard card = client.addNewCard(user.getId());
        user.setCard(card);
        return "redirect:/store/users/cart";
    }

    @GetMapping("/add_favorite/{prod_id}")
    public String addToFavorite(@NotNull @AuthenticationPrincipal final CurrentUserDto user,
                                @PathVariable("prod_id") final Integer prod_id,
                                final HttpServletRequest request) {
        client.addToFavorite(user.getId(), prod_id);
        return "redirect:/" + request.getHeader("referer").substring(22);
    }

    @GetMapping("/remove_favorite/{prod_id}")
    public String removeFavorite(@NotNull @AuthenticationPrincipal final CurrentUserDto userDto,
                                 @PathVariable("prod_id") final Integer prod_id) {
        client.removeFavorite(userDto.getId(), prod_id);

        return "redirect:/store/users/favorite";
    }

    @PostMapping("/comment/{product_id}")
    public ModelAndView leaveCommentary(@RequestParam("commentary") final String comment,
                                        @NotNull @AuthenticationPrincipal final CurrentUserDto user,
                                        @PathVariable("product_id") final Integer prod_id) {
        final ModelAndView error = new ModelAndView("personalProductPage");
        final ProductDto productById = productClient.findProductById(prod_id);
        error.addObject("product", productById);
        if (isNull(comment) || isBlank(comment)) {
            return error.addObject("emptyCommentary", "Пустой комментарий");
        } else if (orderClient.haveBoughtProduct(user.getId(), prod_id) || user.getRole().name().equals("ADMIN")) {
            client.leaveCommentary(user.getId(), prod_id, comment);
            return new ModelAndView("redirect:/store/catalog/" + prod_id);
        }
        return error
                .addObject("userNotBought", "Нельзя комментировать некупленный продукт");
    }

    @GetMapping("/history/{page}/{size}")
    public ModelAndView getOrders(@PathVariable("size") final Integer size,
                                  @PathVariable("page") final Integer page,
                                  @RequestParam(name = "status", required = false) final OrderStatus status,
                                  @RequestParam(name = "name", required = false) final String name,
                                  @RequestParam(name = "title", required = false) final String title,
                                  @AuthenticationPrincipal final CurrentUserDto user,
                                  @RequestParam(name = "sortedBy", required = false, defaultValue = "default") final String sortedBy) {
        final ModelAndView modelAndView = new ModelAndView("userPurchaseHistoryPage");
        final Page<OrderDto> pageContent = orderClient.getOrders(page, size, status, name, title, user.getId(), sortedBy);

        modelAndView.addObject("userHistory", pageContent);
        final int totalPages = pageContent.getTotalPages();
        if (totalPages > 0) {
            final List<Integer> countOfButtons = IntStream.rangeClosed(1, totalPages)
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
