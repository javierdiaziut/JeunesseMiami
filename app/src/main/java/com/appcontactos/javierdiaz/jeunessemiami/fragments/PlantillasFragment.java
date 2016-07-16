package com.appcontactos.javierdiaz.jeunessemiami.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appcontactos.javierdiaz.jeunessemiami.R;
import com.appcontactos.javierdiaz.jeunessemiami.activities.NavigationActivity;
import com.appcontactos.javierdiaz.jeunessemiami.adaptadores.CustomPlantillasAdapter;

public class PlantillasFragment extends Fragment {
    /**
     * activity progress dialog
     */
    protected ProgressDialog mProgressDialog;
    private CustomPlantillasAdapter customPlantillasAdapter;
    private ListView listViewPlantillas;
    private Button btnPreview;
    private TextView txtPreviewmsg, txtPreviewlinkvideo, txtPreviewimg;


    public PlantillasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_plantillas, container, false);
        mProgressDialog = new ProgressDialog(getContext());
        listViewPlantillas = (ListView) view.findViewById(R.id.listview_plantillas);
        btnPreview = (Button) view.findViewById(R.id.btn_preview_msg);
        txtPreviewmsg = (TextView) view.findViewById(R.id.txtview_preview_msg);
        txtPreviewlinkvideo = (TextView) view.findViewById(R.id.txtview_preview_vid);
        txtPreviewimg = (TextView) view.findViewById(R.id.textView_preview_img);


        if(NavigationActivity.plantillasMensajes != null && NavigationActivity.plantillasMensajes.size() > 0){
            listViewPlantillas.setItemsCanFocus(true);
            customPlantillasAdapter = new CustomPlantillasAdapter(getContext(), NavigationActivity.plantillasMensajes);
            listViewPlantillas.setAdapter(customPlantillasAdapter);
        }


        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i =0; i < NavigationActivity.plantillasMensajes.size(); i++){
                    if(NavigationActivity.plantillasMensajes.get(i).isChecked()){
                        txtPreviewmsg.setText(NavigationActivity.plantillasMensajes.get(i).getDescripcion());
                        txtPreviewimg.setText(NavigationActivity.plantillasMensajes.get(i).getImagen());
                        txtPreviewlinkvideo.setText(NavigationActivity.plantillasMensajes.get(i).getLink_video());

                        break;
                    }
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
}
