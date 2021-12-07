package com.example.rumpy.repository;

import com.example.rumpy.model.Address;
import com.example.rumpy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, String> {
    @Query("SELECT a FROM Address a WHERE a.user = :user ORDER BY a.isDefault DESC, a.createdAt DESC")
    List<Address> findAllByUser(@Param("user") User user);
    Integer countAllByUser(User user);
    List<Address> findAllByIsDefault(Boolean isDefault);

    Optional<Address> findByIdAndUser(String id, User user);

    Optional<Address> findFirstByUser(User user);

    List<Address> findByUser(User user);
}//end interface AddressRepository
