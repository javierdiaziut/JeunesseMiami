package com.appcontactos.javierdiaz.jeunessemiami.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appcontactos.javierdiaz.jeunessemiami.R;
import com.appcontactos.javierdiaz.jeunessemiami.activities.NavigationActivity;
import com.appcontactos.javierdiaz.jeunessemiami.modelos.RowContactsModel;

import java.util.ArrayList;
import java.util.List;


public class ConfirmarSmsFragment extends Fragment implements View.OnClickListener {

    private ArrayList<RowContactsModel> seleccionados = new ArrayList<>();
    private TextView txtview_num_seleccionados;
    private Button btnConfirmar;
    private EditText editTextmsg;
    private ArrayList<String> numeros = new ArrayList<>();

    public ConfirmarSmsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirmar_sms, container, false);
        txtview_num_seleccionados = (TextView) view.findViewById(R.id.textview_num_selected);
        btnConfirmar = (Button) view.findViewById(R.id.btn_send_sms);
        btnConfirmar.setOnClickListener(this);
        editTextmsg = (EditText) view.findViewById(R.id.edittext_sms);
        mostrarSelecionados(txtview_num_seleccionados);
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

    private void mostrarSelecionados(TextView txtarea) {
        for (int i = 0; i < NavigationActivity.rows.size(); i++) {
            if (NavigationActivity.rows.get(i).isChecked()) {
                txtarea.append(NavigationActivity.rows.get(i).getName() + " " + NavigationActivity.rows.get(i).getMobile_number() + "\n");
                numeros.add(NavigationActivity.rows.get(i).getMobile_number());
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionSendSms = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS);

            if ((permissionSendSms != PackageManager.PERMISSION_GRANTED)) {
                List<String> permissionsNeeded = new ArrayList<String>();
                permissionsNeeded.add(Manifest.permission.READ_CONTACTS);
                permissionsNeeded.add(Manifest.permission.INTERNET);

                requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                        2);
            } else {
                sendSMS(numeros, editTextmsg.getText().toString());
            }
        } else {
            sendSMS(numeros, editTextmsg.getText().toString());
        }
    }

    public void sendSMS(ArrayList<String> phoneNo, String msg) {
        try {

            SmsManager smsManager = SmsManager.getDefault();
            for (int i = 0; i < phoneNo.size(); i++) {
                smsManager.sendTextMessage(phoneNo.get(i), null, msg, null, null);
            }

            Toast.makeText(getActivity(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

}
