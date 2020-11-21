package com.example.rumpy.security.controller;


import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import com.example.rumpy.model.Gender;
import com.example.rumpy.model.User;
import com.example.rumpy.security.util.JwtUtil;
import com.example.rumpy.service.UserService;
import com.example.rumpy.util.HttpErrors;
import com.example.rumpy.util.ValidateRequestParamUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/api/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(
            @RequestParam(name = "email") Optional<String> optionalEmail,
            @RequestParam(name = "password") Optional<String> optionalPassword
    ) {
        HttpErrors errors = new HttpErrors();
        if (optionalEmail.isEmpty())
            errors.put("email", "The email field is required");
        if (optionalPassword.isEmpty())
            errors.put("password", "the password field is required");

        if (errors.size() > 0)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errors);

        String email = optionalEmail.get();
        String password = optionalPassword.get();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (BadCredentialsException exception) {
            Map<String, String> response = new HashMap<>();

            response.put("message", "Invalid Username or Password");
            return ResponseEntity.badRequest().body(response);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new HashMap<>(Map.of("jwt", jwt)));
    }//end method createAuthenticationToken

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestParam("password") Optional<String> optionalPassword,
            @RequestParam("passwordConfirmation") Optional<String> optionalPasswordConfirmation,
            @RequestParam("email") Optional<String> optionalEmail
    ) {
        HttpErrors errors = new HttpErrors();

        //Check for required fields
        if (optionalPassword.isEmpty())
            errors.put("password", "The password fields is required");

        if (optionalPasswordConfirmation.isEmpty())
            errors.put("password", "The passwordConfirmation fields is required");

        if (optionalEmail.isEmpty())
            errors.put("email", "The email fields is required");


        //If there are errors return the errors
        if (errors.size() > 0) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errors);
        }

        String password = optionalPassword.get();
        String passwordConfirmation = optionalPasswordConfirmation.get();
        String email = optionalEmail.get();

        if (!password.equals(passwordConfirmation))
            errors.put("password", "The password fields do not match");

        if (userService.existsByEmail(email))
            errors.put("email", String.format("The email '%s' already exists", email));

        if (errors.size() > 0) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errors);
        }

        User user = new User();
        user.setEmail(email);

        user = userService.createUser(user, password);

        return ResponseEntity.status(HttpStatus.OK).body(user.getEntityRecord());
    }//end method registerUser

    @PostMapping("/setup")
    public ResponseEntity<?> setupUserData(
            @RequestParam Map<String, String> requestMap
    ) {
        HttpErrors errors = new HttpErrors();

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Class<?>> requiredValues = Map.of(
                "phoneNumber", String.class,
                "firstName", String.class,
                "lastName", String.class,
                "otherNames", String.class,
                "gender", Gender.class,
                "dateOfBirth", LocalDate.class
        );

        ValidateRequestParamUtil validateRequestParamUtil = ValidateRequestParamUtil.forRequired(requiredValues);

        validateRequestParamUtil.setReferenceMap(requestMap);
        HttpErrors validationErrors = validateRequestParamUtil.validate();

        if (validationErrors.size() > 0)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(validationErrors);

        Optional<User> optionalUser = userService.getAuthenticatedUser();
        if(optionalUser.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new HashMap(Map.of("message", "Unauthorized")));

        String phoneNumber = requestMap.get("phoneNumber");
        String firstName = requestMap.get("firstName");
        String lastName = requestMap.get("lastName");
        String otherNames = requestMap.get("otherNames");
        Gender gender = null;
        try {
            gender = objectMapper.readValue(objectMapper.writeValueAsString(requestMap.get("gender")), Gender.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        LocalDate dateOfBirth = LocalDate.parse(requestMap.get("dateOfBirth"));



        User user = optionalUser.get();
        user.setPhoneNumber(phoneNumber);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setOtherNames(otherNames);
        user.setGender(gender);
        user.setDateOfBirth(dateOfBirth);

        return ResponseEntity.status(HttpStatus.OK)
                .body(user.getEntityRecord());
    }//end method setupUserData
}//end class AuthenticationController
