package com.example.adminblservice.entity.product;

import com.example.adminblservice.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
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
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "product")
    private List<ProductImage> images;

    @ManyToMany(mappedBy = "favoriteProducts")
    private List<UserEntity> users_favorites;

    @ManyToMany(mappedBy = "carts_of_users")
    private List<UserEntity> users;

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
        for (UserEntity user: this.users_favorites) {
            user.getFavoriteProducts().remove(this);
        }
        for (UserEntity user: this.users) {
            user.getCarts_of_users().remove(this);
        }
    }
}
