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
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ProductItem extends PlatformItemAbstractClass implements HasEntityRecord<ProductItem.EntityRecord> {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String manufacturerDescription;

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
        private Integer alcoholContent;
        private Long pricePerItem;
        private Integer numberAvailable;
        private List<String> tags;
        private WineCategory category;
        private String manufacturerDescription;
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

    @ManyToMany(mappedBy = "cartItems")
    private List<User> users = new ArrayList<>();
}//end class ProductItem
