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
    private Integer itemCount = 1;

    public static CustomerOrderItem fromCartItem(CartItem cartItem) {
        CustomerOrderItem customerOrderItem = CustomerOrderItem.fromProductItem(cartItem.getProductItem());
        customerOrderItem.setItemCount(cartItem.getItemCount());

        return customerOrderItem;
    }//end method fromCartItem

    public static CustomerOrderItem fromProductItem(ProductItem productItem) {
        CustomerOrderItem customerOrderItem = new CustomerOrderItem();

        customerOrderItem.setOrigin(productItem.getOrigin());
        customerOrderItem.setImageUrl(productItem.getImageUrl());
        customerOrderItem.setName(productItem.getName());
        customerOrderItem.setYear(productItem.getYear());
        customerOrderItem.setAddress(productItem.getAddress());
        customerOrderItem.setAlcoholContent(productItem.getAlcoholContent());
        customerOrderItem.setPricePerItem(productItem.getPricePerItem());
        customerOrderItem.setNumberAvailable(productItem.getNumberAvailable());
        customerOrderItem.setTags(productItem.getEntityRecord().getTags());
        customerOrderItem.setCategory(productItem.getCategory());

        customerOrderItem.setProductItem(productItem);

        return customerOrderItem;
    }//end method fromCartItem
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
        private Integer itemCount;
        private ProductItem.EntityRecord productItem;

        public String getItemId() {
            return "item-id-" + this.getId();
        }
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
                this.getItemCount(),
                this.getProductItem().getEntityRecord()
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
    private List<ShippingTrackAddress> shippingTrackAddresses = new ArrayList<>();
}//end class CustomerOrderItem
