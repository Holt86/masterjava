package ru.javaops.masterjava.service.mail;

import javax.xml.ws.Endpoint;
import ru.javaops.masterjava.persist.DBITestProvider;

/**
 * User: gkislin
 * Date: 28.05.2014
 */
public class MailServicePublisher {

    public static void main(String[] args) {
        DBITestProvider.initDBI();
        Endpoint.publish("http://localhost:8080/mail/mailService", new MailServiceImpl());
    }
}
