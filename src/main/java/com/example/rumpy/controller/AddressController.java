package com.example.rumpy.controller;

import com.example.rumpy.model.Address;
import com.example.rumpy.model.User;
import com.example.rumpy.service.AddressService;
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
@RequestMapping("/api/address")
public class AddressController {
    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        User user = userService.getAuthenticatedUser().get();

        List<Address> addresses = addressService.findAllByUser(user);

        List<Address.EntityRecord> addressEntities = addresses.stream()
                .map(address -> {
                    Address.EntityRecord entityRecord = address.getEntityRecord();
                    entityRecord.setUser(address.getUser().getEntityRecord());

                    return entityRecord;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(addressEntities);
    }//end method findAll

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") String id) {
        User user = userService.getAuthenticatedUser().get();

        Optional<Address> optionalAddress = addressService.findById(id, user);

        if (optionalAddress.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HashMap<>(Map.of("message", "The id does not match any user address")));

        Address.EntityRecord address = optionalAddress.get().getEntityRecord();

        address.setUser(user.getEntityRecord());

        return ResponseEntity.ok(address);
    }//end method findById

    @PostMapping
    public ResponseEntity<?> createAddress(
            @RequestParam Map<String, String> requestMap,
            @RequestParam Optional<List<String>> landmarks
    ) {
        Map<String, Class<?>> requiredValues = Map.of(
                "title", String.class,
                "streetAddress", String.class,
                "state", String.class,
                "isDefault", Boolean.class,
                "city", String.class
        );

        ValidateRequestParamUtil validateRequestParamUtil = new ValidateRequestParamUtil(requiredValues);

        validateRequestParamUtil.setReferenceMap(requestMap);
        HttpErrors validationErrors = validateRequestParamUtil.validate();

        if (landmarks.isEmpty())
            validationErrors.put("landmarks", "The field 'landmarks' is required");

        if (validationErrors.size() > 0)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(validationErrors);


        User user = userService.getAuthenticatedUser().get();

        Address address = new Address();
        address.setIsDefault(Boolean.parseBoolean(requestMap.get("isDefault")));
        address.setTitle(requestMap.get("title"));
        address.setStreetAddress(requestMap.get("streetAddress"));
        address.setState(requestMap.get("state"));
        address.setLandmarks(landmarks.get());
        address.setCity(requestMap.get("city"));

        address = addressService.createAddress(address, user);

        Address.EntityRecord addressEntityRecord = address.getEntityRecord();
        addressEntityRecord.setUser(user.getEntityRecord());

        return ResponseEntity.ok(addressEntityRecord);
    }//end method createAddress

    @PutMapping
    public ResponseEntity<?> updateAddress(
            @RequestParam Map<String, String> requestMap,
            @RequestParam Optional<List<String>> landmarks
    ){
        Map<String, Class<?>> requiredValues = Map.of(
                "id", String.class,
                "title", String.class,
                "streetAddress", String.class,
                "state", String.class,
                "isDefault", Boolean.class,
                "city", String.class
        );


        ValidateRequestParamUtil validateRequestParamUtil = new ValidateRequestParamUtil(requiredValues);

        validateRequestParamUtil.setReferenceMap(requestMap);
        HttpErrors validationErrors = validateRequestParamUtil.validate();

        if (landmarks.isEmpty())
            validationErrors.put("landmarks", "The field 'landmarks' is required");

        if (validationErrors.size() > 0)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(validationErrors);

        User user = userService.getAuthenticatedUser().get();

        Optional<Address> optionalAddress = addressService.findById(requestMap.get("id"), user);

        if(optionalAddress.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new HashMap<>(Map.of("message", "Not fount: Id does not match any user address")));

        Address address = optionalAddress.get();
        address.setIsDefault(Boolean.parseBoolean(requestMap.get("isDefault")));
        address.setTitle(requestMap.get("title"));
        address.setStreetAddress(requestMap.get("streetAddress"));
        address.setState(requestMap.get("state"));
        address.setLandmarks(landmarks.get());
        address.setCity(requestMap.get("city"));

        addressService.updateAddress(address, user);
        Address.EntityRecord addressEntityRecord = address.getEntityRecord();
        addressEntityRecord.setUser(user.getEntityRecord());

        return ResponseEntity.ok(addressEntityRecord);
    }//end method updateAddress

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable("id") String id) {
        User user = userService.getAuthenticatedUser().get();

        Optional<Address> optionalAddress = addressService.findById(id, user);

        if (optionalAddress.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HashMap<>(Map.of("message", "The id does not match any user address")));

        addressService.deleteAddress(optionalAddress.get(), user);

        return ResponseEntity.ok(new HashMap<>(Map.of("message", "Deleted")));
    }//end method deleteAddress
}//end class AddressController
