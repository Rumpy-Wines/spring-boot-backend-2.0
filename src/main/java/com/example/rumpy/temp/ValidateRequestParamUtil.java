package com.example.rumpy.temp;

import com.example.rumpy.util.HttpErrors;
import com.example.rumpy.util.ValidateRequestEntry;
import com.example.rumpy.util.ValidateRequestType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ValidateRequestParamUtil {
    private HttpErrors errors = new HttpErrors();
    private List<ValidateRequestEntry> entries = new ArrayList<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, ? extends Object> referenceMap;

    public ValidateRequestParamUtil (Map<String, Class<?>> requiredMap) {
        this.entries = attachStringListToType(requiredMap, List.of(ValidateRequestType.REQUIRED));
    }

    private List<ValidateRequestEntry> attachStringListToType
            (Map<String, Class<?>> requirementMap, List<ValidateRequestType> types) {
        return requirementMap.entrySet()
                .stream()
                .map(entry -> {
                    return new ValidateRequestEntry(entry.getKey(), entry.getValue(), types);
                })
                .collect(Collectors.toList());
    }//end method attachStringListToType


    public void setReferenceMap(Map<String, ? extends Object> referenceMap) {
        this.referenceMap = referenceMap;
    }

    public HttpErrors validate() throws IllegalArgumentException {
        if (this.referenceMap == null)
            throw new IllegalArgumentException("The referenceMap must be specified");

        this.entries
                .stream()
                .forEach(validateRequestEntry -> {

                    validateRequestEntry
                            .getTypes()
                            .stream()
                            .forEach(validateRequestType -> {
                                if (validateRequestType == ValidateRequestType.REQUIRED) {
                                    if (!this.referenceMap.containsKey(validateRequestEntry.getName())) {
                                        this.errors.put(
                                                validateRequestEntry.getName(),
                                                String.format("The '%s' field is required", validateRequestEntry.getName())
                                        );
                                    }
                                }


                            });

                    //Check if type matches
                    Object referenceValue = this.referenceMap.get(validateRequestEntry.getName());
                    if (referenceValue != null) {
                        //Date Time
                        try {
                            if (Temporal.class.isAssignableFrom(validateRequestEntry.getObjectClass())) {
                                if (LocalDateTime.class.isAssignableFrom(validateRequestEntry.getObjectClass()))
                                    LocalDateTime.parse((String) referenceValue);
                                if (LocalDate.class.isAssignableFrom(validateRequestEntry.getObjectClass()))
                                    LocalDate.parse((String) referenceValue);
                                return;
                            }

                            objectMapper.readValue(objectMapper.writeValueAsString(referenceValue), validateRequestEntry.getObjectClass());
                        } catch (JsonProcessingException | DateTimeParseException e) {
                            this.errors.put(
                                    validateRequestEntry.getName(),
                                    String.format("%s must be of type %s", validateRequestEntry.getName(), validateRequestEntry.getObjectClass().getSimpleName())
                            );
                        }
                    }
                });

        return this.errors;
    }
}//end method ValidateRequestParam

