package com.example.rumpy.service;


import com.example.rumpy.model.User;
import com.example.rumpy.repository.UserRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@NoArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        return user;
    }//end method create User

    public User updateUser(User user) {
        userRepository.save(user);
        return user;
    }//end method updateUser

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public Optional<User> getAuthenticatedUser(){
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return findByEmail(email);
    }//end method getAuthenticatedUser
}//end class UserService
