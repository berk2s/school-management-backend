package com.schoolplus.office.utils;

import java.time.LocalDateTime;

public final class TokenUtils {

    public static boolean isValid(LocalDateTime expiryDate, LocalDateTime notBefore) {
        return !(LocalDateTime.now().isBefore(notBefore) || LocalDateTime.now().isAfter(expiryDate));
    }

}
