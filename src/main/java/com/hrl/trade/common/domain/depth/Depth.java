package com.hrl.trade.common.domain.depth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hrl.trade.common.api.client.binance.future.um.stream.StreamBinanceUmFutureDiffDepthEvent;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Depth
{
	private Long lastUpdateId;
	private List<List<BigDecimal>> asks = new ArrayList<List<BigDecimal>>();
	private List<List<BigDecimal>> bids = new ArrayList<List<BigDecimal>>();

	public Long getLastUpdateId()
	{
		return lastUpdateId;
	}

	public void setLastUpdateId(Long lastUpdateId)
	{
		this.lastUpdateId = lastUpdateId;
	}

	public List<List<BigDecimal>> getAsks()
	{
		return asks;
	}

	public void setAsks(List<List<BigDecimal>> asks)
	{
		this.asks = asks;
	}

	public List<List<BigDecimal>> getBids()
	{
		return bids;
	}

	public void setBids(List<List<BigDecimal>> bids)
	{
		this.bids = bids;
	}

	public static Depth fromDepthEvent(StreamBinanceUmFutureDiffDepthEvent streamBinanceUmFutureDiffDepthEvent) {
		Depth depth = new Depth();
		depth.setLastUpdateId(streamBinanceUmFutureDiffDepthEvent.getLastUpdateId());
		depth.setAsks(streamBinanceUmFutureDiffDepthEvent.getAsks());
		depth.setBids(streamBinanceUmFutureDiffDepthEvent.getBids());
		return depth;
	}

}
