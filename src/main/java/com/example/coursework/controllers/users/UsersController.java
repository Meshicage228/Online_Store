package com.example.coursework.controllers.users;

import com.example.coursework.clients.UsersClient;
import com.example.coursework.dto.user.UserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
//import org.springframework.security.access.annotation.Secured;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("*/users")
public class UsersController {
    UsersClient client;
//    @Secured("hasRole('ROLE_ADMIN')")
    @GetMapping("/{page}/{size}")
    public ModelAndView getPage(@PathVariable(value = "page") Integer page,
                                @PathVariable(value = "size") Integer size,
                                @RequestParam(value = "name", required = false) String name) {

        ModelAndView modelAndView = new ModelAndView("adminUsersPage");
        Page<UserDto> pageContent = client.getAllUsers(page, size, name);

        modelAndView.addObject("totalPage", pageContent);
        int totalPages = pageContent.getTotalPages();
        if (totalPages > 0) {
            List<Integer> countOfButtons = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .toList();
            modelAndView.addObject("countPages", countOfButtons);
        }
        modelAndView.addObject("searchedName", name);
        return modelAndView;
    }
//    @Secured("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ModelAndView deleteUser(@PathVariable("id") UUID id) {
        client.deleteUser(id);
        return new ModelAndView("redirect:/admin/users/0/5");
    }

    @GetMapping("/{user_id}")
    public UserDto getPersonal(@PathVariable("user_id") UUID id) {
        return client.personalUser(id);
    }
//    @Secured("hasRole('ROLE_ADMIN')")
    @PutMapping("/{user_id}")
    public UserDto updateUser(@PathVariable("user_id") UUID id,
                              @Valid @ModelAttribute UserDto dto,
                              BindingResult result) {
        return client.updateUser(id, dto);
    }
    @PostMapping(value = "/save", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    UserDto saveUser(@ModelAttribute UserDto dto){
        return client.saveUser(dto);
    }

    @PostMapping("/{user_id}/add_favorite/{prod_id}")
    public void addToFavorite(@PathVariable("user_id") UUID user_id,
                              @PathVariable("prod_id") Integer prod_id) {
        client.addToFavorite(user_id, prod_id);
    }
    @PostMapping("/{user_id}/comment/{product_id}")
    public void leaveCommentary(@RequestParam("commentary") String comment,
                                @PathVariable("user_id") UUID user_id,
                                @PathVariable("product_id") Integer prod_id){
        client.leaveCommentary(user_id, prod_id, comment);
    }
    @PostMapping("/card/{user_id}")
    public void addNewCard(@PathVariable("user_id") UUID user_id){
        client.addNewCard(user_id);
    }
}
