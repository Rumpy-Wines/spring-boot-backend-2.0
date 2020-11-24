package com.example.rumpy.model;

import com.example.rumpy.entity_interface.HasEntityRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="cart_items")
public class CartItem extends RootModel implements HasEntityRecord<CartItem.EntityRecord> {
    @Column(nullable = false)
    private Integer itemCount = 1;

    @Column(nullable = false)
    private Boolean isActive = true;



    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class EntityRecord implements Serializable {
        private String id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Integer itemCount;
        private Boolean isActive;
        private User.EntityRecord user;
        private ProductItem.EntityRecord productItem;
    }//end class EntityRecord

    @Override
    public EntityRecord getEntityRecord() {
        return new EntityRecord(
                this.getId(),
                this.getCreatedAt(),
                this.getUpdatedAt(),
                this.getItemCount(),
                this.getIsActive(),
                this.getUser().getEntityRecord(),
                this.getProductItem().getEntityRecord()
        );
    }//end method getEntityRecord

    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////         RELATIONSHIPS
    ////////////////////////////////////////////////////////////////////////////////////

    @ManyToOne
    @JoinColumn(nullable = false, name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false, name="product_item_id")
    private ProductItem productItem;
}//end class CartItem
