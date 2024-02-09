package com.example.userblservice.entity.product;


import com.example.userblservice.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

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

    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date date;
}
