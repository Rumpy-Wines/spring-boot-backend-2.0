package com.example.rumpy.controller;

import com.example.rumpy.model.CartItem;
import com.example.rumpy.model.ProductItem;
import com.example.rumpy.model.User;
import com.example.rumpy.service.CartService;
import com.example.rumpy.service.ProductItemService;
import com.example.rumpy.service.UserService;
import com.example.rumpy.util.HttpErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.print.Pageable;
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
    }//end method findAll
}//end class CartController
