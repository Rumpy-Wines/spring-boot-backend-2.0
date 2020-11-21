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
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CustomerOrder extends RootModel implements HasEntityRecord<CustomerOrder.EntityRecord> {
    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private Long price = 0L;

    @Column(nullable = false)
    private CustomerOrderStatus status = CustomerOrderStatus.EN_ROUTE;

    @PrePersist
    public void prePersist() {
        if (this.getOrderId() == null || "".equals(this.getOrderId()))
            this.setOrderId(String.format("%s-order-%s", this.getUser().getEmail().replaceAll("\\s+", ""), MyStringUtil.generateRandom()));
    }//end method prePersist

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class EntityRecord implements Serializable {
        private String id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String orderId;
        private Long price;
        private CustomerOrderStatus status;
    }

    @Override
    public EntityRecord getEntityRecord() {
        return new EntityRecord(
                this.getId(),
                this.getCreatedAt(),
                this.getUpdatedAt(),
                this.getOrderId(),
                this.getPrice(),
                this.getStatus()
        );
    }//end method getEntityRecord

    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////         RELATIONSHIPS
    ////////////////////////////////////////////////////////////////////////////////////
    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @OneToMany(mappedBy = "customerOrder")
    private List<CustomerOrderItem> customerOrderItems = new ArrayList<>();

    @OneToOne(mappedBy = "customerOrder")
    private ShippingAddress shippingAddress;
}//end class CustomerOrder
