package com.example.rumpy.util;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;

@NoArgsConstructor
public class HttpErrors implements Serializable {
    private Map<String, List<String>> errors = new HashMap<>();

    public void put(String key, String value){
        Optional<List<String>> optionalValues = Optional.ofNullable(errors.get(key));

        List<String> values = new ArrayList<>(optionalValues.orElseGet(ArrayList<String>::new));

        values.add(value);

        errors.put(key, values);
    }//end method put

    public void putAll(String key, List<String> values){
        values.stream()
                .forEach(value -> {
                    this.put(key, value);
                });
    }//end method putAll


    public Map<String, List<String>> getErrors(){
        return errors;
    }//end method getErrors

    public Integer size(){
        return errors.size();
    }
}//end class HttpErrors
