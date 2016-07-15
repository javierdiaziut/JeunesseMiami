package com.appcontactos.javierdiaz.jeunessemiami.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appcontactos.javierdiaz.jeunessemiami.R;

public class PlantillasFragment extends Fragment {
    /**
     * activity progress dialog
     */
    protected ProgressDialog mProgressDialog;


    public PlantillasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_plantillas, container, false);
        mProgressDialog = new ProgressDialog(getContext());
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
