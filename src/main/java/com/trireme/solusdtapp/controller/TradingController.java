package com.trireme.solusdtapp.controller;

import com.trireme.solusdtapp.service.TradingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TradingController {

    private final TradingService tradingService;

    public TradingController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    // in case that manual trigger is needed
    @GetMapping("/candlestick")
    public void getCandlestickData() {
        tradingService.getCandlestickData();
    }

    // in case that manual trigger is needed
    @GetMapping("/orderbook")
    public void getOrderBookData() {
        tradingService.getOrderBookData();
    }
}
