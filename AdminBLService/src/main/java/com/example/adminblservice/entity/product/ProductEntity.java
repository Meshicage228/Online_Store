package com.example.adminblservice.entity.product;

import com.example.adminblservice.entity.user.Commentary;
import com.example.adminblservice.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


// TODO: 23.01.2024 Change this entity from user bl
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(exclude = {"users_favorites", "users", "items", "comments"})
@Builder
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

    @OneToMany(mappedBy = "product", orphanRemoval = true)
    private List<Purchases> items;

    @OneToMany(mappedBy = "product", orphanRemoval = true)
    private List<Commentary> comments;

    @ManyToMany(mappedBy = "favoriteProducts")
    private Set<UserEntity> users_favorites;

    @ManyToMany(mappedBy = "cart")
    private Set<UserEntity> users;

    @CreationTimestamp
    @Temporal(value = TemporalType.DATE)
    private Date creationTime;

    @UpdateTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date updateTime;

    @Version
    private Integer version;

    public void addImage(byte[] image){
        if(images == null){
            images = new ArrayList<>();
        }
        ProductImage newImage = ProductImage.builder()
                .product(this)
                .image(image)
                .build();

        images.add(newImage);
    }
    @PreRemove
    public void removeProductAssociations(){
        for (var user: this.users_favorites) {
            user.getFavoriteProducts().remove(this);
        }
        for (var user: this.users) {
            user.getCart().remove(this);
        }
    }
}
