package com.linxa.phonebook.service;

import com.linxa.phonebook.data.entity.Contact;

import java.util.Objects;

public final class NotificationService {

    private static final NotificationService INSTANCE = new NotificationService();

    private NotificationService() {
    }

    public static NotificationService getInstance() {
        return INSTANCE;
    }

    public void sendNotification(Contact contact) {
        var message = "Your contact details have been updated.";
        sendSMS(contact.getPhoneNumber(), message);
        if (Objects.nonNull(contact.getEmail()))
            sendEmail(contact.getEmail(), message);
        System.out.println("SENT NOTIFICATION!");
    }

    private void sendSMS(String phoneNumber, String message) {
        // ...
    }

    private void sendEmail(String email, String message) {
        // ...
    }

}
