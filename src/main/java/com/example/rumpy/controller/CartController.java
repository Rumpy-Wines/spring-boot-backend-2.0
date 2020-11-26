package com.example.rumpy.controller;

import com.example.rumpy.model.*;
import com.example.rumpy.service.CartService;
import com.example.rumpy.service.ProductItemService;
import com.example.rumpy.service.UserService;
import com.example.rumpy.temp.ValidateRequestParamUtil;
import com.example.rumpy.util.HttpErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductItemService productItemService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        Optional<User> authenticatedUser = userService.getAuthenticatedUser();

        if (authenticatedUser.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new HashMap<>(Map.of("message", "Unauthorized")));

        List<CartItem> cartItems = cartService.findByUser(authenticatedUser.get());

        List<CartItem.EntityRecord> cartItemEntityRecords = cartItems.stream()
                .map(CartItem::getEntityRecord)
                .collect(Collectors.toList());

        return ResponseEntity.ok(cartItemEntityRecords);
    }//end method findAll

    @GetMapping("/count")
    public ResponseEntity<?> countAll() {
        Optional<User> authenticatedUser = userService.getAuthenticatedUser();

        if (authenticatedUser.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new HashMap<>(Map.of("message", "Unauthorized")));

        return ResponseEntity.ok(cartService.countByUser(authenticatedUser.get()));
    }//end method countAll

    @PostMapping
    public ResponseEntity<?> addProductItemToCart(
            @RequestParam("productItemId") Optional<String> productItemId,
            @RequestParam("itemCount") Optional<Integer> itemCount
    ) {
        Optional<User> authenticatedUser = userService.getAuthenticatedUser();

        if (authenticatedUser.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new HashMap<>(Map.of("message", "Unauthorized")));

        HttpErrors errors = new HttpErrors();

        if (productItemId.isEmpty())
            errors.put("productItemId", "The 'productItemId' field is required");

        if (errors.size() > 0)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errors);

        Optional<ProductItem> productItem = productItemService.findById(productItemId.get());
        if(productItem.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new HashMap<>(Map.of("message", "No product item with matching productItemId")));

        CartItem cartItem = cartService.addProductItemToCart(productItem.get(), authenticatedUser.get(), itemCount.orElse(1));

        return ResponseEntity.ok(cartItem.getEntityRecord());
    }//end method addProductItemToCart

    @PostMapping("/change-active")
    public ResponseEntity<?> changeActiveState(
            @RequestParam("cartItemId") Optional<String> cartItemId,
            @RequestParam("isActive") Optional<Boolean> isActive
    ) {
        Optional<User> authenticatedUser = userService.getAuthenticatedUser();

        if (authenticatedUser.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new HashMap<>(Map.of("message", "Unauthorized")));

        HttpErrors errors = new HttpErrors();

        if(cartItemId.isEmpty())
            errors.put("cartItemId", "The 'cartItemId' field is required");

        if(isActive.isEmpty())
            errors.put("isActive", "The 'isActive' field is required");

        if (errors.size() > 0)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errors);

        Optional<CartItem> optionalCartItem = cartService.findById(cartItemId.get());

        if(optionalCartItem.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new HashMap<>(Map.of("message", "The cart item not found")));

        CartItem cartItem = optionalCartItem.get();

        cartItem = cartService.updateIsActive(cartItem, isActive.get(), authenticatedUser.get());

        return ResponseEntity.ok(cartItem.getEntityRecord());
    }//end method changeActiveState

    @PutMapping
    public ResponseEntity<?> updateCartItem(@RequestParam Map<String, String> requestMap){
        Optional<User> authenticatedUser = userService.getAuthenticatedUser();

        if (authenticatedUser.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new HashMap<>(Map.of("message", "Unauthorized")));

        Map<String, Class<?>> requiredValues = Map.of(
                "id", String.class,
                "isActive", Boolean.class,
                "itemCount", Integer.class
        );

        ValidateRequestParamUtil validateRequestParamUtil = new ValidateRequestParamUtil(requiredValues);

        validateRequestParamUtil.setReferenceMap(requestMap);
        HttpErrors validationErrors = validateRequestParamUtil.validate();

        if (validationErrors.size() > 0)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(validationErrors);

        CartItem cartItem = new CartItem();
        cartItem.setId(requestMap.get("id"));
        cartItem.setIsActive(Boolean.parseBoolean(requestMap.get("isActive")));
        cartItem.setItemCount(Integer.parseInt(requestMap.get("itemCount")));

        Optional<CartItem> optionalCartItem = cartService.findById(cartItem.getId());

        if(optionalCartItem.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new HashMap<>(Map.of("message", "THe cart item is not found")));

        cartItem = cartService.updateCart(optionalCartItem.get(), cartItem, authenticatedUser.get());

        return ResponseEntity.ok(cartItem.getEntityRecord());
    }//end method updateCartItem

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable("id") String id) {
        User user = userService.getAuthenticatedUser().get();

        Optional<CartItem> optionalCartItem = cartService.findByIdAndUser(id, user);

        if (optionalCartItem.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HashMap<>(Map.of("message", "The id does not match any user cart item")));

        cartService.deleteCartItem(optionalCartItem.get(), user);

        return ResponseEntity.ok(new HashMap<>(Map.of("message", "Deleted")));
    }//end method deleteAddress
}//end class CartController
