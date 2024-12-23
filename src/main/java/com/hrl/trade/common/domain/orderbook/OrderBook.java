package com.hrl.trade.common.domain.orderbook;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Setter
@Getter
public class OrderBook {

    public static Logger logger = LoggerFactory.getLogger(OrderBook.class);

    public int pricePrecision;
    public int qtyPrecision;

    public String platform;
    public String symbol;

    public TreeMap<BigDecimal, OrderBookEntry> asksTreeMap = new TreeMap<>();
    public TreeMap<BigDecimal, OrderBookEntry> bidsTreeMap = new TreeMap<>();

    public int orderBookSize = 100;

    public boolean bidRemoveFlag = false;

    //ask被Remove了
    public boolean askRemoveFlag = false;

    public volatile boolean init = false;
    
    public volatile boolean topNChanging = false;

    public int topN = 10;



    public OrderBook(int pricePrecision, String platform, String symbol) {
        this.pricePrecision = pricePrecision;
        this.platform = platform;
        this.symbol = symbol;
    }

    public int getPricePrecision() {
        return pricePrecision;
    }

    public void setPricePrecision(int pricePrecision) {
        this.pricePrecision = pricePrecision;
    }

    public int getQtyPrecision() {
        return qtyPrecision;
    }

    public void setQtyPrecision(int qtyPrecision) {
        this.qtyPrecision = qtyPrecision;
    }

    public TreeMap<BigDecimal, OrderBookEntry> getAsksTreeMap() {
        return asksTreeMap;
    }

    public void setAsksTreeMap(TreeMap<BigDecimal, OrderBookEntry> asksTreeMap) {
        this.asksTreeMap = asksTreeMap;
    }

    public TreeMap<BigDecimal, OrderBookEntry> getBidsTreeMap() {
        return bidsTreeMap;
    }

    public void setBidsTreeMap(TreeMap<BigDecimal, OrderBookEntry> bidsTreeMap) {
        this.bidsTreeMap = bidsTreeMap;
    }

    // ---- CALCULATED FIELDS -------------------------------------------------

    public BigDecimal getFirstAskPrice() {
        return this.asksTreeMap.firstEntry().getValue().getPrice();
    }

    public BigDecimal getFirstBidPrice() {
        return this.bidsTreeMap.firstEntry().getValue().getPrice();
    }

    public List<OrderBookEntry> getAsksList() {
        return new ArrayList<OrderBookEntry>(asksTreeMap.values());
    }

    public List<OrderBookEntry> getBidsList() {
        return new ArrayList<OrderBookEntry>(bidsTreeMap.values());
    }

    public OrderBook getSnapshot(){
        OrderBook  orderBook = new OrderBook(this.pricePrecision, this.platform, this.symbol);
        orderBook.setOrderBookSize(this.orderBookSize);
        orderBook.setAsksTreeMap(new TreeMap<>(this.asksTreeMap));
        orderBook.setBidsTreeMap(new TreeMap<>(this.bidsTreeMap));
        return orderBook;
    }

    public boolean isInTopN(TreeMap<BigDecimal, OrderBookEntry> map, double price, int n){

        Iterator<Map.Entry<BigDecimal, OrderBookEntry>> iterator = map.entrySet().iterator();
        // 获取前n个最大的键值对
        for (int i = 0; i < n && iterator.hasNext(); i++) {
            Map.Entry<BigDecimal, OrderBookEntry> entry = iterator.next();
            if(price == entry.getValue().getPrice().doubleValue()){
                return true;
            }
        }
        return false;
    }

    public void addAskList(List<List<BigDecimal>> askList) {

        askList.stream().forEach(ask -> {
            BigDecimal price = ask.get(0);
            BigDecimal qty = ask.get(1);

            
            if(this.isInTopN(this.asksTreeMap,price.doubleValue(),topN)) {
                this.topNChanging = true;
            }
            
            //logger.info("is first.:{}", price.doubleValue() == asksTreeMap.firstKey().doubleValue());

            if(asksTreeMap.size() < orderBookSize) {
                //logger.info("add ask when deleted");
                this.asksTreeMap.put(ask.get(0), new OrderBookEntry(price, qty));
                //this.askRemoveFlag=false;
                return;
            }

            BigDecimal lastKey = asksTreeMap.lastKey();
            if (lastKey.doubleValue() < price.doubleValue()) {
                logger.trace("price :{} > lastKey:{}, not save.", price, lastKey);
                return;
            }


            if (qty.doubleValue() == 0) {
                asksTreeMap.remove(price);
                logger.trace("remove price {} from ask list", price, platform, symbol);
                //this.askRemoveFlag = true;
                return;
            }


            //添加到asks列表
            this.asksTreeMap.put(ask.get(0), new OrderBookEntry(price, qty));

            //最后一个key与当前的不一样，说明有插入新的到前面了，把之前最后的key删除，
            // 但是不需要设置askRemoveFlag为true,因为不需要再添加
            if(asksTreeMap.size() > orderBookSize) {
                this.asksTreeMap.remove(lastKey);
            }

        });
    }


