package com.example.orderblservice.entity.product;

import com.example.orderblservice.domain.OrderStatus;
import com.example.orderblservice.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "purchases")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @ToString.Exclude
    private ProductEntity product;

    @ManyToOne
    @ToString.Exclude
    private UserEntity user;

    private Integer countOfProduct;

    private Float priceAtMomentBuying;

    private Float bill;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date dateOfPurchase;

}
