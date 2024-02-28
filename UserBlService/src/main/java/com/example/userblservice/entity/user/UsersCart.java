package com.example.userblservice.entity.user;


import com.example.userblservice.entity.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "user_carts", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
public class UsersCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(
       name = "product_id",
       foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT)
    )
    @ToString.Exclude
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(
       name = "user_id",
       foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT)
    )
    @ToString.Exclude
    private UserEntity user;

    private Integer countToBuy;
}
