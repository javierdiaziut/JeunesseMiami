package com.appcontactos.javierdiaz.jeunessemiami.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.appcontactos.javierdiaz.jeunessemiami.R;
import com.appcontactos.javierdiaz.jeunessemiami.util.ApplicationController;
import com.appcontactos.javierdiaz.jeunessemiami.util.Config;
import com.appcontactos.javierdiaz.jeunessemiami.util.JsonObjectRequestUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;

public class LoginActivity extends AppCompatActivity {

    private TextView txtUser;
    private TextView txtPass;
    private Button btnLogin;
    public static final int PERMISSIONS_REQUEST = 0;
    private Map<String, String> mParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUser = (TextView) findViewById(R.id.login_username);
        txtPass = (TextView) findViewById(R.id.login_password);
        btnLogin =(Button) findViewById(R.id.login_button);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarCampos(txtUser,txtPass)){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        int permissionCheck = ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_CONTACTS );
                        int permissionInternet = ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.INTERNET );
                        if((permissionCheck != PackageManager.PERMISSION_GRANTED) || (permissionInternet != PackageManager.PERMISSION_GRANTED) ) {
                            List<String> permissionsNeeded = new ArrayList<String>();
                            permissionsNeeded.add(Manifest.permission.READ_CONTACTS );
                            permissionsNeeded.add(Manifest.permission.INTERNET );

                            requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                                    PERMISSIONS_REQUEST);
                        } else {
                            testLogin();
                            //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            //startActivity(intent);
                        }
                    } else {
                        testLogin();
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        startActivity(intent);
                    }



                }
            }
        });
    }

    /*
    Metodo para validar si los campos de user y pass no estan vacios.
     */
    private boolean validarCampos(TextView user, TextView pass){
        if((user.length() == 0) || (pass.length() == 0)){
             return false;
        }else{
        return true;
        }
    }

    private void login(){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String url = Config.url+ Config.metodo_login;

        params.put("user_name","pedro");
        params.put("user_password","123");

//        for(int i=0; i < rows.size();i++){
//            if(rows.get(i).isChecked()){
//                params.put("params1[]",rows.get(i).getUserid());
//                params.put("params2[]",rows.get(i).getName());
//                if(rows.get(i).getSurname() == null){
//                    params.put("params3[]","null");
//                }else{
//                    params.put("params3[]",rows.get(i).getSurname());
//                }
//                params.put("params3[]",rows.get(i).getSurname());
//                params.put("params4[]",rows.get(i).getEmail());
//                params.put("params5[]",rows.get(i).getMobile_number());
//            }
//        }

        //Log.e(url, params.toString());
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String txtResponse = String.valueOf(responseBody);
                Log.e("Conexion Exitosa", String.valueOf(responseBody));
                Log.e("Conexion Exitosa", String.valueOf(statusCode));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("Error Conexion", String.valueOf(responseBody));
                Log.e("Error Conexion", String.valueOf(statusCode));
            }
        };
        Log.e("URL CONNECTION",url);
        Log.e("PARAMS",params.toString());
        client.get(url,params,handler);
    }

    private void loginVolley(){

        String url = Config.url+ Config.metodo_login+"user_name=pedro&user_password=123";

        JsonObjectRequest req = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    VolleyLog.e("Error: ", networkResponse.statusCode);
                }
                VolleyLog.e("Error: ", error.getMessage());
            }


        });


        ApplicationController.getInstance(this).addToRequestQueue(req);

    }

   private void testLogin(){

       String url = Config.url+ Config.metodo_login+"user_name=pedro&user_password=123";

       Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {

           @Override
           public void onResponse(JSONObject response) {
               try {
                   VolleyLog.v("Response:%n %s", response.toString(4));
               } catch (JSONException e) {
                   e.printStackTrace();
               }
          }
       };


       Response.ErrorListener errorListener = new Response.ErrorListener() {

           @Override
           public void onErrorResponse(VolleyError error) {
               NetworkResponse networkResponse = error.networkResponse;
               if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                   VolleyLog.e("Error: ", networkResponse.statusCode);
               }
               VolleyLog.e("Error: ", error.getMessage());

           }
       };

       JsonObjectRequestUtil jsonObjectRequest = new JsonObjectRequestUtil(Request.Method.GET, url, null, responseListener, errorListener);
       jsonObjectRequest.setShouldCache(false);
       ApplicationController.getInstance(this).addToRequestQueue(jsonObjectRequest);

   }
}