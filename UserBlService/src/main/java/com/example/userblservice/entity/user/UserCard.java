package com.example.userblservice.entity.user;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "users_cards")
public class UserCard {
    @Id
    @SequenceGenerator(name = "crd_seq",
            sequenceName = "card_sequence",
            initialValue = 4, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crd_seq")
    private Integer id;

    @OneToOne
    @JsonIgnore
    @ToString.Exclude
    private UserEntity user;

    private Float money;
}
