package Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sm.arun.smartmsg.R;

import java.util.ArrayList;

/**
 * Created by Arun on 08-25-2015.
 */
public class MsgAdaptor extends ArrayAdapter<Message> {

        // View lookup cache
        private static class ViewHolder {
            ImageView ImgView;
            TextView txtMessage;
            TextView seperator;
            TextView txtInfo;
            LinearLayout content;
            LinearLayout contentWithBG;
        }

        public MsgAdaptor(Context context, ArrayList<Message> msg) {
            super(context, R.layout.messagelist_template, msg);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Message msg=getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.messagelist_template, parent, false);
                viewHolder.txtInfo = (TextView) convertView.findViewById(R.id.txtInfo);
                viewHolder.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data into the template view using the data object
            viewHolder.txtInfo.setText(msg.getDate());
            viewHolder.txtMessage.setText(msg.getMessage());
            // Return the completed view to render on screen
            return convertView;
        }
}

