package com.example.coursework.controllers;

import com.example.coursework.clients.UsersClient;
import com.example.coursework.dto.user.AuthorizeDao;
import com.example.coursework.dto.user.UserDto;
import com.example.coursework.exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("/store")
public class StoreController {
    UsersClient usersClient;
    @GetMapping
    public ModelAndView getMainPage(){
        return new ModelAndView("storePage");
    }
    @GetMapping("/login")
    public ModelAndView getLogin(@ModelAttribute("enterUser") AuthorizeDao dao){
        return new ModelAndView("login");
    }
    @GetMapping("/authorize")
    public ModelAndView getAuthorize(@ModelAttribute("enterUser") AuthorizeDao dao){
        return new ModelAndView("authorizePage");
    }
    @PostMapping("/authorize")
    public ModelAndView authorize(@Valid @ModelAttribute("enterUser") AuthorizeDao dao,
                                  BindingResult result){
        ModelAndView login = new ModelAndView("authorizePage");
        if(result.hasFieldErrors()){
            return login.addObject("enterUser", dao);
        }
        if (!dao.getPassword().equals(dao.getDoublePass())){
            return login.addObject("passNotMatch", "Пароли не совпадают");
        }
        if(usersClient.findExists(dao.getName())){
            return login.addObject("usersExits", "Пользователь с таким логином существует");
        }
        usersClient.saveUser(UserDto.builder()
                        .name(dao.getName())
                        .password(dao.getPassword())
                .build());
        return new ModelAndView("redirect:/store/login");
    }
}
