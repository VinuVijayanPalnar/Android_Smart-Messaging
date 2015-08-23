package DataBaseSQlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Arun on 07-28-2015.
 */
public class DBAdapter {
    static final String DATABASE_NAME="MyDB";
    static final String TAG="DBAdapter";
    static final int  DATABASE_VERSION=1;
    static final String TABLE_DETAILS="profileDetails";
    static final String TABLE_MESSAGES="pushMessages";
    static final String ADMIN_DETAILS="senderDetails";

    // TABLE_DETAILS fields
    static final String KEY_DROWID="D_id";
    static final String KEY_USERNAME="userName";
    static final String KEY_EMAILID="email";
    static final String KEY_FIRSTNAME="firstName";
    static final String KEY_LASTNAME="lastName";
    static  final String KEY_PHONE_NO="phoneNo";
    static final String KEY_USERIMAGE="photo";

    //TABLE_MESSAGES fields
    static final String KEY_MROWID="M_id";
    static final String KEY_DATE="date";
    static final String KEY_MESSAGE="message";
    static final String KEY_ADMIN_NAME="adminName";
    static final String KEY_ADMIN_ID="adminid";

    //ADMIN_DETAILS fields
    static final String KEY_ADMIN_IMAGE="adminImage";
    static final String ADMIN_ID="senderId";

    //CREATE TABLE profileDetails
    static final String DATABASE_CREATE_profileDetails="create table profileDetails(" +
            "D_id        integer     primary key     autoincrement,"+
            "userName    text        not null," +
            "email       text        not null," +
            "firstName   text," +
            "lastName    text," +
            "phoneNo     long,"+
            "photo       blob);";

    //CREATE TABLE pushMessages
    static final String DATABASE_CREATE_pushMessages="create table pushMessages(" +
            "M_id        integer         primary key      autoincrement,"+
            "date        datetime    default  CURRENT_TIMESTAMP," +
            "message     text            not null," +
            "adminid     integer         ," +
            "adminName   text            not null);";
    //CREATE TABLE senderDetails
    static  final String DATABASE_CREATE_senderDetails="create table senderDetails("+
            "senderId   integer   primary key,"+
            "adminImage text    not null);";

        Context context = null;
        DatabaseHelper DBHelper;
        SQLiteDatabase db;

    public DBAdapter(Context Ctx)
    {
        this.context=Ctx;
        DBHelper= new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE_profileDetails);
                Log.e("DATABASE profileDetails", "Created");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                db.execSQL(DATABASE_CREATE_pushMessages);
                Log.e("DATABASE pushMessages", "Created");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                db.execSQL(DATABASE_CREATE_senderDetails);
                Log.e("DATABASE senderDetails", "Created");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading Database from version" + oldVersion + "to" + newVersion + "which will distroy old data");
            db.execSQL("DROP TABLE IF EXISTS profileDetails");
            db.execSQL("DROP TABLE IF EXISTS pushMessages");
            db.execSQL("DROP TABLE IF EXISTS senderDetails");
            onCreate(db);
        }
    }
//--------------------OPEN DATABASE--------------------------------
        public DBAdapter open()throws SQLException
        {
          db=DBHelper.getWritableDatabase();
            Log.e("DATABASE OPERATIONS","database created or opened"+db.getAttachedDbs());
            return  this;
        }
// ---------------------CLOSE DATABASE-----------------------------------
    public void close()
    {
        DBHelper.close();
        Log.e("DATABASE OPERATIONS", "database closed");
    }
//=======================INSERT INTO DATABASE============================

    public boolean checkBeforeInsert(String username)
    {Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_DETAILS + " WHERE    username=?", new String[]{username});
        }catch (Exception e){
               System.out.println(e.getMessage());
        }

        if(cursor.getCount()<=0){
            Log.e("mCursor", "no item");
            return false;
        }
         else{
            Log.e("Check if present", "true");
            return true;
           }

        }
    //-------------------INSERT PROFILE_DETAILS INTO DATABASE--------------------------
    public long insertProfileDeatils(String userName,String email, String firstName,String lastName,String phoneNo,String photo){
        ContentValues initialValue= new ContentValues();
        initialValue.put(KEY_USERNAME,userName);
        initialValue.put(KEY_EMAILID,email);
        initialValue.put(KEY_FIRSTNAME,firstName);
        initialValue.put(KEY_LASTNAME,lastName);
        initialValue.put(KEY_PHONE_NO, phoneNo);
        initialValue.put(KEY_USERIMAGE,photo);
        long dbid= db.insert(TABLE_DETAILS, null, initialValue);
        Log.e("DATABASE OPERATIONS", "Value inserted");
        return dbid;

    }

    //-------------------INSERT MESSAGES INTO DATABASE--------------------------

    public long insertMessges(String message,String date, String adminName,Integer AdminId){
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date _Date =ConvertStringToDate(date);
        if(_Date==null)
        {   Calendar c = Calendar.getInstance();
            _Date=c.getTime();
        }

         ContentValues initialValue= new ContentValues();
       initialValue.put(KEY_DATE, String.valueOf(_Date));
        initialValue.put(KEY_MESSAGE,message);
        initialValue.put(KEY_ADMIN_NAME, adminName);
        initialValue.put(KEY_ADMIN_ID, AdminId);
        long MsgDbId=db.insert(TABLE_MESSAGES, null, initialValue);
        Log.e("Message inserted","Done");
        return  MsgDbId;
    }

    //-------------------INSERT SENDER DATAILS INTO DATABASE--------------------------

    public long insertSender(Integer senderId,String Senderimage){
        ContentValues initialValue= new ContentValues();
        initialValue.put(ADMIN_ID,senderId);
        initialValue.put(KEY_ADMIN_IMAGE,Senderimage);
        return  db.insert(ADMIN_DETAILS,null,initialValue);
    }

