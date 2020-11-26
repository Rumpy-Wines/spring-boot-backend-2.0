package com.example.rumpy.model;

import com.example.rumpy.entity_interface.HasEntityRecord;
import com.example.rumpy.util.MyStringUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress extends AddressAbstractClass implements HasEntityRecord<ShippingAddress.EntityRecord>{

    public static ShippingAddress fromAddress(Address address) {
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setTitle(address.getTitle());
        shippingAddress.setCity(address.getCity());
        shippingAddress.setStreetAddress(address.getStreetAddress());
        shippingAddress.setState(address.getState());

        List<String> landmarks = Arrays.asList(address.getLandmarks().strip().split(MyStringUtil.STRING_LIST_SEPARATOR));

        shippingAddress.setLandmarks(landmarks);

        return shippingAddress;
    }//end method convertAddressToShippingAddress

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class EntityRecord implements Serializable {
        private String id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String title;
        private String streetAddress;
        private String state;
        private List<String> landmarks;
    }

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
