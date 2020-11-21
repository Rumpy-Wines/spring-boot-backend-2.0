package com.example.rumpy.model;

import com.example.rumpy.entity_interface.HasEntityRecord;
import com.example.rumpy.util.MyStringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CustomerOrderItem extends PlatformItemAbstractClass implements HasEntityRecord<CustomerOrderItem.EntityRecord> {

    record EntityRecord(
            String id,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String origin,
            String imageUrl,
            String name,
            String year,
            String address,
            Integer alcoholContent,
            Long pricePerItem,
            Integer numberAvailable,
            List<String> tags,
            WineCategory category
    ){}

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
