package com.example.userblservice.entity.product;


import com.example.userblservice.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "comments")
public class Commentary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    private UserEntity user;

    private String comment;
}
