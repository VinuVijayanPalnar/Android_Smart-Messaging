package DataBaseSQlite;

import android.provider.BaseColumns;

/**
 * Created by Arun on 08-21-2015.
 */
public class TableData {
    public TableData()
    {}
    public static  abstract  class TableInfo implements BaseColumns
    {
        public static final String DATABASE_NAME="SmartMessageDb";

        public static final String TABLE_USERDETAILS="user_details";
        public static final String USERNAME="user_name";
        public static final String EMAILID="email";
        public static final String FIRSTNAME="first_name";
        public static final String LASTNAME="last_name";
        public static  final String PHONE_NO="phone_no";
        public static final String USERIMAGE="user_image";

        public  static final String TABLE_MESSAGEDETAILS="message_details";
        public static final String CREATED_DATE="created_date";
        public static final String MESSAGES="messages";
        public static final String SENDER_NAME="sender_name";
        public static final String SENDER_ID="sender_id";

        public static final String TABLE_ADMINDETAILS="admin_details";
        public static final String ADMIN_IMAGE="admin_image";
        public static final String ADMIN_ID="admin_id";
    }
}
