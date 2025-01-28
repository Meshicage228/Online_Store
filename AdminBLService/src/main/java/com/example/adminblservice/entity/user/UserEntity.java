package com.example.adminblservice.entity.user;

import com.example.adminblservice.entity.product.ProductEntity;
import com.example.adminblservice.entity.product.Purchases;
import jakarta.persistence.*;
import lombok.*;
import com.example.adminblservice.domain.Role;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(exclude = {"favoriteProducts", "carts", "purchases", "commentaries"})
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Purchases> purchases;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Commentary> commentaries;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_favorite_products",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<ProductEntity> favoriteProducts;

    @OneToMany(mappedBy = "user")
    private Set<UsersCart> carts;
}
