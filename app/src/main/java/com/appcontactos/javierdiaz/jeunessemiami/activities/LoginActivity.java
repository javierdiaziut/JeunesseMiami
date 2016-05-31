package com.appcontactos.javierdiaz.jeunessemiami.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.appcontactos.javierdiaz.jeunessemiami.MainActivity;
import com.appcontactos.javierdiaz.jeunessemiami.R;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private TextView txtUser;
    private TextView txtPass;
    private Button btnLogin;
    public static final int PERMISSIONS_REQUEST = 0;


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
                        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
                            List<String> permissionsNeeded = new ArrayList<String>();
                            permissionsNeeded.add(Manifest.permission.READ_CONTACTS );

                            requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                                    PERMISSIONS_REQUEST);
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
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


}