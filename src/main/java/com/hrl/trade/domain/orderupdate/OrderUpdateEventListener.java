package com.hrl.trade.domain.orderupdate;

@FunctionalInterface
public interface OrderUpdateEventListener {

    void listen(OrderUpdateEvent orderUpdateEvent);
}
