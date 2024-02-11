package com.example.coursework.controllers.order;


import com.example.coursework.clients.OrderClient;
import com.example.coursework.domain.OrderStatus;
import com.example.coursework.dto.product.OrderDto;
import com.example.coursework.dto.product.ProductDto;
import com.example.coursework.utils.OrderSearchDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
//import org.springframework.security.access.annotation.Secured;
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
@RequestMapping("*/orders")
public class OrderController {
    OrderClient client;

    @GetMapping("/{page}/{size}")
    public ModelAndView getOrders(@PathVariable("page") Integer page,
                                  @PathVariable("size") Integer size,
                                  @RequestParam(name = "status", required = false) OrderStatus status,
                                  @RequestParam(name = "name", required = false) String name,
                                  @RequestParam(name = "title", required = false) String title,
                                  @RequestParam(name = "user_id", required = false) UUID user_id,
                                  @RequestParam(name = "sortedBy", required = false, defaultValue = "default") String sortedBy) {
        ModelAndView modelAndView = new ModelAndView("adminOrderPage");
        Page<OrderDto> pageContent = client.getOrders(page, size, status, name, title, user_id, sortedBy);

        modelAndView.addObject("orderContent", pageContent);
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

    @PostMapping("/create/{user_id}")
    public void createPurchase(@PathVariable("user_id") UUID id) {
        client.createPurchase(id);
    }
}
