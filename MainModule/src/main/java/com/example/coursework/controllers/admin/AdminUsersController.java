package com.example.coursework.controllers.admin;

import com.example.coursework.clients.UsersClient;
import com.example.coursework.dto.user.UserDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminUsersController {
    UsersClient client;

    @DeleteMapping("/{id}")
    public ModelAndView deleteUser(@PathVariable("id") UUID id) {
        client.deleteUser(id);
        return new ModelAndView("redirect:/admin/users/0/16");
    }

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
    @PostMapping("/deleteComment/{comment_id}/{prod_id}")
    public String deleteCommentary(@PathVariable("comment_id") Integer id,
                                   @PathVariable("prod_id") Integer prod_id){
        client.deleteComm(id);
        return "redirect:/store/catalog/" + prod_id;
    }
}
