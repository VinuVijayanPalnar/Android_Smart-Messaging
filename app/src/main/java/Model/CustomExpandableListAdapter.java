package Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sm.arun.smartmsg.R;

import java.security.acl.Group;
import java.util.ArrayList;

/**
 * Created by Arun on 09-04-2015.
 */
public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<MessageListModel> mGroups;
    private LayoutInflater mInflater;

    public CustomExpandableListAdapter(Context context, ArrayList<MessageListModel> groups) {
        mContext = context;
        mGroups = groups;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroups.get(groupPosition).Msgs.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroups.get(groupPosition).Msgs.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.message_section_header, null);
        }

        // Get the group item
        MessageListModel group = (MessageListModel) getGroup(groupPosition);

        // Set group name
        TextView textView = (TextView) convertView.findViewById(R.id.sectionHeader);
        textView.setText(group.SectionHeader);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.messagelist_template, null);
        }

        // Get child name
        Message children = (Message) getChild(groupPosition, childPosition);

        // Set child name
        TextView txtInfo = (TextView) convertView.findViewById(R.id.txtInfo);
        TextView txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
        ImageView adminimage=(ImageView)convertView.findViewById((R.id.adminimage));
        txtInfo.setText(children.getAdminName());
        txtMessage.setText(children.getMessage());
        adminimage.setImageBitmap(children.getAdminImage());

        /*convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, children, Toast.LENGTH_SHORT).show();
            }
        });*/

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
