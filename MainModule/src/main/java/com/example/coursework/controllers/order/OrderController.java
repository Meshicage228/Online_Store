package com.example.coursework.controllers.order;

import com.example.coursework.clients.OrderClient;
import com.example.coursework.domain.OrderStatus;
import com.example.coursework.dto.product.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class OrderController {
    private final OrderClient client;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{page}/{size}")
    public ModelAndView getOrders(@PathVariable("page") final Integer page,
                                  @PathVariable("size") final Integer size,
                                  @RequestParam(name = "status", required = false) final OrderStatus status,
                                  @RequestParam(name = "name", required = false) final String name,
                                  @RequestParam(name = "title", required = false) final String title,
                                  @RequestParam(name = "user_id", required = false) final UUID user_id,
                                  @RequestParam(name = "sortedBy", required = false, defaultValue = "default") final String sortedBy) {
        final ModelAndView modelAndView = new ModelAndView("adminOrderPage");
        final Page<OrderDto> pageContent = client.getOrders(page, size, status, name, title, user_id, sortedBy);

        modelAndView.addObject("orderContent", pageContent);
        int totalPages = pageContent.getTotalPages();
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