    public void initAskList(List<List<BigDecimal>> askList) {
        logger.info("initAskList.platform:{},symbol:{}", platform, symbol);

        for (int i = 0; i < orderBookSize; i++) {
            List<BigDecimal> ask = askList.get(i);
            BigDecimal price = ask.get(0);
            BigDecimal qty = ask.get(1);

            if (qty.doubleValue() == 0) {
                this.asksTreeMap.remove(price);
                i--;
                logger.trace("remove price {} from ask list. platform:{},symbol:{}", price, platform, symbol);
            }

            this.asksTreeMap.put(price, new OrderBookEntry(price, qty));
        }

        logger.info("initAskList.platform:{},symbol:{},askSize:{}", platform, symbol,askList.size());

    }

    public void initBidList(List<List<BigDecimal>> bidList) {
        logger.info("initBidList.platform:{},symbol:{}", platform, symbol);

        for (int i = 0; i < orderBookSize; i++) {
            List<BigDecimal> bid = bidList.get(i);
            BigDecimal price = bid.get(0);
            BigDecimal qty = bid.get(1);


            if (qty.doubleValue() == 0) {
                this.bidsTreeMap.remove(price);
                logger.trace("remove price {} from bid list. platform:{},symbol:{}", price, platform, symbol);
            }

            this.bidsTreeMap.put(price, new OrderBookEntry(price, qty));
        }
    }


    public void addBidList(List<List<BigDecimal>> bidList) {

        bidList.stream().forEach(bid -> {
            BigDecimal price = bid.get(0);
            BigDecimal qty = bid.get(1);

            if(this.isInTopN(this.asksTreeMap,price.doubleValue(),topN)) {
                this.topNChanging = true;
            }

            if(bidsTreeMap.size() < orderBookSize) {
                this.bidsTreeMap.put(bid.get(0), new OrderBookEntry(price, qty));
                //bidRemoveFlag = false;
                return;
            }

            BigDecimal lastKey = null;
            lastKey = bidsTreeMap.lastKey();
            if (lastKey.doubleValue() < price.doubleValue()) {
                logger.trace("price :{} > lastKey:{}, not save.", price, lastKey);
                return;
            }

            if (qty.doubleValue() == 0) {
                this.bidsTreeMap.remove(price);
                logger.trace("remove price {} from bid list. platform:{},symbol:{}", price, platform, symbol);
               // bidRemoveFlag = true;
                return;
            }

            this.bidsTreeMap.put(bid.get(0), new OrderBookEntry(price, qty));

            if(bidsTreeMap.size() > orderBookSize) {
                this.bidsTreeMap.remove(lastKey);
            }
        });
    }

    public void clear() {
        this.asksTreeMap = new TreeMap<>();
        this.bidsTreeMap = new TreeMap<>();
        this.init = false;
    }

    public String simplePrint() {
        if (asksTreeMap.isEmpty()) {
            return "Empty order book";
        }
        StringBuilder sb = new StringBuilder();


        int count = 0;

        Iterator<Map.Entry<BigDecimal, OrderBookEntry>> iterator = asksTreeMap.entrySet().iterator();
        while (iterator.hasNext()) {

            if (count >= 3) {
                break;
            }
            Map.Entry<BigDecimal, OrderBookEntry> entry = iterator.next();
            sb.append(entry.getKey().doubleValue() + "," + entry.getValue().getQty().doubleValue() + "\n");
            count++;
        }

		/*for(int i = 0; i < 10; i++) {
			sb.append(asks.get(0).getPrice().doubleValue() + "," +asks.get(0).getQty().doubleValue() + "\n");
		}*/
        String str = sb.toString();
        //logger.trace("\n order book print " + str);
        //logger.trace("#############################################################");

        logger.trace("orderbook ask size:{},bid size:{}", asksTreeMap.size(), bidsTreeMap.size());
        return str;
    }


