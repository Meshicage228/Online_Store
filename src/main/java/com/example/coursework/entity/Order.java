package com.example.coursework.entity;

import com.example.coursework.domain.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name= "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer order_id;

    private Integer user_id;

    @CreationTimestamp
    private Date created_time;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;
}
