package uk.ac.abertay.cmp309.assessmentempty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RemindersAdapter extends ArrayAdapter<String[]> {

    public RemindersAdapter(Context context, ArrayList<String[]> reminders){
        super(context, 0, reminders);
    }

    //Adapter for populating the lists and making use of the single_item layout to do this
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        //Setting up array to put data into
        String[] reminders = getItem(position);

        //Setting convert view to the single_item layout
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_item, parent, false);
        }

        //Setting up the text views in single_item layout
        TextView display_info = convertView.findViewById(R.id.item_info);
        TextView display_date = convertView.findViewById(R.id.item_date);
        TextView display_time = convertView.findViewById(R.id.item_time);
        TextView display_location = convertView.findViewById(R.id.item_location);
        TextView display_id = convertView.findViewById(R.id.item_id);
        TextView display_type = convertView.findViewById(R.id.item_type);
        TextView display_notification = convertView.findViewById(R.id.item_notification);

        //Putting the data into the text views of single_item
        display_id.setText(reminders[0]);
        display_type.setText(reminders[1]);
        display_info.setText(reminders[2]);
        display_date.setText(reminders[3]);
        display_time.setText(reminders[4]);
        display_location.setText(reminders[5]);
        display_notification.setText(reminders[6]);


        //return this instance of the convert view
        return convertView;
    }

}
