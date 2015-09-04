package DataBaseSQlite;

import android.content.ContentValues;
import android.content.Context;
import DataBaseSQlite.TableData.TableInfo;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Arun on 08-21-2015.
 */
public class DatabaseOperations extends SQLiteOpenHelper {

    public  static final int database_version=1;
    public  String CREATE_TABLE_USERDETAILS_QUERY="CREATE TABLE "+TableInfo.TABLE_USERDETAILS+"("+TableInfo.USERNAME+" TEXT   NOT NULL,"+TableInfo.EMAILID+" TEXT  NOT NULL,"
                                                    +TableInfo.FIRSTNAME+" TEXT,"+TableInfo.LASTNAME+" TEXT,"+TableInfo.PHONE_NO+" LONG,"+TableInfo.USERIMAGE+" TEXT);";
    public String CREATE_TABLE_MESSAGEDETAILS_QUERY="CREATE TABLE "+TableInfo.TABLE_MESSAGEDETAILS+"("+TableInfo.SENDER_ID+" INTEGER,"+TableInfo.SENDER_NAME+" TEXT,"
                                                     +TableInfo.MESSAGES+" TEXT,"+TableInfo.CREATED_DATE+" DATETIME);";
    public String CREATE_TABLE_ADMINDETAILS_QUERY="CREATE TABLE "+TableInfo.TABLE_ADMINDETAILS+"("+TableInfo.ADMIN_ID+" INTEGER,"+TableInfo.ADMIN_IMAGE+" TEXT);";

    public DatabaseOperations(Context context){
        super(context, TableInfo.DATABASE_NAME,null,database_version);
        Log.d("DATABASE OPERATIONS","Database Craeted");
    }

