package com.pawlik.przemek.onlinegradebook.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class NotificationDto {

    @Schema(description = "List of email addresses")
    private List<String> emailAddresses;

    @Schema(description = "Message to be sent", example = "Hello, this is a message.")
    private String message;
}