    public BigDecimal getAveragePriceOfFistAskAndBid() {
        BigDecimal avg =  asksTreeMap.firstEntry().getValue().getPrice().add(bidsTreeMap.firstEntry().getValue().getPrice()).divide(new BigDecimal(2.0));
      logger.trace("avg:{},symbol:{},platform:{}", avg,symbol,platform);
      return avg;
    }


    /*public double getQuoteCoinOfSellingQty(double toSellBaseCoinQty) {

        double qtyRemainder =  toSellBaseCoinQty;

        double quoteCoinTotal = 0;

        for ( int i = 0; i < orderBookSize; i++) {

            OrderBookEntry orderBookEntry = bidsTreeMap.get(0);

            double price = orderBookEntry.getPrice().doubleValue();
            double qty = orderBookEntry.getQty().doubleValue();


            if(qtyRemainder < qty) {
                double quoteCoinQty = qtyRemainder * price;
                quoteCoinQty += quoteCoinTotal;
                return quoteCoinTotal;
            } else {
                quoteCoinTotal += orderBookEntry.calculateTotal();
            }
        }

        throw new RuntimeException("Not enough OrderBook to Buy base coin");

    }*/


    //The price for a market pair is how much of the Quote currency it costs to buy one unit of the base currency.
    // 60000 usdt 买一个 btc
    //btcusdt = 60000, usdt is quotecoind,  btc is basecoin

    //买 卖以basecoin为主体， 买basecoin， 卖basecoin

    //oneShare使用人民币，usdtrmb, 需要维护usdtrmb的orderbook,

    //usdt -> intermediate , intermediate -> han
    //需要卖usdt。 所以需要遍历买价列表bids，
    //买intermediate
   /* public SellingQuoteCoinTransactionOrder calcOneShareOfSellingQuoteCoin(double toSellQuoteCoinQty, int ) {

        SellingQuoteCoinTransactionOrder sellingQuoteCoinTransactionOrder = new SellingQuoteCoinTransactionOrder();

        double sellingQuoteCoinTotal = 0;

        double gettingBaseCoinTotalQty = 0;

        for ( Map.Entry<BigDecimal, OrderBookEntry> entry : asksTreeMap.entrySet()) {
            OrderBookEntry orderBookEntry = entry.getValue();
            double orderBookEntryTotal = orderBookEntry.calculateTotal().doubleValue();

            // 需要计算
            if(sellingQuoteCoinTotal + orderBookEntryTotal >= toSellQuoteCoinQty) {

                //1. 如果第一个单就够了, 多于一个单也是同样操作
                double lastOrderBookShare  = toSellQuoteCoinQty - sellingQuoteCoinTotal;
                double lastOrderQty = new BigDecimal(lastOrderBookShare)
                        .divide( orderBookEntry.getPrice(), CoreConstants.dividePrecision, RoundingMode.HALF_DOWN).doubleValue();

                OrderBookEntry lastOrderBookEntry = orderBookEntry.copy();
                lastOrderBookEntry.setQty( new BigDecimal(lastOrderQty));
                sellingQuoteCoinTransactionOrder.addOrderBookEntry(lastOrderBookEntry);

                sellingQuoteCoinTransactionOrder.setFillOneShare(true);

                gettingBaseCoinTotalQty += lastOrderQty;
                sellingQuoteCoinTransactionOrder.setGettingBaseCoinQty(gettingBaseCoinTotalQty);

                sellingQuoteCoinTotal += orderBookEntry.getPrice().multiply(new BigDecimal(lastOrderQty)).doubleValue();
                sellingQuoteCoinTransactionOrder.setSellingQuoteCoinQty(sellingQuoteCoinTotal);

                return sellingQuoteCoinTransactionOrder;
            } else {
                sellingQuoteCoinTransactionOrder.addOrderBookEntry(orderBookEntry);
                gettingBaseCoinTotalQty += orderBookEntry.getQty().doubleValue();
                sellingQuoteCoinTotal += orderBookEntryTotal;
            }
        }

        sellingQuoteCoinTransactionOrder.setGettingBaseCoinQty(gettingBaseCoinTotalQty);
        sellingQuoteCoinTransactionOrder.setFillOneShare(false);
        return sellingQuoteCoinTransactionOrder;
    }*/


