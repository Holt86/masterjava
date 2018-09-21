package ru.javaops.masterjava.service.mail;

import lombok.*;

/**
 * gkislin
 * 15.11.2016
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Addressee {
    private @NonNull String email;
    private @NonNull String name;
}
