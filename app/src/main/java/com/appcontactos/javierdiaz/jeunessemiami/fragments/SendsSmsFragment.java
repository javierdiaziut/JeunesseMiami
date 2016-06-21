package com.appcontactos.javierdiaz.jeunessemiami.fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.appcontactos.javierdiaz.jeunessemiami.R;
import com.appcontactos.javierdiaz.jeunessemiami.adaptadores.CustomArrayAdapter;
import com.appcontactos.javierdiaz.jeunessemiami.modelos.RowContactsModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class SendsSmsFragment extends Fragment implements View.OnClickListener{
    private String phoneNumber;
    private ListView listView_contactos;
    private ArrayList<RowContactsModel> rows = new ArrayList<>();
    private HashMap<Double, RowContactsModel> listItems;
    private Button btnSincronizar;
    private CustomArrayAdapter customArrayAdapter;
    protected ProgressDialog mProgressDialog;

    public SendsSmsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sends_sms, container, false);

        listView_contactos = (ListView) view.findViewById(R.id.listview_contactos);
        btnSincronizar = (Button) view.findViewById(R.id.btn_sincronizar);
        btnSincronizar.setOnClickListener(this);
        listItems = new HashMap<>();

        mProgressDialog = new ProgressDialog(getContext());
        showProgressDialog("Cargando contactos..");
        getNumber(getActivity().getContentResolver());
        dismissProgressDialog();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
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
            Cursor emails = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + user_id, null, null);
            while (emails.moveToNext()) {
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
        customArrayAdapter = new CustomArrayAdapter(getActivity(), rows);
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

    private void selectAll() {

        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).setChecked(true);
        }
        customArrayAdapter.notifyDataSetChanged();


    }

    private void unSelectAll() {

        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).setChecked(false);
        }
        customArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }
}
