package com.example.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 通知服务：邮件等
 */
@Service
public class NotificationService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@flight-recommend.local}")
    private String fromEmail;

    public void sendPriceAlertEmail(String to, String departure, String arrival, int targetPrice, int currentPrice) {
        if (mailSender == null) return;
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(to);
            msg.setSubject("[机票推荐] 低价提醒：您的目标航线已达期望价格！");
            msg.setText(String.format(
                    "您好！\n\n您关注的航线 %s -> %s 当前最低价 %d 元，已低于您设置的期望价格 %d 元。\n\n快去看看吧！",
                    departure, arrival, currentPrice, targetPrice));
            mailSender.send(msg);
        } catch (Exception e) {
            // 记录日志，不阻塞主流程
        }
    }
}
