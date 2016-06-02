package com.appcontactos.javierdiaz.jeunessemiami.util;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SinAsignarT1 on 02/06/2016.
 */
public class JsonObjectRequestUtil extends JsonObjectRequest {

    private String user = "goldenboy";
    private String pass = "$2a$12$W4cqEjYmpQ7zJ2fBq45mqOUkNoGBR5tZm9GacR/GJ.h8TgSDg2Azm";

    public JsonObjectRequestUtil(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public JsonObjectRequestUtil(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


//    @Override
//    public Map<String, String> getHeaders() throws AuthFailureError {
//        Map<String, String> params = new HashMap<String, String>();
//
//        String base64EncodedCredentials = "Basic " + Base64.encodeToString(
//                (user + ":" + pass).getBytes(),
//                Base64.NO_WRAP);
//        params.put("Authorization",base64EncodedCredentials);
//        return params;
//    }
}
