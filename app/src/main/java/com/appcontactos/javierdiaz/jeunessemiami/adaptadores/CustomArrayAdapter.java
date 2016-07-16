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
import com.appcontactos.javierdiaz.jeunessemiami.modelos.RowContactsModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Javier Diaz on 31/05/2016.
 */
public class CustomArrayAdapter extends ArrayAdapter<RowContactsModel> {

    private LayoutInflater layoutInflater;
    private final ArrayList<RowContactsModel> list;

    public CustomArrayAdapter(Context context, ArrayList<RowContactsModel> objects)
    {
        super(context, 0, objects);
        layoutInflater = LayoutInflater.from(context);
        list = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // holder pattern
        Holder holder = null;
        if (convertView == null)
        {
            holder = new Holder();

            convertView = layoutInflater.inflate(R.layout.listview_row, null);
            holder.setTextViewTitle((TextView) convertView.findViewById(R.id.textViewTitle));
            holder.setTextViewSubtitle((TextView) convertView.findViewById(R.id.textViewSubtitle));
            holder.setCheckBox((CheckBox) convertView.findViewById(R.id.checkBox));
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();
                    list.get(getPosition).setChecked(buttonView.isChecked());

                }
            });
            convertView.setTag(holder);
            convertView.setTag(holder);
            convertView.setTag(R.id.textViewTitle, holder.textViewTitle);
            convertView.setTag(R.id.checkBox, holder.checkBox);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }

        RowContactsModel row = getItem(position);
        holder.getTextViewTitle().setText(row.getName());
        holder.getTextViewSubtitle().setText(row.getMobile_number());


        holder.checkBox.setTag(position); // This line is important.
        holder.checkBox.setChecked(list.get(position).isChecked());
        return convertView;
    }

    static class Holder
    {
        TextView textViewTitle;
        TextView textViewSubtitle;
        CheckBox checkBox;

        public TextView getTextViewTitle()
        {
            return textViewTitle;
        }

        public void setTextViewTitle(TextView textViewTitle)
        {
            this.textViewTitle = textViewTitle;
        }

        public TextView getTextViewSubtitle()
        {
            return textViewSubtitle;
        }

        public void setTextViewSubtitle(TextView textViewSubtitle)
        {
            this.textViewSubtitle = textViewSubtitle;
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
