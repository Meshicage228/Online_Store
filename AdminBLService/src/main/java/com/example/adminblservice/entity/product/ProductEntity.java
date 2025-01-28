package com.example.adminblservice.entity.product;

import com.example.adminblservice.entity.user.UsersCart;
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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = {"users_favorites", "users", "items", "comments"})
@Builder
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @SequenceGenerator(name = "prod_seq",
            sequenceName = "product_sequence",
            initialValue = 22, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prod_seq")
    @Column(name = "product_id")
    private Integer id;
    private String title;
    private Float price;
    private Integer count;
    private String description;

    @OneToMany( mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Purchases> items;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Commentary> comments;

    @ManyToMany(mappedBy = "favoriteProducts", cascade = CascadeType.ALL)
    private Set<UserEntity> users_favorites;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<UsersCart> users;

    @CreationTimestamp
    @Temporal(value = TemporalType.DATE)
    private Date creationTime;

    @UpdateTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date updateTime;

    @Version
    private Integer version;

    public void addImage(byte[] image){
        if(images.isEmpty()){
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
    }
}
