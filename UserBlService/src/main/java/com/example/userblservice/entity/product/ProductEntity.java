package com.example.userblservice.entity.product;

import com.example.userblservice.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(exclude = {"users_favorites", "users"})
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer id;
    private String title;
    private Float price;
    private Integer count;
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "product")
    private List<ProductImage> images;

    @OneToMany(mappedBy = "product")
    private List<Purchases> items;

    @OneToMany(mappedBy = "product")
    private List<Commentary> comments;

    @ManyToMany(mappedBy = "favoriteProducts")
    @ToString.Exclude
    private Set<UserEntity> users_favorites;

    @ManyToMany(mappedBy = "cart", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<UserEntity> users;

    @CreationTimestamp
    @Temporal(value = TemporalType.DATE)
    private Date creationTime;

    @UpdateTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date updateTime;

    @Version
    private Integer version;

}
