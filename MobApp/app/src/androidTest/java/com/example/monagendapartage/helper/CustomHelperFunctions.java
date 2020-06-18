package com.example.monagendapartage.helper;

public class CustomHelperFunctions {

    public static void customSleep() {
        try {
            Thread.sleep(5000); // TODO replace by IdlingResource
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
