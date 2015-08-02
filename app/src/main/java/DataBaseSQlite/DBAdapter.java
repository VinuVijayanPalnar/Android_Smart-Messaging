package DataBaseSQlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Arun on 07-28-2015.
 */
public class DBAdapter {
    static final String DATABASE_NAME="MyDB";
    static final String TAG="DBAdapter";
    static final int  DATABASE_VERSION=1;
    static final String TABLE_DETAILS="profileDetails";
    static final String TABLE_MESSAGES="pushMessages";
    // TABLE_DETAILS fields
    static final String KEY_DROWID="D_id";
    static final String KEY_USERNAME="userName";
    static final String KEY_EMAILID="email";
    static final String KEY_FIRSTNAME="firstName";
    static final String KEY_LASTNAME="lastName";
    static  final String KEY_PHONE_NO="phoneNo";
    //TABLE_MESSAGES fields
    static final String KEY_MROWID="M_id";
    static final String KEY_DATE="date";
    static final String KEY_MESSAGE="message";
    static final String KEY_ADMIN_NAME="adminName";

    //CREATE TABLE profileDetails
    static final String DATABASE_CREATE_profileDetails="create table profileDetails(" +
            "D_id        integer     primary key     autoincrement,"+
            "userName    text        not null," +
            "email       text        not null," +
            "firstName   text," +
            "lastName    text," +
            "phoneNo     long);";

    static final String DATABASE_CREATE_pushMessages="create table pushMessages(" +
            "M_id        integer         primary key     autoincrement,"+
            "date        datetime        not null," +
            "message     text            not null," +
            "adminName   text            not null);";

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
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                db.execSQL(DATABASE_CREATE_pushMessages);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading Database from version" + oldVersion + "to" + newVersion + "which will distroy old data");
            db.execSQL("DROP TABLE IF EXISTS profileDetails");
            db.execSQL("DROP TABLE IF EXISTS pushMessages");
            onCreate(db);
        }
    }
//--------------------OPEN DATABASE--------------------------------
        public DBAdapter open()throws SQLException
        {
          db=DBHelper.getWritableDatabase();
            return  this;
        }
// ---------------------CLOSE DATABASE-----------------------------------
    public void close()
    {
        DBHelper.close();
    }
//=======================INSERT INTO DATABASE============================

    //-------------------INSERT PROFILE_DETAILS INTO DATABASE--------------------------
    public long insertProfileDeatils(String userName,String email, String firstName,String lastName,String phoneNo){
        ContentValues initialValue= new ContentValues();
        initialValue.put(KEY_USERNAME,userName);
        initialValue.put(KEY_EMAILID,email);
        initialValue.put(KEY_FIRSTNAME,firstName);
        initialValue.put(KEY_LASTNAME,lastName);
        initialValue.put(KEY_PHONE_NO, phoneNo);
        return  db.insert(DATABASE_CREATE_profileDetails,null,initialValue);
    }

    //-------------------INSERT MESSAGES INTO DATABASE--------------------------

    public long insertMessges(String date,String message, String adminName){
        ContentValues initialValue= new ContentValues();
        initialValue.put(KEY_DATE,date);
        initialValue.put(KEY_MESSAGE,message);
        initialValue.put(KEY_ADMIN_NAME,adminName);
        return  db.insert(DATABASE_CREATE_profileDetails,null,initialValue);
    }

//-=====================DELETE FROM DATABASE================================
    //-------------------DELETE A PERTICULAR MESSAGE------------------
    public  boolean deleteMessage( long M_id)
    {
        return db.delete(DATABASE_CREATE_pushMessages,KEY_MROWID+"="+M_id,null)>0;
    }
// =======================RETRIEVE DATA FROM DATABASE==============================
    //--------------------RETRIEVE ALL PROFILE DETAILS-------------------------------
    public Cursor getAllProfileDetails()
    {
        return db.query(DATABASE_CREATE_profileDetails, new String[]{KEY_USERNAME, KEY_EMAILID, KEY_FIRSTNAME, KEY_LASTNAME, KEY_PHONE_NO}, null, null, null, null, null);
    }
    //--------------------RETRIEVE A PERTICULAR PROFILE DETAILS-------------------------------

    public  Cursor getProfileDetails(String username)throws SQLException {
        String query = "select * from profileDetails where userName ="+ username;
        Cursor mCursor = db.rawQuery(query, new String[]{KEY_DROWID,KEY_USERNAME,KEY_EMAILID,KEY_FIRSTNAME,KEY_LASTNAME,KEY_PHONE_NO});
//        Cursor mCursor=db.query(true,DATABASE_CREATE_profileDetails,new String[]{KEY_DROWID,KEY_USERNAME,KEY_EMAILID,KEY_FIRSTNAME,KEY_LASTNAME,KEY_PHONE_NO},KEY_USERNAME+"="+userName,null,null,null,null);
        if (mCursor != null) {
            mCursor.moveToFirst();

        }
        return  mCursor;
    }

    //--------------------RETRIEVE ALL MESSAGES-------------------------------
    public Cursor getAllMessages()
    {
      return db.query(DATABASE_CREATE_pushMessages,new String[]{KEY_DATE,KEY_MESSAGE,KEY_ADMIN_NAME},null,null,null,null,null);
    }
    //--------------------RETRIEVE A PERTICULAR PROFILE DETAILS-------------------------------

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
    public boolean updateprofile(long RowId,String UserName,String Email,String FirstName,String LastName,long PhoneNo){
        ContentValues args = new ContentValues();
        args.put(KEY_FIRSTNAME,FirstName);
        args.put(KEY_LASTNAME,LastName);
        args.put(KEY_PHONE_NO,PhoneNo);
        return db.update(DATABASE_CREATE_profileDetails,args,KEY_DROWID+"="+RowId,null)>0;
    }

    //-----------------------------------UDATE MESSAGES------------------------
}