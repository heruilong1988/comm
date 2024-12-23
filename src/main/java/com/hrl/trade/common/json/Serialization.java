package com.hrl.trade.common.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Serialization {

    public static Serialization serializationInstance= new Serialization();

    private ObjectMapper objectMapper = new ObjectMapper();

    private Serialization() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
    }

    public ObjectMapper getObjectMapper(){
        return this.objectMapper;
    }

}
