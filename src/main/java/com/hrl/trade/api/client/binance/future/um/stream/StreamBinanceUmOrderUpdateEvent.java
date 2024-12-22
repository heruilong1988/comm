package com.hrl.trade.api.client.binance.future.um.stream;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.hrl.trade.json.Serialization.serializationInstance;

@Getter
@Setter
@ToString
public class StreamBinanceUmOrderUpdateEvent {

    @JsonAlias("e")
    public String event;

    @JsonAlias("E")
    public Long eventType;

    @JsonAlias("T")
    public Long transactionTime;

    @JsonAlias("o")
    public StreamBinanceUmOrderUpdateEventPayload payload;

    public static StreamBinanceUmOrderUpdateEvent fromJson(String orderUpdateEventPayloadStr) {
        try {
            return serializationInstance.getObjectMapper().readValue(orderUpdateEventPayloadStr, StreamBinanceUmOrderUpdateEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
