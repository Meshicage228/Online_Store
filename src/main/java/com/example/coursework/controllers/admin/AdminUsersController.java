package com.example.coursework.controllers.admin;

import com.example.coursework.clients.UsersClient;
import com.example.coursework.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminUsersController {
    private final UsersClient client;

    @DeleteMapping("/{id}")
    public ModelAndView deleteUser(@PathVariable("id") final UUID id) {
        client.deleteUser(id);
        return new ModelAndView("redirect:/admin/users/0/16");
    }

    @GetMapping("/{page}/{size}")
    public ModelAndView getPage(@PathVariable(value = "page") final Integer page,
                                @PathVariable(value = "size") final Integer size,
                                @RequestParam(value = "name", required = false) final String name) {

        final ModelAndView modelAndView = new ModelAndView("adminUsersPage");
        final Page<UserDto> pageContent = client.getAllUsers(page, size, name);

        modelAndView.addObject("totalPage", pageContent);
        final int totalPages = pageContent.getTotalPages();
        if (totalPages > 0) {
            final List<Integer> countOfButtons = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .toList();
            modelAndView.addObject("countPages", countOfButtons);
        }
        modelAndView.addObject("searchedName", name);
        return modelAndView;
    }

    @PostMapping("/deleteComment/{comment_id}/{prod_id}")
    public String deleteCommentary(@PathVariable("comment_id") final Integer id,
                                   @PathVariable("prod_id") final Integer prod_id) {
        client.deleteComm(id);
        return "redirect:/store/catalog/" + prod_id;
    }
}
