package ru.javaops.masterjava.service.mail;

import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.config.Configs;
import ru.javaops.web.WebStateException;

@Slf4j
public class MailWSClientMain {
    public static void main(String[] args) throws WebStateException {
      FileDataSource dataSource1 = new FileDataSource(Configs.getConfigFile("files/лайки.txt"));
      FileDataSource dataSource2 = new FileDataSource(Configs.getConfigFile("files/скан.PDF"));
      List<Attachment> attachments = Arrays
          .asList(new Attachment("лайки.txt", new DataHandler(dataSource1)),
              new Attachment("скан.PDF", new DataHandler(dataSource2)));

        String state = MailWSClient.sendToGroup(
                ImmutableSet.of(new Addressee("To <aleksandr_986@inbox.ru>")),
                ImmutableSet.of(new Addressee("Copy <aleksandr_986@inbox.ru>")), "Subject", "Body", attachments);
        System.out.println(state);
    }
}