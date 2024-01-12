package com.example.coursework.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("")
public class MainPageController {
    @GetMapping
    public ModelAndView getMainPage(){
        return new ModelAndView("adminPage");
    }
}
