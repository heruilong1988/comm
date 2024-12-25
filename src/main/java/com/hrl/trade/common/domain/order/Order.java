package com.hrl.trade.common.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Getter
@Setter
@Builder(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
public class Order
{
	private Long id;
	private Long orderId;
	private String clientOrderId;
	private String symbol;
	private BigDecimal cumQuote;
	private BigDecimal executedQty;
	private BigDecimal avgPrice;
	private BigDecimal origQty;
	private BigDecimal price;
	private Boolean reduceOnly;
	private String side;
	private String positionSide;
	private String status;
	private BigDecimal stopPrice;
	private String closePosition;
	private String timeInForce;
	private String type;
	private String origType;
	private BigDecimal activationPrice;
	private BigDecimal priceRate;
	private Long updateTime;
	private String workingType;
	private Boolean priceProtect;


	private Date  createTime;
	//返回值没有的
	private BigDecimal lastExecutedQty;
	private String platform;
	private BigDecimal symbolThirdAvgPriceWhenCreatedOrder;
	private String symbolThird;
	private String symbolThirdPlatform;

	private Double gridRatio;


	private String symbolOpposite;
	private String symbolOppositePlatform;
	private Boolean isParent;
	private Long parentOrderId;



	public boolean statusCanFilled(){
		return OrderStatus.NEW.toString().equalsIgnoreCase(status)
				|| OrderStatus.PARTIALLY_FILLED.toString().equalsIgnoreCase(status);
	}

	public boolean isSellOrder(){
		return "sell".equalsIgnoreCase(side);
	}

	public String getOppositeOrderSide(){

		if(OrderSide.SELL.toString().equalsIgnoreCase(side)){
			return OrderSide.BUY.toString();
		}

		return OrderSide.SELL.toString();
	}

	public boolean isStatusFilled(){
		return OrderStatus.FILLED.toString().equalsIgnoreCase(status);
	}

}
