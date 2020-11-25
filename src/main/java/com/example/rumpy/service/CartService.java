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

    public Optional<CartItem> findById(String id){
        return cartItemRepository.findById(id);
    }//end method findById

    public Optional<CartItem> findByIdAndUser(String id, User user) {
        return cartItemRepository.findByIdAndUser(id, user);
    }//end method findByIdAndUser

    public Integer countByUser(User user){
        return cartItemRepository.countByUser(user);
    }//end method countByUser

    public CartItem addProductItemToCart(ProductItem productItem, User user) {
        return addProductItemToCart(productItem, user, 1);
    }//end method addProductItemToCart

    public CartItem addProductItemToCart(ProductItem productItem, User user, Integer itemCount) {
        Optional<CartItem> optionalCartItem = cartItemRepository.findByProductItemAndUser(productItem, user);
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

    public CartItem updateIsActive(CartItem cartItem, Boolean isActive, User user){
        if(!cartItem.getUser().getId().equals(user.getId())) return cartItem;

        cartItem.setIsActive(isActive);
        return cartItemRepository.save(cartItem);
    }//end method updateIsActive

    public CartItem updateCart(CartItem cartItem, CartItem cartItemWithUpdates, User user) {
        cartItem.setItemCount(cartItemWithUpdates.getItemCount());
        cartItem.setIsActive(cartItemWithUpdates.getIsActive());

        if(cartItem.getItemCount() > cartItem.getProductItem().getNumberAvailable())
            cartItem.setItemCount(cartItem.getProductItem().getNumberAvailable());

        return cartItemRepository.save(cartItem);
    }//end method updateCart

    public void deleteCartItem(CartItem cartItem, User user) {
        if(!cartItem.getUser().getId().equals(user.getId())) return;

        cartItemRepository.delete(cartItem);
    }//end method deleteCartItem
}//end class CartService
