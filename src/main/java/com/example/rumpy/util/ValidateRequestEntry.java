package com.example.rumpy.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateRequestEntry {
    private String name;
    private Class<?> objectClass;
    private List<ValidateRequestType> types;
}