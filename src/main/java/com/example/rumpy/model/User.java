package com.example.rumpy.model;

import com.example.rumpy.entity_interface.HasEntityRecord;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends RootModel implements HasEntityRecord<User.EntityRecord> {
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "other_names")
    private String otherNames;

    private Role role = Role.USER;

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class EntityRecord implements Serializable {
        private String id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String email;
        private String phoneNumber;
        private Gender gender;
        private LocalDate dateOfBirth;
        private String firstName;
        private String lastName;
        private String otherNames;

        public String getName() {
            return (
                    Optional.ofNullable(firstName).orElse("")
                    + " " +
                    Optional.ofNullable(lastName).orElse("")
                    + " " +
                    Optional.ofNullable(otherNames).orElse("")
            ).strip();
        }//end method getName
    }//end class EntityRecord

    @Override
    public EntityRecord getEntityRecord() {
        return new EntityRecord(
                this.getId(),
                this.getCreatedAt(),
                this.getUpdatedAt(),
                this.getEmail(),
                this.getPhoneNumber(),
                this.getGender(),
                this.getDateOfBirth(),
                this.getFirstName(),
                this.getLastName(),
                this.getOtherNames()
        );
    }//end method getEntityRecord

    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////         RELATIONSHIPS
    ////////////////////////////////////////////////////////////////////////////////////
    @OneToMany(mappedBy = "user")
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ProductItem> productItems = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ProductReview> productReviews = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "cart_items", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "product_item_id"))
    private List<ProductItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<CustomerOrder> customerOrders = new ArrayList<>();
}//enc class User
