package com.appcontactos.javierdiaz.jeunessemiami;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appcontactos.javierdiaz.jeunessemiami.activities.LoginActivity;
import com.appcontactos.javierdiaz.jeunessemiami.util.ApplicationController;
import com.appcontactos.javierdiaz.jeunessemiami.util.Config;
import com.appcontactos.javierdiaz.jeunessemiami.util.JsonObjectRequestContacts;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private String phoneNumber;
    private ListView listView_contactos;
    private ArrayList<RowContactsModel> rows = new ArrayList<>();
    private HashMap<Double, RowContactsModel> listItems;
    private Button btnSincronizar;
    private CustomArrayAdapter customArrayAdapter;
    private Button btn_selecc_all;
    private Button btn_unselecc_all;
    private int pendingRequests = 0;
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
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (phones.moveToNext()) {
            RowContactsModel row = new RowContactsModel();
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String lastname = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHONETIC_NAME));
            String user_id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            System.out.println(name + " .................. " + phoneNumber + "ID............." + user_id);
            row.setName(name);
            row.setMobile_number(phoneNumber);
            if (lastname != null) {
                row.setSurname(lastname);
            }
            if (user_id != null) {
                row.setUserid(user_id);
            }

            row.setChecked(true);
            Cursor emails = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + user_id, null, null);
            while (emails.moveToNext())
            {
                String email1 = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                row.setEmail(email1);
                break;
            }
            emails.close();

            listItems.put(getFotmatedNumber(phoneNumber), row);

        }
        phones.close();// close cursor
        rows.addAll(listItems.values());
        Collections.sort(rows, new Comparator<RowContactsModel>() {
            public int compare(RowContactsModel v1, RowContactsModel v2) {
                return v1.getName().compareToIgnoreCase(v2.getName());
            }
        });

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
                    showProgressDialog(getString(R.string.sincronizar_contactos));
                    for(int i = 0; i< rows.size(); i++){
                        if(rows.get(i).isChecked()){
                            synContacto(getApplicationContext(),rows.get(i).getUserid(),rows.get(i).getName(),
                                    rows.get(i).getSurname(),rows.get(i).getEmail(),rows.get(i).getMobile_number());
                            pendingRequests++;
                        }
                    }

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
    /*
    Método para sincrinizar los contactos en bd remota. No se sincroniza por lote.
     */

    public void synContacto(Context context, final String userid, final String nombre, final String apellido, final String correo, final String tlf){
        String url = Config.url_sincronizar;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("INSERTAR CONTACTO",response);
                pendingRequests--;
                if(pendingRequests == 0){
                    dismissProgressDialog();
                    Toast.makeText(getApplicationContext(), "Usuarios sincronizados satisfactoriamente",Toast.LENGTH_LONG).show();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pendingRequests--;
                if(pendingRequests == 0){
                    dismissProgressDialog();
                    Toast.makeText(getApplicationContext(), "Usuarios sincronizados con errores",Toast.LENGTH_LONG).show();

                }Log.d("ERROR AL INSERTAR",error.toString());
                //Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("method","addContactsLot");
                params.put("param1[]",LoginActivity.userid);
                params.put("param2[]",nombre);
                if(apellido != null){
                    params.put("param3[]",apellido);
                }else
                {
                    params.put("param3[]","apellido");
                }
                if(correo != null){
                    params.put("param4[]",correo);
                }else
                {
                    params.put("param4[]","email"+userid+"@email.com");
                }
                params.put("param5[]",tlf);

                return params;
            }


        };
        sr.setRetryPolicy(new DefaultRetryPolicy(12000 * 27000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(sr);
    }
}



