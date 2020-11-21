package com.example.rumpy.model;

import com.example.rumpy.entity_interface.HasEntityRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ShippingTrackAddress extends RootModel implements HasEntityRecord<ShippingTrackAddress.EntityRecord> {

    @Column(nullable = false)
    private Integer shippingOrder = 1;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String email;

    record EntityRecord(
            String id,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Integer order,
            String address,
            String name,
            String phoneNumber,
            String email
    ){}

    @Override
    public EntityRecord getEntityRecord() {
        return new EntityRecord(
                this.getId(),
                this.getCreatedAt(),
                this.getCreatedAt(),
                this.getShippingOrder(),
                this.getAddress(),
                this.getName(),
                this.getPhoneNumber(),
                this.getEmail()
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////         RELATIONSHIPS
    ////////////////////////////////////////////////////////////////////////////////////

    @ManyToOne
    @JoinColumn(nullable = false)
    private CustomerOrderItem customerOrderItem;
}//end class ShippingTrackAddress
