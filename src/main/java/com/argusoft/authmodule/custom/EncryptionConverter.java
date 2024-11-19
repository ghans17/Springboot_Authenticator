package com.argusoft.authmodule.custom;


import com.argusoft.authmodule.utils.EncryptionUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EncryptionConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            return EncryptionUtil.encrypt(attribute);  // Encrypt before storing in DB
        } catch (Exception e) {
            throw new RuntimeException("Could not encrypt data", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            return EncryptionUtil.decrypt(dbData);  // Decrypt when reading from DB
        } catch (Exception e) {
            throw new RuntimeException("Could not decrypt data", e);
        }
    }
}