package com.appcontactos.javierdiaz.jeunessemiami.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.appcontactos.javierdiaz.jeunessemiami.MainActivity;
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
    protected ProgressDialog mProgressDialog;
    public static String userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUser = (TextView) findViewById(R.id.login_username);
        txtPass = (TextView) findViewById(R.id.login_password);
        btnLogin =(Button) findViewById(R.id.login_button);
        mProgressDialog = new ProgressDialog(this);
        userid ="";
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
                            testLogin(txtUser.getText().toString(),txtPass.getText().toString());
                        }
                    } else {
                        testLogin(txtUser.getText().toString(),txtPass.getText().toString());

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


   private void testLogin(String user, String pass){
       userid = "";
       showProgressDialog(getString(R.string.cargando));
       String url = String
               .format(Config.url+ Config.metodo_login+"user_name=%s&user_password=%s",
                       user,
                       pass);

       Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {

           @Override
           public void onResponse(JSONObject response) {
               dismissProgressDialog();
               if(response.has("user_fname")){
                   try {
                       userid = response.getString("user_id");
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
                   Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
                   startActivity(intent);
                   txtUser.setText("");
                   txtPass.setText("");
               }else{
                   Toast.makeText(getApplicationContext(), "Usuario y/o password invalido",Toast.LENGTH_LONG).show();
               }

          }
       };


       Response.ErrorListener errorListener = new Response.ErrorListener() {

           @Override
           public void onErrorResponse(VolleyError error) {
               dismissProgressDialog();
               Toast.makeText(getApplicationContext(), getString(R.string.error_servicios),Toast.LENGTH_LONG).show();
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

    /**
     * show a progress dialog with a custom message
     *
     * @param message
     */
    public void showProgressDialog(String message) {
        if (mProgressDialog != null) {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        } else {
            Log.e("LoginActivity.class", "Error al mostrar el Progress Dialog");
        }
    }

    /**
     * hide the progress dialog
     */
    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } else {
            Log.e("LoginActivity.class", "Error al mostrar el Progress Dialog");
        }
    }
}