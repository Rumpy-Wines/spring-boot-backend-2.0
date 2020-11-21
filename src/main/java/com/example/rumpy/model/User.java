package com.example.rumpy.model;

import com.example.rumpy.entity_interface.HasEntityRecord;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends RootModel implements HasEntityRecord<User.EntityRecord> {
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name="phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name="date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="other_names")
    private String otherNames;

    record EntityRecord(
            String id,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String email,
            String phoneNumber,
            Gender gender,
            LocalDate dateOfBirth,
            String firstName,
            String lastName,
            String otherNames
    ){}

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
}//enc class User