    @Override
    public void onCreate(SQLiteDatabase smdb) {
        try {
            smdb.execSQL(CREATE_TABLE_USERDETAILS_QUERY);
            Log.d("TABLE UserDetails", "Created");
            smdb.execSQL(CREATE_TABLE_MESSAGEDETAILS_QUERY);
            Log.d("TABLE MessageDetails", "Created");
            smdb.execSQL(CREATE_TABLE_ADMINDETAILS_QUERY);
            Log.d("TABLE AdminDetails", "Created");
        }catch (SQLException e){
            Log.e("SQLException",e.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TableInfo.DATABASE_NAME, "Upgrading Database from version" + oldVersion + "to" + newVersion + "which will distroy old data");
        db.execSQL("DROP TABLE IF EXISTS" + TableInfo.TABLE_USERDETAILS);
        db.execSQL("DROP TABLE IF EXISTS "+TableInfo.TABLE_MESSAGEDETAILS);
        db.execSQL("DROP TABLE IF EXISTS senderDetails"+TableInfo.TABLE_ADMINDETAILS);
        onCreate(db);
    }
    public void close(DatabaseOperations Dop)
    {
        Dop.close();
        Log.e("DATABASE OPERATIONS", "database closed");
    }

    public  boolean IsUserPresent(DatabaseOperations Dop, String username){
        SQLiteDatabase sq=Dop.getReadableDatabase();
        String[] columns={TableInfo.USERNAME,TableInfo.EMAILID,TableInfo.FIRSTNAME,TableInfo.LASTNAME,TableInfo.PHONE_NO,TableInfo.USERIMAGE};
        String[] args={username};
        Cursor cursor=sq.query(TableInfo.TABLE_USERDETAILS,columns,TableInfo.USERNAME+"=?",args,null,null,null);
        if(cursor.getCount()<=0){
            Log.e(" IS USERDATA PRESENT:", "User data not available-Returned false");
            Dop.close();
            return false;
        }
        else{
            Log.e(" IS USERDATA PRESENT:", "User data available-Returned True");
            Dop.close();
            return true;
        }

    }

    public void InsertUserDetails(DatabaseOperations Dop,String username,String email,String firstname,String lastname,String phoneNo, String userimage)
    {
        SQLiteDatabase sq=Dop.getWritableDatabase();

        ContentValues initialValue= new ContentValues();
        initialValue.put(TableInfo.USERNAME,username);
        initialValue.put(TableInfo.EMAILID,email);
        initialValue.put(TableInfo.FIRSTNAME,firstname);
        initialValue.put(TableInfo.LASTNAME,lastname);
        initialValue.put(TableInfo.PHONE_NO, phoneNo);
        initialValue.put(TableInfo.USERIMAGE, userimage);
        long dbid= sq.insert(TableInfo.TABLE_USERDETAILS, null, initialValue);
        Log.d("USER TABLE INSERTION:","Data Inserted");

        Dop.close();
    }
    public  void UpdateUserDetails(DatabaseOperations Dop,String username,String firstname,String lastname,Long phoneNO,String Photo){

        SQLiteDatabase sq=Dop.getWritableDatabase();
        String[] args={username};

        ContentValues initialValue= new ContentValues();
        initialValue.put(TableInfo.USERNAME,username);
        initialValue.put(TableInfo.FIRSTNAME,firstname);
        initialValue.put(TableInfo.LASTNAME,lastname);
        initialValue.put(TableInfo.PHONE_NO, phoneNO);
        initialValue.put(TableInfo.USERIMAGE, Photo);
        sq.update(TableInfo.TABLE_USERDETAILS, initialValue, TableInfo.USERNAME + "=?", args);
        Log.d("USER TABLE UPDATION:","User Data Updated");

        Dop.close();

    }
    public void InsertMessageDetails(DatabaseOperations Dop,String message,String date,String Adminname,int admin_Id)
    {
        SQLiteDatabase sq=Dop.getWritableDatabase();
        ContentValues initialValue= new ContentValues();
        initialValue.put(TableInfo.MESSAGES,message);
        initialValue.put(TableInfo.CREATED_DATE, String.valueOf(date));
        initialValue.put(TableInfo.SENDER_ID,admin_Id);
        initialValue.put(TableInfo.SENDER_NAME, Adminname);
        long dbid= sq.insert(TableInfo.TABLE_MESSAGEDETAILS, null, initialValue);
        Log.d("MSG TABLE INSERTION:","Message Inserted");

        Dop.close();
    }

    public  boolean IsDataAvailable(DatabaseOperations Dop){
        SQLiteDatabase sq=Dop.getReadableDatabase();
        String[] columns={TableInfo.SENDER_ID,TableInfo.SENDER_NAME,TableInfo.MESSAGES,TableInfo.CREATED_DATE};
       Cursor cursor=sq.query(TableInfo.TABLE_USERDETAILS, columns, null, null, null, null, null);
        if(cursor.getCount()<=0){
            Log.e(" IS MESSAGES PRESENT:", "Messages not available-Returned false");
            Dop.close();
            return false;
        }
        else{
            Log.e(" IS MESSAGES PRESENT:", "Messages available-Returned True");
            Dop.close();
            return true;
        }

    }
    public  Cursor GetAllMessages(DatabaseOperations Dop){
        SQLiteDatabase sq=Dop.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TableInfo.TABLE_MESSAGEDETAILS +" md, " + TableInfo.TABLE_ADMINDETAILS + " ad WHERE md." + TableInfo.SENDER_ID + " = "
                + "ad." + TableInfo.ADMIN_ID+" ORDER BY DATETIME("+TableInfo.CREATED_DATE+") DESC";
//        order  by datetime(datetimeColumn) DESC LIMIT 1
//        String selectQuery = "SELECT  * FROM " + TableInfo.TABLE_MESSAGEDETAILS +" ORDER BY DATETIME("+TableInfo.CREATED_DATE+") DESC";
        Cursor c=sq.rawQuery(selectQuery, null);
        Log.d("MSG TABLE RETRIEVE:", "Messages Retrieved");
        return  c;
}
    public  String GetAdminPhoto(DatabaseOperations Dop, String adminId){
        SQLiteDatabase sq=Dop.getReadableDatabase();
        String Sender_Image = null;
        String[] columns={TableInfo.ADMIN_IMAGE};
        String[] args={adminId};
        Cursor cursor=sq.query(TableInfo.TABLE_ADMINDETAILS, columns, TableInfo.ADMIN_ID + "=?", args, null, null, null);
        Log.d("ADMIN IMAGE:", "Admin Image Retrieved");
        if(cursor!=null && cursor.moveToFirst())
        {
            Sender_Image = cursor.getString(0);
        }
        Dop.close();
      return Sender_Image;
    }

    public void InsertAdminImg(DatabaseOperations Dop, int admin_Id,String img)
    {
        SQLiteDatabase sq=Dop.getWritableDatabase();
        ContentValues initialValue= new ContentValues();
        initialValue.put(TableInfo.ADMIN_ID,admin_Id);
        initialValue.put(TableInfo.ADMIN_IMAGE, img);
        long dbid= sq.insert(TableInfo.TABLE_ADMINDETAILS, null, initialValue);
        Log.d("ADMIN IMAGE INSERTION:","Image Inserted");

        Dop.close();
    }
    public Date ConvertStringToDate(String _date)
    {
        DateFormat formatter = null;
        Date date = null;
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = formatter.parse(_date);
            String v = formatter.format(date);
         } catch (ParseException ex) {
            System.out.println(ex.getMessage());
            return date;
        }
        return date;

    }
}
