package com.example.rumpy.model;

import java.util.Set;

public enum Role {
    USER(Set.of(Authority.ITEM_READ)),
    RETAILER((Set.of(
            Authority.ITEM_CREATE,
            Authority.ITEM_WRITE,
            Authority.ITEM_READ
    )));

    private Set<Authority> authorities;

    Role(Set<Authority> authorities){
        this.authorities = authorities;
    }

    public Set<Authority> getAuthorities(){
        return authorities;
    }
}// end enumeration Role
