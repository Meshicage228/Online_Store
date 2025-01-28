package com.example.userblservice.entity.product;

import com.example.userblservice.domain.OrderStatus;
import com.example.userblservice.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "purchases")
public class Purchases {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @ToString.Exclude
    private ProductEntity product;

    @ManyToOne
    @ToString.Exclude
    private UserEntity user;

    private Float bill;

    private Integer countOfProduct;

    private Float priceAtMomentBuying;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date dateOfPurchase;

}
