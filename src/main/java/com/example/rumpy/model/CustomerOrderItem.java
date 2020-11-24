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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CustomerOrderItem extends PlatformItemAbstractClass implements HasEntityRecord<CustomerOrderItem.EntityRecord> {

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class EntityRecord implements Serializable {
        private String id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String origin;
        private String imageUrl;
        private String name;
        private String year;
        private String address;
        private Double alcoholContent;
        private Long pricePerItem;
        private Integer numberAvailable;
        private List<String> tags;
        private WineCategory category;
    }

    @Override
    public EntityRecord getEntityRecord() {
        List<String> tags = Arrays.asList(this.getTags().strip().split(MyStringUtil.STRING_LIST_SEPARATOR));
        return new EntityRecord(
                this.getId(),
                this.getCreatedAt(),
                this.getUpdatedAt(),
                this.getOrigin(),
                this.getImageUrl(),
                this.getName(),
                this.getYear(),
                this.getAddress(),
                this.getAlcoholContent(),
                this.getPricePerItem(),
                this.getNumberAvailable(),
                tags,
                this.getCategory()
        );
    }//end method getEntityRecord

    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////         RELATIONSHIPS
    ////////////////////////////////////////////////////////////////////////////////////
    @OneToOne
    @JoinColumn(nullable = false)
    private ProductItem productItem;

    @ManyToOne
    @JoinColumn(nullable = false)
    private CustomerOrder customerOrder;

    @OneToMany(mappedBy = "customerOrderItem")
    private List<ShippingTrackAddress> shippingAddress = new ArrayList<>();
}//end class CustomerOrderItem
