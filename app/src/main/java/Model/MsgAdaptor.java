package Model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.sm.arun.smartmsg.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Arun on 08-25-2015.
 */
public class MsgAdaptor extends ArrayAdapter<Message> {
    String date=null;
    int Msgtype;
    String SesctionHdr=null;
    ViewHolder viewHolder = null;
    ArrayList<Message> MsgList;
    Context ctxt;
    private boolean mNotifyOnChange = true;
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
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mNotifyOnChange = true;
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }


    //    @Override
//    public int getViewTypeCount() {
//
//        return getCount();
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//
//        return position;
//    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        Message msg=getItem(position);
        Msgtype=msg.getType();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (convertView == null) {

            viewHolder = new ViewHolder();
            switch(Msgtype)
            {
                case 1:
                    convertView = inflater.inflate(R.layout.listitem_seperator, parent, false);
                    viewHolder.seperator = (TextView) convertView.findViewById(R.id.seperator);
//                    convertView.setTag(viewHolder);
                    break;
                case 2:
                    convertView = inflater.inflate(R.layout.messagelist_template, parent, false);
                    viewHolder.txtInfo = (TextView) convertView.findViewById(R.id.txtInfo);
                    viewHolder.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
                    viewHolder.ImgView=(ImageView)convertView.findViewById(R.id.adminimage);
//                    convertView.setTag(viewHolder);
                    break;
            }
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();


//        viewHolder.ImgView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });
        // Populate the data into the template view using the data object
//        viewHolder.txtInfo.setText(msg.getAdminName());
//        viewHolder.txtMessage.setText(msg.getMessage());
        if(Msgtype==1) {
            if (viewHolder.seperator != null )
                viewHolder.seperator.setText(msg.getMessage());
            else
            {
                convertView = inflater.inflate(R.layout.listitem_seperator, parent, false);
                viewHolder.seperator = (TextView) convertView.findViewById(R.id.seperator);
                convertView.setTag(viewHolder);
                viewHolder.seperator.setText(msg.getMessage());
            }
        }else
        {
            if((viewHolder.txtInfo != null )&&(viewHolder.txtMessage != null ))
            {
                viewHolder.txtInfo.setText(msg.getAdminName());
                viewHolder.txtMessage.setText(msg.getMessage());
                viewHolder.ImgView.setImageBitmap(msg.getAdminImage());}

            else
            {
                convertView = inflater.inflate(R.layout.messagelist_template, parent, false);
                viewHolder.txtInfo = (TextView) convertView.findViewById(R.id.txtInfo);
                viewHolder.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
                viewHolder.ImgView=(ImageView)convertView.findViewById(R.id.adminimage);
                convertView.setTag(viewHolder);
                viewHolder.txtInfo.setText(msg.getAdminName());
                viewHolder.txtMessage.setText(msg.getMessage());
                viewHolder.ImgView.setImageBitmap(msg.getAdminImage());
            }
        }
        // Return the completed view to render on screen
        return convertView;


    }


    public int compareDate( String dte1,String dte2) {
        try {
//            "yyyy-MM-dd'T'HH:mm:ss'Z'"
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);

            System.out.println("Date :" + formatter.format(date));
            Date date1 =formatter.parse(formatter.format(sdf.parse(dte1)));
            Date date2 = formatter.parse(formatter.format(sdf.parse(dte2)));

            if(date1.compareTo(date2)>0){
                Log.v("app", "Date1 is after Date2");
                return 3;
            }else if(date1.compareTo(date2)<0){
                Log.v("app","Date1 is before Date2");
                return 2;
            }else if(date1.compareTo(date2)==0){
                Log.v("app","Date1 is equal to Date2");
                return 1;
            }
        } catch(Exception e) {Log.v("DateCompare:","Exception "+e.getMessage()+" caught while comparing Date1 and Date2"); }
        return 0;
    }
}