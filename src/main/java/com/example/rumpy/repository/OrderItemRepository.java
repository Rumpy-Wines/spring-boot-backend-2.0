package com.example.rumpy.repository;

import com.example.rumpy.model.CustomerOrderItem;
import com.example.rumpy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<CustomerOrderItem, String> {
    Optional<CustomerOrderItem> findByIdAndCustomerOrder_User(String id, User user);
}
