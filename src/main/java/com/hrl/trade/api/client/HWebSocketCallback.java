package com.hrl.trade.api.client;

@FunctionalInterface
public interface HWebSocketCallback {
    /**
     * onReceive will be called when data is received from server.
     *
     * @param data The data send by server.
     */
    void onReceive(String data);
}