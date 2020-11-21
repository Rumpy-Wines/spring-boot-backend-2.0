package com.example.rumpy.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class ValidateRequestParamUtil {
    public static enum ValidateRequestType {
        REQUIRED
    }

    record ValidateRequestEntry(
            String name,
            Class<?> objectClass,
            List<ValidateRequestType> types
    ) {
    }

    private HttpErrors errors = new HttpErrors();
    private List<ValidateRequestEntry> entries = new ArrayList<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, ? extends Object> referenceMap;

    public ValidateRequestParamUtil(List<ValidateRequestEntry> entries) {
        this.entries = entries;
    }

    public <T extends Class<?>> ValidateRequestParamUtil(Map<String, List<ValidateRequestType>> entries, T objectClass) {
        List<ValidateRequestEntry> validateRequestEntries = entries.entrySet()
                .stream()
                .map(entry -> {
                    return new ValidateRequestEntry(entry.getKey(), objectClass, entry.getValue());
                })
                .collect(Collectors.toList());
        this.entries = validateRequestEntries;
    }

    public static ValidateRequestParamUtil forRequired(Map<String, Class<?>> requiredMap) {
        return new ValidateRequestParamUtil(attachStringListToType(requiredMap, List.of(ValidateRequestType.REQUIRED)));
    }

    public static List<ValidateRequestEntry> attachStringListToType
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
                            .types
                            .stream()
                            .forEach(validateRequestType -> {
                                if (validateRequestType == ValidateRequestType.REQUIRED) {
                                    if (!this.referenceMap.containsKey(validateRequestEntry.name)) {
                                        this.errors.put(
                                                validateRequestEntry.name,
                                                String.format("The '%s' field is required", validateRequestEntry.name)
                                        );
                                    }
                                }


                            });

                    //Check if type matches
                    Object referenceValue = this.referenceMap.get(validateRequestEntry.name);
                    if (referenceValue != null) {
                        //Date Time
                        try {
                            if (Temporal.class.isAssignableFrom(validateRequestEntry.objectClass)) {
                                if (LocalDateTime.class.isAssignableFrom(validateRequestEntry.objectClass))
                                    LocalDateTime.parse((String) referenceValue);
                                if (LocalDate.class.isAssignableFrom(validateRequestEntry.objectClass))
                                    LocalDate.parse((String) referenceValue);
                                return;
                            }

                            objectMapper.readValue(objectMapper.writeValueAsString(referenceValue), validateRequestEntry.objectClass);
                        } catch (JsonProcessingException | DateTimeParseException e) {
                            this.errors.put(
                                    validateRequestEntry.name,
                                    String.format("%s must be of type %s", validateRequestEntry.name, validateRequestEntry.objectClass.getSimpleName())
                            );
                        }
                    }
                });

        return this.errors;
    }
}//end method ValidateRequestParam
