package com.appcontactos.javierdiaz.jeunessemiami;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;

import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceActivity;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.appcontactos.javierdiaz.jeunessemiami.activities.LoginActivity;
import com.appcontactos.javierdiaz.jeunessemiami.util.ApplicationController;
import com.appcontactos.javierdiaz.jeunessemiami.util.Config;
import com.appcontactos.javierdiaz.jeunessemiami.util.JsonObjectRequestUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.appcontactos.javierdiaz.jeunessemiami.adaptadores.CustomArrayAdapter;
import com.appcontactos.javierdiaz.jeunessemiami.modelos.RowContactsModel;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    String phoneNumber;
    ListView listView_contactos;
    ArrayList<RowContactsModel> rows = new ArrayList<>();
    HashMap<Double, RowContactsModel> listItems;
    private Button btnSincronizar;
    CustomArrayAdapter customArrayAdapter;
    private Button btn_selecc_all;
    private Button btn_unselecc_all;
    /**
     * activity progress dialog
     */
    protected ProgressDialog mProgressDialog;


    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView_contactos = (ListView) findViewById(R.id.listview_contactos);
        btnSincronizar = (Button) findViewById(R.id.btn_sincronizar);
        btn_selecc_all = (Button) findViewById(R.id.btn_selecctodos);
        btn_unselecc_all = (Button) findViewById(R.id.btn_deselecctodos);
        btnSincronizar.setOnClickListener(this);
        btn_selecc_all.setOnClickListener(this);
        btn_unselecc_all.setOnClickListener(this);
        listItems = new HashMap<>();

        mProgressDialog = new ProgressDialog(this);

        getNumber(this.getContentResolver());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void getNumber(ContentResolver cr) {
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            RowContactsModel row = new RowContactsModel();
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String lastname = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHONETIC_NAME));
            String user_id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            String email = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            System.out.println(name + " .................. " + phoneNumber + " Email....." + email);
            row.setName(name);
            row.setMobile_number(phoneNumber);
            if (lastname != null) {
                row.setSurname(lastname);
            }
            if (user_id != null) {
                row.setUserid(user_id);
            }
            if (email != null) {
                row.setEmail(email);
            }
            row.setChecked(true);

            listItems.put(getFotmatedNumber(phoneNumber), row);

        }
        phones.close();// close cursor
        rows.addAll(listItems.values());
        listView_contactos.setItemsCanFocus(true);
        customArrayAdapter = new CustomArrayAdapter(this,rows);
        listView_contactos.setAdapter(customArrayAdapter);


    }

    /*
    Metodo que formatea el num de tlf para usarlo como un key en el hashmap y no se repitan los num
     */
    private static Double getFotmatedNumber(String number) {
        Double result = 0.0;
        try {
            number = number.replace(" ", "");
            number = number.replace("+", "");
            number = number.replace("-", "");
            result = Double.valueOf(number);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.btn_sincronizar:
                int count=0;
                for(int i = 0; i< rows.size(); i++){
                    if(rows.get(i).isChecked()){
                        count ++;
                    }
                }

                if(count > 0){
                    sincronizarContactos(rows);
                }else{
                    Toast.makeText(getApplicationContext(),"Debe seleccionar al menos un(1) contacto", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_selecctodos:
                selectAll();
                break;
            case R.id.btn_deselecctodos:
                unSelectAll();
                break;
        }


    }


    private void sincronizarContactos(ArrayList<RowContactsModel> arrayContacts){
        showProgressDialog(getString(R.string.sincronizar_contactos));

        String patron ="param1[]=%s&param2[]=%s&param3[]=%s&param4[]=%s&param5[]=%s&";
        String url = Config.url_sincronizar+ Config.metodo_contactos;

                for(int i=0; i < rows.size();i++){
                     if(rows.get(i).isChecked()){
                        String lastname="";


                      if(rows.get(i).getSurname() == null){
                          lastname ="null";
                        }else{
                          lastname = rows.get(i).getSurname();
                      }

                     String params = String.format(patron,rows.get(i).getUserid(),rows.get(i).getName(),lastname
                                 ,rows.get(i).getSurname(),rows.get(i).getEmail(),rows.get(i).getMobile_number());
                     url += params;
                     }
                    }





        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dismissProgressDialog();

                try {
                    String mensaje = response.getString("mensaje");
                    Toast.makeText(getApplicationContext(), mensaje,Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
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
            Log.e("MainActivity.class", "Error al mostrar el Progress Dialog");
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
            Log.e("MainActivity.class", "Error al mostrar el Progress Dialog");
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.cerrar_sesion))
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logout(LoginActivity.userid);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .show();


    }

    private void logout(String iduser){
        showProgressDialog(getString(R.string.cargando));

        String url = String
                .format(Config.url+ Config.metodo_logout+"user_id=%s",
                        iduser);
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dismissProgressDialog();
                Toast.makeText(getApplicationContext(), getString(R.string.gracias_por_su_visita),Toast.LENGTH_LONG).show();
                finish();
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

    private void selectAll(){

        for(int i = 0;i < rows.size(); i++){
            rows.get(i).setChecked(true);
        }
        customArrayAdapter.notifyDataSetChanged();


    }

    private void unSelectAll(){

        for(int i = 0;i < rows.size(); i++){
            rows.get(i).setChecked(false);
        }
        customArrayAdapter.notifyDataSetChanged();
    }
}



