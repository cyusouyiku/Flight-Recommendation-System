package com.example.app.scheduler;

import com.example.app.service.PriceAlertService;
import com.example.app.service.PriceTrendService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 价格相关定时任务
 */
@Component
public class PriceScheduler {

    private final PriceTrendService priceTrendService;
    private final PriceAlertService priceAlertService;

    public PriceScheduler(PriceTrendService priceTrendService, PriceAlertService priceAlertService) {
        this.priceTrendService = priceTrendService;
        this.priceAlertService = priceAlertService;
    }

    /** 每小时采集一次价格到历史表 */
    @Scheduled(cron = "0 0 * * * ?")
    public void collectPrices() {
        priceTrendService.collectPrices();
    }

    /** 每30分钟检查低价提醒 */
    @Scheduled(cron = "0 */30 * * * ?")
    public void checkPriceAlerts() {
        priceAlertService.checkAndNotify();
    }
}
