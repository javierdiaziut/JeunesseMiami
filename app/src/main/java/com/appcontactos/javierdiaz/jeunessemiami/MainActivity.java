package com.appcontactos.javierdiaz.jeunessemiami;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.appcontactos.javierdiaz.jeunessemiami.adaptadores.CustomArrayAdapter;
import com.appcontactos.javierdiaz.jeunessemiami.modelos.RowContactsModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String phoneNumber;
    ListView listView_contactos;
    ArrayList <String> aa= new ArrayList<String>();
    ArrayList<RowContactsModel> rows = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView_contactos = (ListView) findViewById(R.id.listview_contactos);
        getNumber(this.getContentResolver());
    }

    public void getNumber(ContentResolver cr)
    {
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            RowContactsModel row = new RowContactsModel();
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            System.out.println(name +" .................. "+phoneNumber);
            row.setName(name);
            row.setNumber(phoneNumber);
            rows.add(row);

            aa.add(phoneNumber);
        }
        phones.close();// close cursor
        listView_contactos.setAdapter(new CustomArrayAdapter(this, rows));
        //display contact numbers in the list
    }
}



