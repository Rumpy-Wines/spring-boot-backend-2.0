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
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ProductItem extends PlatformItem implements HasEntityRecord<ProductItem.EntityRecord> {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String manufacturerDescription;



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
            WineCategory category,
            String manufacturerDescription
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
                this.getCategory(),
                this.getManufacturerDescription()
        );
    }//end method getEntityRecord

    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////         RELATIONSHIPS
    ////////////////////////////////////////////////////////////////////////////////////
    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @OneToMany(mappedBy = "productItem")
    private List<ProductReview> productReviews = new ArrayList<>();
}//end class ProductItem
