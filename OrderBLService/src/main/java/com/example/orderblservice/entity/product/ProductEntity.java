package com.example.orderblservice.entity.product;

import com.example.orderblservice.entity.user.UsersCart;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer id;
    private String title;
    private Float price;
    private Integer count;
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "product")
    private List<ProductImage> images;

    @OneToMany(mappedBy = "product")
    private List<Orders> items;

    @OneToMany(mappedBy = "product")
    private Set<UsersCart> users;
}
