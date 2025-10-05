package com.example.userblservice.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
