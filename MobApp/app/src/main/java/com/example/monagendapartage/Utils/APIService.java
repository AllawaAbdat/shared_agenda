package com.example.monagendapartage.Utils;


import com.example.monagendapartage.Notifications.MyResponse;
import com.example.monagendapartage.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAluVhs_8:APA91bHUM4CsM8wzgKAyGntot87MEkyW0pb0aCbdnxHN0dfZ-hsH-3bedYpYLProolK-aPeGXl4XBObBdQ8MIXhrvx6xk_coeqdhFCCUAC46D5CMZvNvUAQ0dc6ytKywj_fg4m46Llcs",
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
