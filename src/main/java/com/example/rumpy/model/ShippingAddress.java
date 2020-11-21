package com.example.rumpy.model;

import com.example.rumpy.entity_interface.HasEntityRecord;
import com.example.rumpy.util.MyStringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress extends AddressAbstractClass implements HasEntityRecord<ShippingAddress.EntityRecord>{


    record EntityRecord (
            String id,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String title,
            String streetAddress,
            String state,
            List<String> landmarks
    ){}

    @Override
    public EntityRecord getEntityRecord() {
        List<String> landmarks;

        landmarks = Arrays.asList(this.landmarks.strip().split(MyStringUtil.STRING_LIST_SEPARATOR));

        return new EntityRecord(
                this.getId(),
                this.getCreatedAt(),
                this.getUpdatedAt(),
                this.getTitle(),
                this.getStreetAddress(),
                this.getState(),
                landmarks
        );
    }//end method getEntityRecord

    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////         RELATIONSHIPS
    ////////////////////////////////////////////////////////////////////////////////////
    @OneToOne
    @JoinColumn(nullable = false)
    private CustomerOrder customerOrder;
}//end class
