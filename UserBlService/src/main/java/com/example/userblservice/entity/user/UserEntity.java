package com.example.userblservice.entity.user;


import com.example.userblservice.entity.product.Commentary;
import com.example.userblservice.entity.product.ProductEntity;
import com.example.userblservice.entity.product.Purchases;
import jakarta.persistence.*;
import lombok.*;
import com.example.userblservice.domain.Role;

import java.util.*;

import static java.util.Objects.isNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(exclude = {"favoriteProducts", "cart", "purchases", "commentaries"})
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;

    @Column(unique = true)
    private String name;

    @Lob
    private byte[] avatar;

    private String password;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Purchases> purchases;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Commentary> commentaries;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_favorite_products",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<ProductEntity> favoriteProducts;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_carts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<ProductEntity> cart;

    public boolean AddToCart(ProductEntity entity) {
        if (isNull(cart)) {
            cart = new HashSet<>();
        }
        cart.add(entity);
        entity.getUsers().add(this);
        return true;
    }

    public boolean AddToFavorite(ProductEntity entity) {
        if (isNull(favoriteProducts)) {
            favoriteProducts = new HashSet<>();
        }
        favoriteProducts.add(entity);
        entity.getUsers().add(this);
        return true;
    }
}
