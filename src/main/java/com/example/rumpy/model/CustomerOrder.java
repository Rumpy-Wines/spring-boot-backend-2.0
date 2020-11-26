package com.example.rumpy.model;

import com.example.rumpy.entity_interface.HasEntityRecord;
import com.example.rumpy.util.MyStringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CustomerOrder extends RootModel implements HasEntityRecord<CustomerOrder.EntityRecord>, TransactionPaymentInterface {
    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private Long price = 0L;

    @Column(nullable = false)
    private TransactionStatus transactionStatus = TransactionStatus.PENDING;

    @Column(nullable = false)
    private CustomerOrderStatus status = CustomerOrderStatus.EN_ROUTE;

    @PrePersist
    public void prePersist() {
        if (this.getOrderId() == null || "".equals(this.getOrderId()))
            this.setOrderId(String.format("%s-order-%s", this.getUser().getEmail().replaceAll("[^a-z^A-z]", ""), MyStringUtil.generateRandom()));
    }//end method prePersist

    @Override
    public String getReference() {
        return getOrderId();
    }

    @Override
    public Long getAmount() {
        return getPrice();
    }

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
        private CustomerOrderStatus status ;
        private User.EntityRecord user;
        private ShippingAddress.EntityRecord shippingAddress;
        private List<CustomerOrderItem.EntityRecord> customerOrderItems;
    }

    @Override
    public EntityRecord getEntityRecord() {
        List<CustomerOrderItem.EntityRecord> customerOrderItems = this.getCustomerOrderItems().stream()
                .map(CustomerOrderItem::getEntityRecord)
                .collect(Collectors.toList());
        return new EntityRecord(
                this.getId(),
                this.getCreatedAt(),
                this.getUpdatedAt(),
                this.getOrderId(),
                this.getPrice(),
                this.getStatus(),
                this.getUser().getEntityRecord(),
                this.getShippingAddress().getEntityRecord(),
                customerOrderItems
        );
    }//end method getEntityRecord

    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////         RELATIONSHIPS
    ////////////////////////////////////////////////////////////////////////////////////
    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @OneToMany(mappedBy = "customerOrder", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private List<CustomerOrderItem> customerOrderItems = new ArrayList<>();

    @OneToOne(mappedBy = "customerOrder", cascade = CascadeType.PERSIST)
    private ShippingAddress shippingAddress;
}//end class CustomerOrder
