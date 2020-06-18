package com.example.monagendapartage.Model.Helper;

import java.util.UUID;

public class HelperCustomClass {

    public static String generateString() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid;
    }
}
