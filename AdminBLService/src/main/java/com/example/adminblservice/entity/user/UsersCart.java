package com.example.adminblservice.entity.user;

import com.example.adminblservice.entity.product.ProductEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(
       name = "user_id",
       foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT)
    )
    private UserEntity user;

    private Integer countToBuy;
}
