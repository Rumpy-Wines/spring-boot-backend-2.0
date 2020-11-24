package com.example.rumpy.service;

import com.example.rumpy.model.CartItem;
import com.example.rumpy.model.ProductItem;
import com.example.rumpy.model.User;
import com.example.rumpy.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartItemRepository cartItemRepository;

    public List<CartItem> findByUser(User user) {
        return cartItemRepository.findByUser(user);
    }//end method findAll

    public CartItem addProductItemToCart(ProductItem productItem, User user) {
        return addProductItemToCart(productItem, user, 1);
    }//end method addProductItemToCart

    public CartItem addProductItemToCart(ProductItem productItem, User user, Integer itemCount) {
        Optional<CartItem> optionalCartItem = cartItemRepository.findByProductItem(productItem);
        CartItem cartItem = null;

        if(optionalCartItem.isPresent()){
            cartItem = optionalCartItem.get();
            cartItem.setItemCount(cartItem.getItemCount() + itemCount);
        }else{
            cartItem = new CartItem();
            cartItem.setIsActive(true);
            cartItem.setItemCount(itemCount);
            cartItem.setUser(user);
            cartItem.setProductItem(productItem);
        }

        if(cartItem.getItemCount() > productItem.getNumberAvailable())
            cartItem.setItemCount(productItem.getNumberAvailable());

        return cartItemRepository.save(cartItem);
    }//end method addProductItemToCart
}//end class CartService
