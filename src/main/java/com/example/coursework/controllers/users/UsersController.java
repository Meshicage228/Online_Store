package com.example.coursework.controllers.users;

import com.example.coursework.clients.UsersClient;
import com.example.coursework.dto.user.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
//import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("/store/users")
public class UsersController {
    UsersClient client;

    @GetMapping("/{user_id}")
    public UserDto getPersonal(@PathVariable("user_id") UUID id) {
        return client.personalUser(id);
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

    @PostMapping("/card")
    public void addNewCard(@AuthenticationPrincipal CurrentUser user) {
        client.addNewCard(user.getId());
    }
}
