package by.meshicage.analyzerservice.entity.product;

import by.meshicage.analyzerservice.entity.user.Commentary;
import by.meshicage.analyzerservice.entity.user.UserEntity;
import by.meshicage.analyzerservice.entity.user.UsersCart;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ProductImage> images;

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

    @PreRemove
    public void removeProductAssociations() {
        for (final var user : this.users_favorites) {
            user.getFavoriteProducts().remove(this);
        }
    }
}
