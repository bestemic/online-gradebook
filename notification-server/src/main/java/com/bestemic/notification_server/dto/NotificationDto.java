package com.bestemic.notification_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class NotificationDto {

    private List<String> emailAddresses;

    private String message;
}
