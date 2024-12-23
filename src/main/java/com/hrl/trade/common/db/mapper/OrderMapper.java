package com.hrl.trade.common.db.mapper;

import com.hrl.trade.common.domain.order.Order;
import com.hrl.trade.common.domain.orderupdate.OrderUpdateEvent;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderMapper {

    @Select("select * from hrl_transaction.order where clientOrderId = #{clientOrderId}")
    Order findByClientOrderId(@Param("clientOrderId") String clientOrderId);


    @Insert({ "insert into hrl_transaction.order(" +
            "         orderId,    clientOrderId,    symbol,    cumQuote,    executedQty,    avgPrice,    origQty,    price,    reduceOnly,    side,    positionSide,    status,    stopPrice,    closePosition,    timeInForce,    type,    origType,    activationPrice,    priceRate,    updateTime,   createTime,   workingType,     priceProtect,    lastExecutedQty,    platform,   symbolThirdAvgPriceWhenCreatedOrder,    symbolThird,    symbolThirdPlatform,     symbolOpposite,   symbolOppositeplatform,    isParent,       parentOrderId,   gridRatio) " +
            "values(#{orderId}, #{clientOrderId}, #{symbol}, #{cumQuote}, #{executedQty}, #{avgPrice}, #{origQty}, #{price}, #{reduceOnly}, #{side}, #{positionSide}, #{status}, #{stopPrice}, #{closePosition}, #{timeInForce}, #{type}, #{origType}, #{activationPrice}, #{priceRate}, #{updateTime}, #{createTime},#{workingType}, #{priceProtect}, #{lastExecutedQty}, #{platform}, #{symbolThirdAvgPriceWhenCreatedOrder}, #{symbolThird}, #{symbolThirdPlatform}, #{symbolOpposite}, #{symbolOppositeplatform} , #{isParent},  #{parentOrderId}, #{gridRatio})" })
    int insertNewOrder(Order order);


    @Update({"update hrl_transaction.order set executedQty=#{orderFilledAccumulatedQuantity},lastExecutedQty=#{orderLastFilledQuantity},status=#{orderStatus} where orderId = #{orderId}"})

    int updateOrderByOrderUpdateEvent(@Param("orderUpdateEvent") OrderUpdateEvent orderUpdateEvent);
}
