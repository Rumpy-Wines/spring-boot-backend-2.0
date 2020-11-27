package com.example.rumpy.service;

import com.example.rumpy.exceptions.PaymentServiceException;
import com.example.rumpy.model.*;
import com.example.rumpy.repository.OrderRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentServiceInterface paymentService;

    public List<CustomerOrder> findAllByUser(User user) {
        return orderRepository.findAllByUser(user);
    }//end method findAllByUser

    public List<CustomerOrder> findAllByUserAndTransactionStatus(User user, TransactionStatus status) {
        return orderRepository.findAllByUserAndTransactionStatus(user, status);
    }//end method findAllByUser

    public Optional<CustomerOrder> findByUserAndIdAndTransactionStatus(User user, String id, TransactionStatus status) {
        return orderRepository.findByUserAndIdAndTransactionStatus(user, id, status);
    }//end method findAllByUser




    public Optional<CustomerOrder> findByOrderId(String orderId){
        return orderRepository.findByOrderId(orderId);
    }//end method findByOrderId

    public String placeOrder(User user, List<CartItem> cartItems, Address address, Optional<String> callbackUrl) throws PaymentServiceException{
        CustomerOrder customerOrder = new CustomerOrder();

        cartItems = cartItems.stream()
                .filter(CartItem::getIsActive)
                .collect(Collectors.toList());

        ShippingAddress shippingAddress = ShippingAddress.fromAddress(address);

        List<CustomerOrderItem> customerOrderItems = cartItems.stream()
                .map(CustomerOrderItem::fromCartItem)
                .map(customerOrderItem -> {
                    customerOrderItem.setCustomerOrder(customerOrder);
                    return customerOrderItem;
                })
                .collect(Collectors.toList());



        Long totalPrice = cartItems.stream()
                .mapToLong(item -> {
                    Long price = item.getProductItem().getPricePerItem() * item.getItemCount();

                    return price;
                }).sum();

        customerOrder.setPrice(totalPrice);


        customerOrder.setUser(user);

        customerOrder.setShippingAddress(shippingAddress);
        shippingAddress.setCustomerOrder(customerOrder);

        customerOrder.setCustomerOrderItems(customerOrderItems);
        customerOrder.prePersist();

        String paymentUrl = paymentService.initializeTransaction(customerOrder, callbackUrl.orElse(""));

        orderRepository.save(customerOrder);
        return paymentUrl;
    }//end method placeOrder

    public void validateTransactionStatus(CustomerOrder customerOrder){
        try {
            TransactionStatus transactionStatus = paymentService.getTransactionStatus(customerOrder);
            customerOrder.setTransactionStatus(transactionStatus);

            orderRepository.save(customerOrder);
        } catch (PaymentServiceException e) {
            e.printStackTrace();
        }
    }// end method validateTransactionStatus
}//end method OrderService
