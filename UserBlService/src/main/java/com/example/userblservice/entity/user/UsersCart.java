package com.example.userblservice.entity.user;

import com.example.userblservice.entity.product.ProductEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
