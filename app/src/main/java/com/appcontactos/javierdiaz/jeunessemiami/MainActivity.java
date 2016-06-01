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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    String phoneNumber;
    ListView listView_contactos;
    ArrayList<RowContactsModel> rows = new ArrayList<>();
    Set<RowContactsModel> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView_contactos = (ListView) findViewById(R.id.listview_contactos);
        listItems = new HashSet<RowContactsModel>();

        getNumber(this.getContentResolver());
    }

    public void getNumber(ContentResolver cr)
    {
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            RowContactsModel row = new RowContactsModel();
            String name= phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String lastname= phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHONETIC_NAME));
            String user_id= phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            String email= phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            System.out.println(name +" .................. "+phoneNumber +" Email....."+ email);
            row.setName(name);
            row.setNumber(phoneNumber);
            listItems.add(row);
            //rows.add(row);

        }
        phones.close();// close cursor

        listView_contactos.setAdapter(new CustomArrayAdapter(this, rows));
        //display contact numbers in the list
    }
}



