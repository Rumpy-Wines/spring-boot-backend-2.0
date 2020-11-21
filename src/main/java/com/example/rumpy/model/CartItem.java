package com.example.rumpy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="cart_items")
public class CartItem extends RootModel {
    @Column(nullable = false)
    private Integer itemCount = 1;

    @Column(nullable = false)
    private Boolean isActive = true;

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