//-=====================DELETE FROM DATABASE================================
//-------------------DELETE A PERTICULAR profile detail------------------
    public  boolean deleteProfileDeatils( long D_id)
    {
        return db.delete(TABLE_DETAILS,KEY_MROWID+"="+D_id,null)>0;
    }
    //-------------------DELETE A PERTICULAR MESSAGE------------------
    public  boolean deleteMessage( long M_id)
    {
        return db.delete(TABLE_MESSAGES,KEY_MROWID+"="+M_id,null)>0;
    }

    //-------------------DELETE All MESSAGEs------------------
    public  boolean deleteMessage()
    {
        try {
            db.execSQL("delete * from "+ TABLE_MESSAGES);
            return true;
        }catch (Exception e)
        {
            System.out.println(e.getMessage());
            return false;
        }

    }

// =======================RETRIEVE DATA FROM DATABASE==============================
    //--------------------RETRIEVE ALL PROFILE DETAILS-------------------------------
    public Cursor getAllProfileDetails()
    {
        Cursor mCursor= db.query(TABLE_DETAILS, new String[]{KEY_USERNAME, KEY_EMAILID, KEY_FIRSTNAME, KEY_LASTNAME, KEY_PHONE_NO}, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();

        }
        return mCursor;
    }
    //--------------------RETRIEVE A PERTICULAR PROFILE DETAILS-------------------------------

    public  Cursor getProfileDetails(long Dbid)throws SQLException {
//        ====== WORKING QUERY WITH USERNAME=================
//     String query = " SELECT * FROM profileDetails WHERE userName = ?";
//       String query = "SELECT * FROM "+ TABLE_DETAILS +" WHERE "+KEY_USERNAME+" = "+"'"+username+"'"+";";
//       Cursor mCursor = db.rawQuery(query, new String[]{""+username},null);
        String query = " SELECT * FROM profileDetails WHERE D_id = ?";
        Cursor mCursor = db.rawQuery(query, new String[]{""+Dbid},null);
         if (mCursor != null) {
            mCursor.moveToFirst();
         }
        Log.e("mCursor", mCursor.getString(0));
        Log.e("mCursor", mCursor.getString(1));
        Log.e("mCursor", mCursor.getString(2));
        Log.e("mCursor", mCursor.getString(3));
        Log.e("mCursor", mCursor.getString(4));
        Log.e("mCursor", mCursor.getString(5));
        Log.e("mCursor", mCursor.getString(6));
        Log.e("DATABASE OPERATIONS", "Retrieved Data");
        return  mCursor;
    }

    //--------------------RETRIEVE ALL MESSAGES-------------------------------
    public Cursor getAllMessages()
    {
        Cursor mCursor=db.rawQuery("SELECT * FROM pushMessages, senderDetails " +
            "WHERE pushMessages.adminid = senderDetails.senderId " +
            "GROUP BY pushMessages.date", null);

//        String query = " SELECT * FROM pushMessages";
//        Cursor mCursor = db.rawQuery(query, new String[]{},null);
//        Cursor  mCursor=db.query(TABLE_MESSAGES, new String[] {KEY_MROWID, KEY_MESSAGE,
//                KEY_ADMIN_NAME, KEY_ADMIN_ID,KEY_DATE}, null, null, null, null, KEY_DATE + " DESC");
        if (mCursor != null) {
            mCursor.moveToFirst();
            Log.e("M_id", mCursor.getString(0));
            Log.e("date", mCursor.getString(1));
            Log.e("message", mCursor.getString(2));
//            Log.e("adminid", mCursor.getString(3));
//            Log.e("adminName", mCursor.getString(4));
//            Log.e("adminImage", mCursor.getString(5));


        }
        return  mCursor;

    }
    //--------------------RETRIEVE A PERTICULAR Message table-------------------------------

    public boolean IsMessagesAvailable() {
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM"+ TABLE_MESSAGES, new String[]{},null);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        if(cursor.getCount()<=0){
            Log.e("mCursor", "no item");
            return false;
        }
        else{
            Log.e("Check if present", "true");
            return true;
        }

    }

    public  Cursor getMessages(String adminname)throws SQLException {
        String query = "select * from pushMessages where adminName ="+ adminname;
        Cursor mCursor = db.rawQuery(query, new String[]{KEY_DATE,KEY_MESSAGE,KEY_ADMIN_NAME});
      if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return  mCursor;
    }
//==========================================UPDATE=================================
    //------------------------------------UPDATE PROFILE DETAILS------------------
    public boolean updateprofile(long RowId,String FirstName,String LastName,long PhoneNo,String photo){
        ContentValues args = new ContentValues();
        args.put(KEY_FIRSTNAME,FirstName);
        args.put(KEY_LASTNAME, LastName);
        args.put(KEY_PHONE_NO, PhoneNo);
        args.put(KEY_USERIMAGE,photo);
        return db.update(DATABASE_CREATE_profileDetails,args,KEY_DROWID+"="+RowId,null)>0;
    }

    //-----------------------------------UDATE MESSAGES------------------------

    public Date ConvertStringToDate(String _date)
     {
        DateFormat formatter = null;
        Date date = null;
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = formatter.parse(_date);
            //if you want you can convert it to `java.sql.Date`
            String v = formatter.format(date);
            //String DATE=df.format(_Date);
//        _Date=ConvertStringToDate(DATE);
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
            return date;
         }
         return date;

    }
}