   /* public SellingBaseCoinTransactionOrder calcSellingBaseCoinTransactionOrders(double toSellBaseCoinQty){
        SellingBaseCoinTransactionOrder sellingBaseCoinTransactionOrder = new SellingBaseCoinTransactionOrder();
        double sellingBaseCoinTotal = 0;

        double gettingQuoteCoinQty=0;

        for ( Map.Entry<BigDecimal, OrderBookEntry> entry : bidsTreeMap.entrySet()) {
            OrderBookEntry orderBookEntry = entry.getValue();

            if(sellingBaseCoinTotal + orderBookEntry.getQty().doubleValue() > toSellBaseCoinQty){

                double lastOrderQty = toSellBaseCoinQty - sellingBaseCoinTotal;
                OrderBookEntry lastOrderBookEntry = orderBookEntry.copy();
                lastOrderBookEntry.setQty(new BigDecimal(lastOrderQty));
                sellingBaseCoinTransactionOrder.addOrderBookEntry(lastOrderBookEntry);

                sellingBaseCoinTransactionOrder.setFillOneShare(true);

                sellingBaseCoinTransactionOrder.setSellingBaseCoinQty(toSellBaseCoinQty);

                gettingQuoteCoinQty += lastOrderQty * orderBookEntry.getPrice().doubleValue();

                sellingBaseCoinTransactionOrder.setGettingQuoteCoinQty(gettingQuoteCoinQty);

                return sellingBaseCoinTransactionOrder;
            }else {
                sellingBaseCoinTransactionOrder.addOrderBookEntry(orderBookEntry);
                gettingQuoteCoinQty += orderBookEntry.calculateTotal().doubleValue();
                sellingBaseCoinTotal += orderBookEntry.getQty().doubleValue();
            }

        }

        sellingBaseCoinTransactionOrder.setFillOneShare(false);
        sellingBaseCoinTransactionOrder.setSellingBaseCoinQty(sellingBaseCoinTotal);
        sellingBaseCoinTransactionOrder.setGettingQuoteCoinQty(gettingQuoteCoinQty);
        return sellingBaseCoinTransactionOrder;
    }*/

    //示例 卖toSellQuoteCoinQty usdt 获得han 或者 intermediateCoin
    //由于账户里面基础币应该是usdt（稳定）， 所以用usdt作为每次交易设定的份额，例如每次交易上限为5个usdt(30元)
    //btcusdt 60000
   /* public BuyingQuoteCoinTransactionOrder calcOneShareOfBuyingQuoteCoin(double toBuyQuoteCoinQty) {
        BuyingQuoteCoinTransactionOrder buyingQuoteCoinTransactionOrder = new BuyingQuoteCoinTransactionOrder();
        double buyingQuoteCoinTotal = 0;

        double sellingBaseCoinTotalQty = 0;
        for ( int i = 0; i < orderBookSize; i++) {
            OrderBookEntry askOrderBookEntry = bidsTreeMap.get(i);
            double orderBookEntryTotal = askOrderBookEntry.calculateTotal().doubleValue();


            // 需要计算
            if (buyingQuoteCoinTotal + orderBookEntryTotal >= toBuyQuoteCoinQty) {
//1. 如果第一个单就够了, 多于一个单也是同样操作
                double lastOrderBookShare = toBuyQuoteCoinQty - buyingQuoteCoinTotal;
                double lastOrderQty = new BigDecimal(lastOrderBookShare).divide(askOrderBookEntry.getPrice(), CoreConstants.dividePrecision, RoundingMode.HALF_DOWN).doubleValue();


                OrderBookEntry lastOrderBookEntry = askOrderBookEntry.copy();
                lastOrderBookEntry.setQty(new BigDecimal(lastOrderQty));
                buyingQuoteCoinTransactionOrder.addOrderBookEntry(lastOrderBookEntry);

                buyingQuoteCoinTransactionOrder.setFillOneShare(true);

                sellingBaseCoinTotalQty += lastOrderQty;

                buyingQuoteCoinTransactionOrder.setSellingBaseCoinQty(sellingBaseCoinTotalQty);

                buyingQuoteCoinTotal += askOrderBookEntry.getPrice().multiply(new BigDecimal(lastOrderQty)).doubleValue();
                buyingQuoteCoinTransactionOrder.setBuyingQuoteCoinQty(buyingQuoteCoinTotal);

                return buyingQuoteCoinTransactionOrder;

            } else {
                buyingQuoteCoinTransactionOrder.addOrderBookEntry(askOrderBookEntry);
                sellingBaseCoinTotalQty += askOrderBookEntry.getQty().doubleValue();
                buyingQuoteCoinTotal += orderBookEntryTotal;
            }
        }
        buyingQuoteCoinTransactionOrder.setBuyingQuoteCoinQty(buyingQuoteCoinTotal);
        buyingQuoteCoinTransactionOrder.setFillOneShare(false);
        return buyingQuoteCoinTransactionOrder;
    }*/

}
