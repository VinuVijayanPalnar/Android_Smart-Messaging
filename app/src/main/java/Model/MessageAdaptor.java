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
public class MessageAdaptor extends ArrayAdapter<MessageListModel> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList<String> mData = new ArrayList<String>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();


    private ArrayList<MessageListModel> allMessages;
   private boolean mNotifyOnChange = true;
    private LayoutInflater mInflater;

//    private final List<Message> chatMessages;
    private Context context;

    public MessageAdaptor(Context context, ArrayList<MessageListModel> chatMessages) {
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
    public MessageListModel getItem(int position) {
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
    public int getPosition(MessageListModel item) {
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
//        holder.name.setText(allPersons.get(position).getName());
//        holder.description.setText(allPersons.get(position).getDescription());
//        holder.pos = position;
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


//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//
//
//        ViewHolder holder = null;
//        int rowType = getItemViewType(position);
//        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        if (convertView == null) {
//            holder = new ViewHolder();
//            switch (rowType) {
//                case TYPE_ITEM:
//                    convertView = mInflater.inflate(R.layout.messagelist_template, null);
//                    holder.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
//                    break;
//                case TYPE_SEPARATOR:
//                    convertView = mInflater.inflate(R.layout.listitem_seperator, null);
//                    holder.seperator = (TextView) convertView.findViewById(R.id.seperator);
//                    break;
//            }
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//        holder.seperator.setText(mData.get(position));
//
//        return convertView;
//
//
//
////        boolean myMsg = Message.getIsme() ;//Just a dummy check
////        //to simulate whether it me or other sender
//////        setAlignment(holder, myMsg);
////        holder.txtMessage.setText(chatMessage.getMessage());
////        holder.txtInfo.setText(chatMessage.getDate());
////
////        return convertView;
//    }

//    public void add(Message message) {
//        chatMessages.add(message);
//    }
//
//    public void add(List<Message> messages) {
//        chatMessages.addAll(messages);
//    }

//    private ViewHolder createViewHolder(View v) {
//        ViewHolder holder = new ViewHolder();
//        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
//        holder.content = (LinearLayout) v.findViewById(R.id.content);
//        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
//        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
//        return holder;
//    }

    private static class ViewHolder {
        public ImageView ImgView;
        public TextView txtMessage;
        public TextView seperator;
        public TextView txtInfo;
        public LinearLayout content;
        public LinearLayout contentWithBG;
    }
}