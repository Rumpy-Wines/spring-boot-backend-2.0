package com.example.rumpy.security.service;

import com.example.rumpy.model.Role;
import com.example.rumpy.model.User;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private String email;
    private String password;
    private List<SimpleGrantedAuthority> authorities = new ArrayList<>();

    public UserDetailsImpl(String email, String password, Role role){
        this.password = password;
        this.email = email;
        this.setAuthorities(role);
    }

    public static UserDetailsImpl convertUserToUserDetails(User user){
        return new UserDetailsImpl(user.getEmail(), user.getPassword(), user.getRole());
    }//end method convertUserToUSerDetails

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    private void setAuthorities(Role role){
        this.authorities = role.getAuthorities()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}//end class UserDetailsImpl