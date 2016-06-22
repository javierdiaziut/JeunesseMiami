package com.appcontactos.javierdiaz.jeunessemiami.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appcontactos.javierdiaz.jeunessemiami.R;
import com.appcontactos.javierdiaz.jeunessemiami.activities.NevigationActivity;
import com.appcontactos.javierdiaz.jeunessemiami.modelos.RowContactsModel;

import java.util.ArrayList;


public class ConfirmarSmsFragment extends Fragment {

    private ArrayList<RowContactsModel> seleccionados = new ArrayList<>();
    private TextView txtview_num_seleccionados;

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

    private void mostrarSelecionados(TextView txtarea){
        for(int i =0; i < NevigationActivity.rows.size(); i++){
            if(NevigationActivity.rows.get(i).isChecked()){
                txtarea.append(NevigationActivity.rows.get(i).getName()+" " +NevigationActivity.rows.get(i).getMobile_number()+"\n");

            }
        }
    }

}
