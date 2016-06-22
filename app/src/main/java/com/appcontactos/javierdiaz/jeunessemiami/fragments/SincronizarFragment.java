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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appcontactos.javierdiaz.jeunessemiami.R;
import com.appcontactos.javierdiaz.jeunessemiami.activities.LoginActivity;
import com.appcontactos.javierdiaz.jeunessemiami.activities.NevigationActivity;
import com.appcontactos.javierdiaz.jeunessemiami.adaptadores.CustomArrayAdapter;
import com.appcontactos.javierdiaz.jeunessemiami.modelos.RowContactsModel;
import com.appcontactos.javierdiaz.jeunessemiami.util.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class SincronizarFragment extends Fragment implements View.OnClickListener {


    private String phoneNumber;
    private ListView listView_contactos;
    private HashMap<Double, RowContactsModel> listItems;
    private Button btnSincronizar;
    private CustomArrayAdapter customArrayAdapter;
    private int pendingRequests = 0;
    private CheckBox checkBoxTodos;
    /**
     * activity progress dialog
     */
    protected ProgressDialog mProgressDialog;


    public SincronizarFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sincronizar, container, false);
        listView_contactos = (ListView) view.findViewById(R.id.listview_contactos);
        btnSincronizar = (Button) view.findViewById(R.id.btn_sincronizar);
        btnSincronizar.setOnClickListener(this);
        listItems = new HashMap<>();
        checkBoxTodos = (CheckBox) view.findViewById(R.id.checkBox_todos);
        mProgressDialog = new ProgressDialog(getContext());
        showProgressDialog("Cargando contactos..");

        if((NevigationActivity.rows == null) || (NevigationActivity.rows.size() < 1) ){
            getNumber(getActivity().getContentResolver());
        }else{
            listView_contactos.setItemsCanFocus(true);
            customArrayAdapter = new CustomArrayAdapter(getActivity(), NevigationActivity.rows);
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
        NevigationActivity.rows.addAll(listItems.values());
        Collections.sort(NevigationActivity.rows, new Comparator<RowContactsModel>() {
            public int compare(RowContactsModel v1, RowContactsModel v2) {
                return v1.getName().compareToIgnoreCase(v2.getName());
            }
        });

        listView_contactos.setItemsCanFocus(true);
        customArrayAdapter = new CustomArrayAdapter(getActivity(), NevigationActivity.rows);
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

        for (int i = 0; i < NevigationActivity.rows.size(); i++) {
            NevigationActivity.rows.get(i).setChecked(true);
        }
        customArrayAdapter.notifyDataSetChanged();


    }

    private void unSelectAll() {

        for (int i = 0; i < NevigationActivity.rows.size(); i++) {
            NevigationActivity.rows.get(i).setChecked(false);
        }
        customArrayAdapter.notifyDataSetChanged();
    }
    /*
    Método para sincrinizar los contactos en bd remota. No se sincroniza por lote.
     */

    public void synContacto(Context context, final String userid, final String nombre, final String apellido, final String correo, final String tlf) {
        String url = Config.url_sincronizar;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("INSERTAR CONTACTO", response);
                pendingRequests--;
                if (pendingRequests == 0) {
                    dismissProgressDialog();
                    Toast.makeText(getActivity(), "Usuarios sincronizados satisfactoriamente", Toast.LENGTH_LONG).show();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pendingRequests--;
                if (pendingRequests == 0) {
                    dismissProgressDialog();
                    Toast.makeText(getActivity(), "Usuarios sincronizados con errores", Toast.LENGTH_LONG).show();

                }
                Log.d("ERROR AL INSERTAR", error.toString());
                //Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("method", "addContactsLot");
                params.put("param1[]", LoginActivity.userid);
                params.put("param2[]", nombre);
                if (apellido != null) {
                    params.put("param3[]", apellido);
                } else {
                    params.put("param3[]", "apellido");
                }
                if (correo != null) {
                    params.put("param4[]", correo);
                } else {
                    params.put("param4[]", "email" + userid + "@email.com");
                }
                params.put("param5[]", tlf);

                return params;
            }


        };
        sr.setRetryPolicy(new DefaultRetryPolicy(12000 * 27000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(sr);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sincronizar:
                int count = 0;
                for (int i = 0; i < NevigationActivity.rows.size(); i++) {
                    if (NevigationActivity.rows.get(i).isChecked()) {
                        count++;
                    }
                }

                if (count > 0) {
                    showProgressDialog(getString(R.string.sincronizar_contactos));
                    for (int i = 0; i < NevigationActivity.rows.size(); i++) {
                        if (NevigationActivity.rows.get(i).isChecked()) {
                            synContacto(getActivity(), NevigationActivity.rows.get(i).getUserid(), NevigationActivity.rows.get(i).getName(),
                                    NevigationActivity.rows.get(i).getSurname(), NevigationActivity.rows.get(i).getEmail(), NevigationActivity.rows.get(i).getMobile_number());
                            pendingRequests++;
                        }
                    }

                } else {
                    Toast.makeText(getActivity(), "Debe seleccionar al menos un(1) contacto", Toast.LENGTH_LONG).show();
                }
                break;

        }
    }
}
