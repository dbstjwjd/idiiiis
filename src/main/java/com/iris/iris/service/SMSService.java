package com.iris.iris.service;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Random;

@Service
public class SMSService {
    public void sendSms(String phoneNum, String verKey) {
        DefaultMessageService messageService = NurigoApp.INSTANCE.initialize("NCSEIS05KZ5NXZ4E", "2OKACTN9PVKAEH5BY8JLDDPKCCOHNE6P", "https://api.solapi.com");
        Message message = new Message();
        message.setFrom("01024616781");
        message.setTo(phoneNum);
        message.setText("인증번호 [" + verKey + "] ");

        try {
            messageService.send(message);
        } catch (NurigoMessageNotReceivedException exception) {
            System.out.println(exception.getFailedMessageList());
            System.out.println(exception.getMessage());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    public String createRandomNum() {
        Random rand = new Random();
        String randomNum = "";
        for (int i = 0; i < 6; i++) {
            String random = Integer.toString(rand.nextInt(10));
            randomNum += random;
        }
        System.out.println(randomNum);
        return randomNum;
    }
}
