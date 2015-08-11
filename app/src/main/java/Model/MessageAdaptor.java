package Model;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sm.arun.smartmsg.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by Arun on 08-10-2015.
 */
public class MessageAdaptor extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList<String> mData = new ArrayList<String>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private LayoutInflater mInflater;

    private final List<Message> chatMessages;
    private Activity context;

    public MessageAdaptor(Activity context, List<Message> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public Message getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        ViewHolder holder = null;
        int rowType = getItemViewType(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.messagelist_template, null);
                    holder.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.listitem_seperator, null);
                    holder.seperator = (TextView) convertView.findViewById(R.id.seperator);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.seperator.setText(mData.get(position));

        return convertView;



//        boolean myMsg = Message.getIsme() ;//Just a dummy check
//        //to simulate whether it me or other sender
////        setAlignment(holder, myMsg);
//        holder.txtMessage.setText(chatMessage.getMessage());
//        holder.txtInfo.setText(chatMessage.getDate());
//
//        return convertView;
    }

    public void add(Message message) {
        chatMessages.add(message);
    }

    public void add(List<Message> messages) {
        chatMessages.addAll(messages);
    }

//    private void setAlignment(ViewHolder holder, boolean isMe) {
//        if (!isMe) {
////            holder.contentWithBG.setBackgroundResource(R.drawable.in_message_bg);
//
//            LinearLayout.LayoutParams layoutParams =
//                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
//            layoutParams.gravity = Gravity.RIGHT;
//            holder.contentWithBG.setLayoutParams(layoutParams);
//
//            RelativeLayout.LayoutParams lp =
//                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
//            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
//            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            holder.content.setLayoutParams(lp);
//            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
//            layoutParams.gravity = Gravity.RIGHT;
//            holder.txtMessage.setLayoutParams(layoutParams);
//
//            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
//            layoutParams.gravity = Gravity.RIGHT;
//            holder.txtInfo.setLayoutParams(layoutParams);
//        } else {
////            holder.contentWithBG.setBackgroundResource(R.drawable.out_message_bg);
//
//            LinearLayout.LayoutParams layoutParams =
//                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
//            layoutParams.gravity = Gravity.LEFT;
//            holder.contentWithBG.setLayoutParams(layoutParams);
//
//            RelativeLayout.LayoutParams lp =
//                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
//            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
//            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//            holder.content.setLayoutParams(lp);
//            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
//            layoutParams.gravity = Gravity.LEFT;
//            holder.txtMessage.setLayoutParams(layoutParams);
//
//            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
//            layoutParams.gravity = Gravity.LEFT;
//            holder.txtInfo.setLayoutParams(layoutParams);
//        }
//    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
        return holder;
    }

    private static class ViewHolder {
        public TextView txtMessage;
        public TextView seperator;
        public TextView txtInfo;
        public LinearLayout content;
        public LinearLayout contentWithBG;
    }
}