package com.example.coursework.controllers;

import com.example.coursework.clients.ProductClient;
import com.example.coursework.clients.UsersClient;
import com.example.coursework.dto.product.ProductDto;
import com.example.coursework.dto.user.AuthorizeDao;
import com.example.coursework.dto.user.UserDto;
import com.example.coursework.utils.markers.AuthorizeValidationMarker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {
    private final UsersClient usersClient;
    private final ProductClient productClient;

    @GetMapping
    public ModelAndView getMainPage() {
        return new ModelAndView("storePage");
    }

    @GetMapping("/login")
    public ModelAndView getLogin(@ModelAttribute("enterUser") final AuthorizeDao dao) {
        return new ModelAndView("loginPage");
    }

    @GetMapping("/authorize")
    public ModelAndView getAuthorize(@ModelAttribute("enterUser") final AuthorizeDao dao) {
        return new ModelAndView("authorizePage");
    }

    @PostMapping("/authorize")
    public ModelAndView authorize(@Validated(value = AuthorizeValidationMarker.class) @ModelAttribute("enterUser") final AuthorizeDao dao,
                                  final BindingResult result) {
        final ModelAndView login = new ModelAndView("authorizePage");

        if (result.hasFieldErrors()) {
            return login.addObject("enterUser", dao);
        }

        if (!dao.getPassword().equals(dao.getDoublePass())) {
            return login.addObject("passNotMatch", "Пароли не совпадают");
        }
        if (usersClient.findExists(dao.getNameAuth())) {
            return login.addObject("usersExits", "Пользователь с таким логином существует");
        }
        usersClient.saveUser(UserDto.builder()
                .name(dao.getNameAuth())
                .password(dao.getPassword())
                .build());
        return new ModelAndView("redirect:/store/login");
    }

    @GetMapping("/catalog/{prod_id}")
    ModelAndView getPersonalPage(@PathVariable("prod_id") final Integer id) {
        final ModelAndView modelAndView = new ModelAndView("personalProductPage");
        final ProductDto productById = productClient.findProductById(id);
        modelAndView.addObject("product", productById);
        return modelAndView;
    }

    @GetMapping("/{page}/{size}")
    public ModelAndView getPage(@PathVariable(value = "page") final Integer page,
                                @PathVariable(value = "size") final Integer size,
                                @RequestParam(value = "title", required = false) final String title,
                                @RequestParam(value = "price", required = false) final Float price,
                                @RequestParam(value = "sortedBy", required = false) final String sortedBy) {

        final ModelAndView modelAndView = new ModelAndView("storePage");
        final Page<ProductDto> pageContent = productClient.getAllSearchPaginatedSortedProducts(page, size, title, price, sortedBy);

        modelAndView.addObject("totalPage", pageContent);
        final int totalPages = pageContent.getTotalPages();
        if (totalPages > 0) {
            final List<Integer> countOfButtons = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .toList();
            modelAndView.addObject("countPages", countOfButtons);
        }
        modelAndView.addObject("sort", sortedBy);
        modelAndView.addObject("searchTitle", title);
        return modelAndView;
    }
}
