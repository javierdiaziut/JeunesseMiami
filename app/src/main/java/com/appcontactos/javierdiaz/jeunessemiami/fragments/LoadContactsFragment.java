package com.appcontactos.javierdiaz.jeunessemiami.fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.appcontactos.javierdiaz.jeunessemiami.R;
import com.appcontactos.javierdiaz.jeunessemiami.activities.NavigationActivity;
import com.appcontactos.javierdiaz.jeunessemiami.adaptadores.CustomArrayAdapter;
import com.appcontactos.javierdiaz.jeunessemiami.modelos.RowContactsModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class LoadContactsFragment extends Fragment implements View.OnClickListener {
    private String phoneNumber;
    private ListView listView_contactos;
    private HashMap<Double, RowContactsModel> listItems;
    private Button btnSincronizar;
    private CustomArrayAdapter customArrayAdapter;
    protected ProgressDialog mProgressDialog;
    private CheckBox checkBoxTodos;

    public LoadContactsFragment() {
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
        checkBoxTodos = (CheckBox) view.findViewById(R.id.checkBox_todos);
        btnSincronizar.setOnClickListener(this);
        listItems = new HashMap<>();

        mProgressDialog = new ProgressDialog(getContext());
        showProgressDialog("Cargando contactos..");
        if((NavigationActivity.rows == null) || (NavigationActivity.rows.size() < 1) ){
            getNumber(getActivity().getContentResolver());
        }else{
            listView_contactos.setItemsCanFocus(true);
            customArrayAdapter = new CustomArrayAdapter(getActivity(), NavigationActivity.rows);
            listView_contactos.setAdapter(customArrayAdapter);
        }
        dismissProgressDialog();

        checkBoxTodos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    selectAll();

                }else{
                    unSelectAll();

                }


            }
        });

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
        NavigationActivity.rows.addAll(listItems.values());
        Collections.sort(NavigationActivity.rows, new Comparator<RowContactsModel>() {
            public int compare(RowContactsModel v1, RowContactsModel v2) {
                return v1.getName().compareToIgnoreCase(v2.getName());
            }
        });

        listView_contactos.setItemsCanFocus(true);
        customArrayAdapter = new CustomArrayAdapter(getActivity(), NavigationActivity.rows);
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

        for (int i = 0; i < NavigationActivity.rows.size(); i++) {
            NavigationActivity.rows.get(i).setChecked(true);
        }
        customArrayAdapter.notifyDataSetChanged();


    }

    private void unSelectAll() {

        for (int i = 0; i < NavigationActivity.rows.size(); i++) {
            NavigationActivity.rows.get(i).setChecked(false);
        }
        customArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        int count = 0;
        for (int i = 0; i < NavigationActivity.rows.size(); i++) {
            if (NavigationActivity.rows.get(i).isChecked()) {
                count++;
            }
        }

        if (count >= 0) {
            fragmentManager = getActivity().getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();

            switch (NavigationActivity.NEXT_FRAGMENT){
                case NavigationActivity.FRAGMENT_MSG:
                    ConfirmarSmsFragment confirmarSmsFragment = new ConfirmarSmsFragment();
                    fragmentTransaction.replace(R.id.fragment, confirmarSmsFragment);
                    fragmentTransaction.addToBackStack("mensajes");
                    fragmentTransaction.commit();
                    break;
                case NavigationActivity.FRAGMENT_PLANTILLAS:
                    PlantillasFragment plantillasFragment = new PlantillasFragment();
                    fragmentTransaction.replace(R.id.fragment, plantillasFragment);
                    fragmentTransaction.addToBackStack("plantilas");
                    fragmentTransaction.commit();
                    break;
            }

        }else{
            Toast.makeText(getActivity(), "Debe seleccionar al menos un(1) contacto", Toast.LENGTH_LONG).show();
        }
    }
}
