package com.pangtudy.userapi.user.service;

public interface EmailService {
    void sendMail(String to, String sub, String text);
}
