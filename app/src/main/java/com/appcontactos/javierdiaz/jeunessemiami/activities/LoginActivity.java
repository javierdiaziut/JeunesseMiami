package com.appcontactos.javierdiaz.jeunessemiami.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.appcontactos.javierdiaz.jeunessemiami.R;

public class LoginActivity extends AppCompatActivity {

    private TextView txtUser;
    private TextView txtPass;
    private Button btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUser = (TextView) findViewById(R.id.login_username);
        txtPass = (TextView) findViewById(R.id.login_password);
        btnLogin =(Button) findViewById(R.id.login_button);
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