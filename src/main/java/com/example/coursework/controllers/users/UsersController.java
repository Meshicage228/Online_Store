package com.example.coursework.controllers.users;

import com.example.coursework.clients.OrderClient;
import com.example.coursework.clients.UsersClient;
import com.example.coursework.domain.OrderStatus;
import com.example.coursework.dto.product.OrderDto;
import com.example.coursework.dto.user.CurrentUser;
import com.example.coursework.dto.user.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.remove;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("/store/users")
public class UsersController {
    UsersClient client;
    OrderClient orderClient;

    @GetMapping("/profile")
    public ModelAndView getProfile(@AuthenticationPrincipal CurrentUser user) {
        UserDto userDto = client.personalUser(user.getId());
        return new ModelAndView("userProfilePage").addObject("userInfo", userDto);
    }
    @GetMapping("/favorite")
    public ModelAndView getFavorite(@AuthenticationPrincipal CurrentUser user) {
        UserDto userDto = client.personalUser(user.getId());
        return new ModelAndView("userFavoriteProductsPage").addObject("userInfo", userDto);
    }

    @GetMapping("/card")
    public String getCardPage(){
        return "userRegisterCardPage";
    }

    @PostMapping("/addCard")
    public String addCard(@AuthenticationPrincipal CurrentUser user) {
        client.addNewCard(user.getId());

        return "redirect:/store/users/cart";
    }

    @PutMapping
    public UserDto updateUser(@AuthenticationPrincipal CurrentUser user,
                              @Valid @ModelAttribute UserDto dto,
                              BindingResult result) {
        return client.updateUser(user.getId(), dto);
    }

    @PostMapping("/add_favorite/{prod_id}")
    public String addToFavorite(@AuthenticationPrincipal CurrentUser user,
                                @PathVariable("prod_id") Integer prod_id,
                                HttpServletRequest request) {
        client.addToFavorite(user.getId(), prod_id);
        return "redirect:/" + request.getHeader("referer").substring(22);
    }

    @PostMapping("/comment/{product_id}")
    public ModelAndView leaveCommentary(@RequestParam("commentary") String comment,
                                        @AuthenticationPrincipal CurrentUser user,
                                        @PathVariable("product_id") Integer prod_id) {
        ModelAndView modelAndView = new ModelAndView("redirect:/store/catalog/" + prod_id);
        if (isNull(comment) || isBlank(comment)) {
            return modelAndView;
        }
        client.leaveCommentary(user.getId(), prod_id, comment);
        return modelAndView;
    }

    @GetMapping("/history/{page}/{size}")
    public ModelAndView getOrders(@PathVariable("page") Integer page,
                                  @PathVariable("size") Integer size,
                                  @RequestParam(name = "status", required = false) OrderStatus status,
                                  @RequestParam(name = "name", required = false) String name,
                                  @RequestParam(name = "title", required = false) String title,
                                  @AuthenticationPrincipal CurrentUser user,
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
