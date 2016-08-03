package com.appcontactos.javierdiaz.jeunessemiami.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private ImageView imageViewAdjunto;
    private Button btnUpload;
    public static Uri actualURIimg;
    private static String actualPATHimg = "";
    private boolean isSMS = Boolean.TRUE;
    private static List<String> numerosSeparated = new ArrayList<>();
    private static String mensaje = "";

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
        imageViewAdjunto = (ImageView) view.findViewById(R.id.imgView_upload_img);
        btnUpload = (Button) view.findViewById(R.id.btn_upload_img);
        mostrarSelecionados(txtview_num_seleccionados);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permissionCamCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
                    int permissionsdCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
                    int permissionGalCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permissionCamCheck != PackageManager.PERMISSION_GRANTED || permissionGalCheck != PackageManager.PERMISSION_GRANTED
                            ||permissionsdCheck != PackageManager.PERMISSION_GRANTED ) {
                        List<String> permissionsNeeded = new ArrayList<String>();
                        permissionsNeeded.add(Manifest.permission.CAMERA);
                        permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);

                        requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                                11);
                    } else {
                        Intent i = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        startActivityForResult(i, 200);
                    }
                } else {
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, 200);
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
                mensaje = editTextmsg.getText().toString();
                if(isSMS){
                    sendSMS(numeros, editTextmsg.getText().toString());
                }else{
                    checkNumbers();
                    if (numerosSeparated.size() > 0) {
                        sendMMS(mensaje, numerosSeparated);
                    }
                }

            }
        } else {

            if(isSMS){
                sendSMS(numeros, editTextmsg.getText().toString());
            }else{
                checkNumbers();
                if (numerosSeparated.size() > 0) {
                    sendMMS(mensaje, numerosSeparated);
                }
            }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != data) {
            Uri selectedImage = data.getData();
            actualURIimg = selectedImage;
            isSMS = Boolean.FALSE;
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            actualPATHimg = picturePath;
            cursor.close();

            imageViewAdjunto.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            imageViewAdjunto.setVisibility(View.VISIBLE);

        }
        if (requestCode == 2) {
            if (numerosSeparated.size() > 0) {
                sendMMS(mensaje, numerosSeparated);
            }
        }
    }

    private void checkNumbers() {

        for (int i = 0; i < NavigationActivity.rows.size(); i++) {
            if (NavigationActivity.rows.get(i).isChecked()) {
                numerosSeparated.add(NavigationActivity.rows.get(i).getMobile_number());
            }
        }
    }

    private void sendMMS(String mensaje, List<String> destino) {


        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra("address", destino.get(0));
        i.putExtra("sms_body", mensaje);
        i.putExtra(Intent.EXTRA_STREAM, actualURIimg);
        i.setType("image/png");
        destino.remove(0);
        startActivity(i);
        startActivityForResult(i, 2);

    }


}
