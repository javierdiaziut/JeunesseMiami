package com.appcontactos.javierdiaz.jeunessemiami.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appcontactos.javierdiaz.jeunessemiami.R;
import com.appcontactos.javierdiaz.jeunessemiami.activities.NavigationActivity;
import com.appcontactos.javierdiaz.jeunessemiami.adaptadores.CustomPlantillasAdapter;
import com.appcontactos.javierdiaz.jeunessemiami.util.Util;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class PlantillasFragment extends Fragment {
    /**
     * activity progress dialog
     */
    protected ProgressDialog mProgressDialog;
    private CustomPlantillasAdapter customPlantillasAdapter;
    private ListView listViewPlantillas;
    private Button btnPreview, btnSend;
    private TextView txtPreviewmsg, txtPreviewlinkvideo;
    private ImageView imgPreviewimg;
    public static final int PERMISSIONS_REQUEST = 10;
    private static final int REQUEST_A_PICTURE = 100;
    private Uri actualURIimg;
    private String actualPATHimg = "";
    private RelativeLayout relativePreviewMMS, relativePreviewSMS;
    private EditText editTextsms;
    private boolean isSMS = Boolean.FALSE;
    private ArrayList<String> numeros = new ArrayList<>();


    public PlantillasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_plantillas, container, false);
        mProgressDialog = new ProgressDialog(getContext());
        listViewPlantillas = (ListView) view.findViewById(R.id.listview_plantillas);
        btnPreview = (Button) view.findViewById(R.id.btn_preview_msg);
        btnSend = (Button) view.findViewById(R.id.btn_send_msg);
        txtPreviewmsg = (TextView) view.findViewById(R.id.txtview_preview_msg);
        txtPreviewlinkvideo = (TextView) view.findViewById(R.id.txtview_preview_vid);
        imgPreviewimg = (ImageView) view.findViewById(R.id.imgView_preview_img);
        relativePreviewSMS = (RelativeLayout) view.findViewById(R.id.relative_preview_sms);
        relativePreviewMMS = (RelativeLayout) view.findViewById(R.id.relative_preview_mms);
        editTextsms = (EditText) view.findViewById(R.id.edittext_preview_msg);


        if (NavigationActivity.plantillasMensajes != null && NavigationActivity.plantillasMensajes.size() > 0) {
            listViewPlantillas.setItemsCanFocus(true);
            customPlantillasAdapter = new CustomPlantillasAdapter(getContext(), NavigationActivity.plantillasMensajes);
            listViewPlantillas.setAdapter(customPlantillasAdapter);
        }


        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < NavigationActivity.plantillasMensajes.size(); i++) {
                    if (NavigationActivity.plantillasMensajes.get(i).isChecked() &&
                            (NavigationActivity.plantillasMensajes.get(i).getImagen() != null && !NavigationActivity.plantillasMensajes.get(i).getImagen().isEmpty())) {
                        txtPreviewmsg.setText(NavigationActivity.plantillasMensajes.get(i).getDescripcion());
                        txtPreviewlinkvideo.setText(NavigationActivity.plantillasMensajes.get(i).getLink_video());
                        relativePreviewSMS.setVisibility(View.GONE);
                        relativePreviewMMS.setVisibility(View.VISIBLE);
                        isSMS = Boolean.FALSE;

                        break;
                    } else {
                        if (NavigationActivity.plantillasMensajes.get(i).isChecked()) {
                            editTextsms.setText(NavigationActivity.plantillasMensajes.get(i).getDescripcion());
                            relativePreviewSMS.setVisibility(View.VISIBLE);
                            relativePreviewMMS.setVisibility(View.GONE);
                            isSMS = Boolean.TRUE;
                            break;
                        }

                    }
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = "";
                String sms = "";

                for (int i = 0; i < NavigationActivity.plantillasMensajes.size(); i++) {
                    if (NavigationActivity.plantillasMensajes.get(i).isChecked()) {
                        mensaje = NavigationActivity.plantillasMensajes.get(i).getDescripcion() + " Video_link: " + NavigationActivity.plantillasMensajes.get(i).getLink_video();
                        sms = editTextsms.getText().toString();
                        break;
                    }
                }
                if (mensaje.isEmpty()) {
                    Toast.makeText(getContext(), "Debe seleccionar una plantilla de mensaje", Toast.LENGTH_LONG).show();
                } else {

                    if (isSMS) {
                        sendSMS(sms);
                    } else {
                        sendMMS(mensaje);
                    }
                }

            }
        });

        imgPreviewimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permissionCamCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
                    int permissionGalCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permissionCamCheck != PackageManager.PERMISSION_GRANTED || permissionGalCheck != PackageManager.PERMISSION_GRANTED) {
                        List<String> permissionsNeeded = new ArrayList<String>();
                        permissionsNeeded.add(Manifest.permission.CAMERA);
                        permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                        requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                                PERMISSIONS_REQUEST);
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

    private void sendMMS(String mensaje) {
        String numeros = "";
        for (int i = 0; i < NavigationActivity.rows.size(); i++) {
            if (NavigationActivity.rows.get(i).isChecked()) {
                numeros += NavigationActivity.rows.get(i).getMobile_number() + ";";
            }
        }
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra("address", numeros);
        i.putExtra("sms_body", mensaje);
        i.putExtra(Intent.EXTRA_STREAM, actualURIimg);
        i.setType("image/png");
        startActivity(i);

    }

    private void sendSMS(String msg) {
        for (int i = 0; i < NavigationActivity.rows.size(); i++) {
            if (NavigationActivity.rows.get(i).isChecked()) {
                numeros.add(NavigationActivity.rows.get(i).getMobile_number());
            }
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            for (int i = 0; i < numeros.size(); i++) {
                smsManager.sendTextMessage(numeros.get(i), null, msg, null, null);
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
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            actualPATHimg = picturePath;
            cursor.close();

            imgPreviewimg.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @Override
    public void onDetach() {

        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        getFragmentManager().popBackStackImmediate();

    }
}
