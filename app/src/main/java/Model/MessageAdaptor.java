package Model;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sm.arun.smartmsg.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by Arun on 08-10-2015.
 */
public class MessageAdaptor extends ArrayAdapter<Message> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList<String> mData = new ArrayList<String>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();


    private ArrayList<Message> allMessages;
   private boolean mNotifyOnChange = true;
    private LayoutInflater mInflater;

//    private final List<Message> chatMessages;
    private Context context;

    public MessageAdaptor(Context context, ArrayList<Message> chatMessages) {
        super(context,R.layout.messagelist_template);
        this.context = context;
        this.allMessages = chatMessages;
        this.mInflater = LayoutInflater.from(context);
//        mInflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (allMessages != null) {
            return allMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public Message getItem(int position) {
        if (allMessages != null) {
            return allMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getPosition(Message item) {
        return allMessages .indexOf(item);
    }

    @Override
    public int getViewTypeCount() {
        return 1; //Number of types + 1 !!!!!!!!
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        Message msg=getItem(position);
        int type = getItemViewType(position);
        if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
                case 1:
                    convertView = mInflater.inflate(R.layout.messagelist_template,parent, false);
                    holder.txtInfo=(TextView)convertView.findViewById(R.id.txtInfo);
                    holder.txtMessage=(TextView)convertView.findViewById(R.id.txtMessage);
                    holder.ImgView=(ImageView)convertView.findViewById(R.id.adminimage);

//                    holder.name = (TextView) convertView.findViewById(R.id.textview_name);
//                    holder.description = (TextView) convertView.findViewById(R.id.textview_description);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtInfo.setText(allMessages.get(position).getDate());
        holder.txtMessage.setText(allMessages.get(position).getMessage());
//        holder.ImgView .setImageBitmap(allMessages.get(position).getAdminImage());
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mNotifyOnChange = true;
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }


    private static class ViewHolder {
        public ImageView ImgView;
        public TextView txtMessage;
        public TextView seperator;
        public TextView txtInfo;
        public LinearLayout content;
        public LinearLayout contentWithBG;
    }
}