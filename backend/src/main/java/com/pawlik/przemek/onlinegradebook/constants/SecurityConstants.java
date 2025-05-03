package com.pawlik.przemek.onlinegradebook.constants;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SecurityConstants {

    public static final String JWT_KEY = "jxgEQeXHuPq8VdbyYFNkANdudQ53YUn4";
    public static final String JWT_HEADER = "Authorization";
    public static final long JWT_EXPIRATION_MILLIS = 45L * 60L * 1000L; // 45 minutes
}
