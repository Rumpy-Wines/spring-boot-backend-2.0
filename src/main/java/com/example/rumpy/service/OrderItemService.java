package com.example.rumpy.service;

import com.example.rumpy.model.CustomerOrderItem;
import com.example.rumpy.model.User;
import com.example.rumpy.repository.OrderItemRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@NoArgsConstructor
public class OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;

    public Optional<CustomerOrderItem> findByIdAndCustomerOrder_User(String id, User user) {
        return orderItemRepository.findByIdAndCustomerOrder_User(id, user);
    }
}
