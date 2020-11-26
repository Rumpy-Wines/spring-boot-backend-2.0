package com.example.rumpy.controller;

import com.example.rumpy.exceptions.PaymentServiceException;
import com.example.rumpy.model.*;
import com.example.rumpy.service.*;
import com.example.rumpy.util.HttpErrors;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ProductItemService productItemService;

    @Autowired
    private CartService cartService;

    @Autowired
    private PaymentServiceInterface paymentService;

    @Autowired
    private OrderItemService orderItemService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        User user = userService.getAuthenticatedUser().get();

        List<CustomerOrder> orders = orderService.findAllByUserAndTransactionStatus(user, TransactionStatus.SUCCESS);

        List<CustomerOrder.EntityRecord> customerOrderEntities = orders.stream()
                .map(CustomerOrder::getEntityRecord)
                .collect(Collectors.toList());

        return ResponseEntity.ok(customerOrderEntities);
    }//end method findAll

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") String id) {
        User user = userService.getAuthenticatedUser().get();

        Optional<CustomerOrder> order = orderService.findByUserAndIdAndTransactionStatus(user, id, TransactionStatus.SUCCESS);

        if (order.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HashMap<>(Map.of("message", "Order not found")));

        return ResponseEntity.ok(order.get().getEntityRecord());
    }//end method findById

    @PostMapping
    public ResponseEntity<?> placeOrder(
            @RequestParam("callbackUrl") Optional<String> callbackUrl,
            @RequestParam("addressId") Optional<String> addressId
    ) {
        HttpErrors errors = new HttpErrors();

        if (callbackUrl.isEmpty())
            errors.put("callbackUrl", "The callbackUrl is required");
        if (addressId.isEmpty())
            errors.put("addressId", "The addressId field is required");

        if (errors.size() > 0)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errors);

        User user = userService.getAuthenticatedUser().get();

        Optional<Address> address = addressService.findByUser(user);
        if (address.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HashMap<>(Map.of("message", "Address not found")));

        List<CartItem> cartItems = cartService.findByUser(user);
        if (cartItems.size() == 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new HashMap<>(Map.of("message", "Cart Is Empty")));

        ////////////////////
        // Migrate Data for order
        /////////////////////
        String paymentUrl = null;
        try {
            paymentUrl = orderService.placeOrder(user, cartItems, address.get(), callbackUrl);
        } catch (PaymentServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new HashMap<>(Map.of(
                                    "message",
                                    e.getMessage() == null ? "An error occurred while making payment calls" : e.getMessage()
                            ))
                    );
        }

        return ResponseEntity.ok(new HashMap<>(Map.of("payment_url", paymentUrl)));
    }//end method placeOrder

    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestBody JSONObject requestJSON, HttpServletRequest request) {
        paymentService.webhook(requestJSON, request);
        return ResponseEntity.ok("ok");
    }//end method webhook

    @GetMapping("/item/{id}")
    public ResponseEntity<?> getSingleOrderItem(@PathVariable("id") String id) {
        User user = userService.getAuthenticatedUser().get();

        Optional<CustomerOrderItem> orderItem = orderItemService.findByIdAndCustomerOrder_User(id, user);

        if(orderItem.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new HashMap<>(Map.of("message", "Customer order item not found")));

        return ResponseEntity.ok(orderItem.get().getEntityRecord());
    }//end method
}//end class OrderController
