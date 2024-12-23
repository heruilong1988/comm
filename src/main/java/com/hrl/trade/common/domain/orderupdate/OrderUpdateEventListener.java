package com.hrl.trade.common.domain.orderupdate;

@FunctionalInterface
public interface OrderUpdateEventListener {

    void listen(OrderUpdateEvent orderUpdateEvent);
}
