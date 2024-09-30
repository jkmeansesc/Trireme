package com.trireme.solusdtapp.service;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.market.MarketInterval;
import com.bybit.api.client.domain.market.request.MarketDataRequest;
import com.bybit.api.client.service.BybitApiClientFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TradingService {

    private final SimpMessagingTemplate messagingTemplate;

    public TradingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Fetch candlestick data and broadcast it at 60s interval.
    @Scheduled(fixedRate = 60000)
    public void getCandlestickData() {
        var client = BybitApiClientFactory.newInstance(BybitApiConfig.TESTNET_DOMAIN).newMarketDataRestClient();
        var marketKLineRequest = MarketDataRequest.builder()
                .category(CategoryType.LINEAR)
                .symbol("SOLUSDT")
                .marketInterval(MarketInterval.ONE_MINUTE)
                .end(System.currentTimeMillis())
                .build();

        var marketKlineResult = client.getMarketLinesData(marketKLineRequest);
        // Broadcast data to WebSocket topic /topic/candlestick
        messagingTemplate.convertAndSend("/topic/candlestick", marketKlineResult);
    }

    // Fetch order book data and broadcast it at 5s interval
    @Scheduled(fixedRate = 5000)
    public void getOrderBookData() {
        var client = BybitApiClientFactory.newInstance(BybitApiConfig.TESTNET_DOMAIN, true).newAsyncMarketDataRestClient();
        var orderbookRequest = MarketDataRequest.builder()
                .category(CategoryType.LINEAR)
                .symbol("SOLUSDT")
                .build();

        client.getMarketOrderBook(orderbookRequest, result -> {
            // Broadcast data to WebSocket topic /topic/orderbook
            messagingTemplate.convertAndSend("/topic/orderbook", result);
        });
    }
}
