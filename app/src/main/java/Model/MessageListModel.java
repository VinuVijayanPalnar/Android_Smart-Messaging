package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arun on 08-14-2015.
 */
public class MessageListModel {
   public String SectionHeader;
    public ArrayList<Message> Msgs= new ArrayList<Message>();
    public  MessageListModel (String Name)
    {
        SectionHeader=Name;
    }
}


