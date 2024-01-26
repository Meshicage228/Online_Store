package com.example.orderblservice.entity.user;


import com.example.orderblservice.domain.Role;
import com.example.orderblservice.entity.product.Orders;
import com.example.orderblservice.entity.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
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
    private List<Orders> purchases;

    @OneToOne(mappedBy = "user")
    private UserCard userCard;

    @OneToMany(mappedBy = "user")
    private List<UsersCart> cart;
}
