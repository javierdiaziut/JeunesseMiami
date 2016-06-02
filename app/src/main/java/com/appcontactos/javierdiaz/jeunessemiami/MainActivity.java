package com.appcontactos.javierdiaz.jeunessemiami;


import android.content.ContentResolver;
import android.content.Intent;
import cz.msebera.android.httpclient.Header;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceActivity;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.appcontactos.javierdiaz.jeunessemiami.util.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.appcontactos.javierdiaz.jeunessemiami.adaptadores.CustomArrayAdapter;
import com.appcontactos.javierdiaz.jeunessemiami.modelos.RowContactsModel;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    String phoneNumber;
    ListView listView_contactos;
    ArrayList<RowContactsModel> rows = new ArrayList<>();
    HashMap<Double, RowContactsModel> listItems;
    private Button btnSincronizar;

    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView_contactos = (ListView) findViewById(R.id.listview_contactos);
        btnSincronizar = (Button) findViewById(R.id.btn_sincronizar);
        btnSincronizar.setOnClickListener(this);
        listItems = new HashMap<>();

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

            listItems.put(getFotmatedNumber(phoneNumber), row);

        }
        phones.close();// close cursor
        rows.addAll(listItems.values());
        listView_contactos.setItemsCanFocus(true);
        listView_contactos.setAdapter(new CustomArrayAdapter(this, rows));


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
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.appcontactos.javierdiaz.jeunessemiami/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.appcontactos.javierdiaz.jeunessemiami/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onClick(View v) {
        int count=0;
        for(int i = 0; i< rows.size(); i++){
                if(rows.get(i).isChecked()){
                    count ++;
                }
        }

        if(count > 0){
            sincronizar();
        }
    }

    private void sincronizar(){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String url = Config.url+ Config.metodo_contactos;


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

        client.get(url,params,handler);
    }
}



