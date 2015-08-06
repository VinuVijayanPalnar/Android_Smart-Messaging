package Model;

import android.graphics.Bitmap;

/**
 * Created by Arun on 08-05-2015.
 */
public class AppUser {
    public static int id;
    public static String name;
    public static String username;
    public static String email;
    public static String first_name;
    public static String last_name;
    public static String mobile_number;
    public static int user_group_id;
    public static int device_type_id;
    public static Bitmap photo;
    public  AppUser(int id,String username,String emaiid, String firstname,String lastname,String mob,Bitmap photo)
    {
      this.id=id;
        this.username=username;
        this.email=emaiid;
        this.first_name=firstname;
        this.last_name=lastname;
        this.mobile_number=mob;
        this.photo=photo;
    }

}
