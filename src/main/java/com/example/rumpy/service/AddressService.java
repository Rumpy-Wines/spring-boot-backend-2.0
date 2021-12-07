package com.example.rumpy.service;

import com.example.rumpy.model.Address;
import com.example.rumpy.model.User;
import com.example.rumpy.repository.AddressRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@NoArgsConstructor
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;

    public List<Address> findAllByUser(User user) {
        return addressRepository.findAllByUser(user);
    }//end method findAllByUser

//    public Optional<Address> findByUser(User user) {
//        return addressRepository.findByUser(user);
//    }//end method findByUser

    public Optional<Address> findById(String id, User user){
        return addressRepository.findByIdAndUser(id, user);
    }//end method findById

    public Address createAddress(Address address, User user) {
        address.setUser(user);
        if(!address.getIsDefault() && addressRepository.countAllByUser(user) == 0)
            address.setIsDefault(true);
        else if(address.getIsDefault()) ensureOtherAddressesAreNotDefault(user);

        return addressRepository.save(address);
    }//end method createAddress

    public Address updateAddress(Address address, User user) {
        if(!address.getUser().getId().equals(user.getId())) return address;

        if(address.getIsDefault()) ensureOtherAddressesAreNotDefault(user);

        return addressRepository.save(address);
    }//end method updateAddress

    public void deleteAddress(Address address, User user){
        if(!address.getUser().getId().equals(user.getId())) return;

        Boolean isDefault = address.getIsDefault();

        addressRepository.delete(address);

        if(isDefault)
            makeFirstAddressDefault(user);
    }//end method deleteAddress

    private void makeFirstAddressDefault(User user){
        Optional<Address> optionalAddress = addressRepository.findFirstByUser(user);
        if(optionalAddress.isEmpty()) return;

        Address address = optionalAddress.get();
        address.setIsDefault(true);
        addressRepository.save(address);
    }//end method makeFirstAddressDefault

    private void ensureOtherAddressesAreNotDefault(User user){
        List<Address> addresses = findAllByUser(user);
        addresses.stream()
                .map(address -> {
                    address.setIsDefault(false);
                    return address;
                }).collect(Collectors.toList());

        addressRepository.saveAll(addresses);
    }//end method ensureOtherAddressesAreNotDefault
}//end class AddressService
