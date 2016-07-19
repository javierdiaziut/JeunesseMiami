package com.appcontactos.javierdiaz.jeunessemiami.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.appcontactos.javierdiaz.jeunessemiami.R;
import com.appcontactos.javierdiaz.jeunessemiami.modelos.Mensajes;
import java.util.ArrayList;

/**
 * Created by Javier Diaz on 15/07/2016.
 */
public class CustomPlantillasAdapter extends ArrayAdapter<Mensajes> {

    private LayoutInflater layoutInflater;
    private final ArrayList<Mensajes> list;
    Integer selected_position = -1;

    public CustomPlantillasAdapter(Context context, ArrayList<Mensajes> objects) {

        super(context, 0, objects);
        layoutInflater = LayoutInflater.from(context);
        list = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // holder pattern
        Holder holder = null;
        if (convertView == null)
        {
            holder = new Holder();

            convertView = layoutInflater.inflate(R.layout.plantillas_row, null);
            holder.setTextViewType((TextView) convertView.findViewById(R.id.textViewType));
            holder.setTextViewTMensaje((TextView) convertView.findViewById(R.id.textViewMensaje));
            holder.setCheckBox((CheckBox) convertView.findViewById(R.id.checkBoxPlantillas));
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        selected_position =  position;
                        int getPosition = (Integer) buttonView.getTag();
                        list.get(getPosition).setChecked(buttonView.isChecked());
                    }
                    else{
                        selected_position = -1;
                        int getPosition = (Integer) buttonView.getTag();
                        list.get(getPosition).setChecked(buttonView.isChecked());
                    }
                    notifyDataSetChanged();


                }
            });

            convertView.setTag(holder);
            convertView.setTag(R.id.textViewTitle, holder.textViewType);
            convertView.setTag(R.id.checkBoxPlantillas, holder.checkBox);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }

        if(position==selected_position)
        {
            holder.checkBox.setChecked(true);
        }
        else
        {
            holder.checkBox.setChecked(false);
        }

        Mensajes row =  getItem(position);

        if (row.getImagen()!= null && row.getImagen().length() > 0){
            holder.getTextViewType().setText("MMS");
        }else{
            holder.getTextViewType().setText("SMS");
        }
        holder.getTextViewMensaje().setText(row.getDescripcion());


        holder.checkBox.setTag(position); // This line is important.
        return convertView;
    }

    static class Holder
    {
        TextView textViewType;
        TextView textViewMensaje;
        CheckBox checkBox;

        public TextView getTextViewType()
        {
            return textViewType;
        }

        public void setTextViewType(TextView textViewTipe)
        {
            this.textViewType = textViewTipe;
        }

        public TextView getTextViewMensaje()
        {
            return textViewMensaje;
        }

        public void setTextViewTMensaje(TextView textViewMensaje)
        {
            this.textViewMensaje = textViewMensaje;
        }
        public CheckBox getCheckBox()
        {
            return checkBox;
        }
        public void setCheckBox(CheckBox checkBox)
        {
            this.checkBox = checkBox;
        }


    }
}
