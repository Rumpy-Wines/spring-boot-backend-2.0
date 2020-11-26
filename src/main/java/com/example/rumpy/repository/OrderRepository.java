package com.example.rumpy.repository;

import com.example.rumpy.model.CustomerOrder;
import com.example.rumpy.model.TransactionStatus;
import com.example.rumpy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<CustomerOrder, String> {
    List<CustomerOrder> findAllByUser(User user);

    Optional<CustomerOrder> findByOrderId(String orderId);

    List<CustomerOrder> findAllByUserAndTransactionStatus(User user, TransactionStatus success);

    Optional<CustomerOrder> findByUserAndIdAndTransactionStatus(User user, String id, TransactionStatus status);
}//end interface OrderRepository
