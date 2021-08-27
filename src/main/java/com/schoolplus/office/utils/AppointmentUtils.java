package com.schoolplus.office.utils;

import java.text.MessageFormat;

public final class AppointmentUtils {

    private final static String nameFormat = "{0} {1} öğretmen ile randevu";

    public static String generateName(String firstName, String lastName) {
        return MessageFormat.format(nameFormat, firstName, lastName);
    }

}
