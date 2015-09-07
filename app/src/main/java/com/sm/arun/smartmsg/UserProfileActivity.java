package com.sm.arun.smartmsg;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import DataBaseSQlite.DBAdapter;
import DataBaseSQlite.DatabaseOperations;
import Model.AppUser;


/**
 * Created by Arun on 07-27-2015.
 */
public class UserProfileActivity extends Activity{
    private static final String MY_PREFS_NAME = "MyPrefs";
    public static final String REG_ID = "regId";
    ImageView ImageV;
    EditText FrstName,LstName,PhoneNo,Email;
    TextView UserName;
    Bitmap UserImage;
    Button Save;
    ImageButton Edit,Backbtn;
    long DbId=-1;
    Context applicationcontext;
    DatabaseOperations Db;
    DBAdapter db;

    String Username,email,regId;
    String encoded_image="No image";
    private static final int SELECT_PHOTO = 100;
    private static final int CAMERA_REQUEST=1888;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userprofile_layout);
        Intent myIntent= getIntent();
        Bundle b = myIntent.getExtras();
        applicationcontext=getApplicationContext();
        Db=new DatabaseOperations(applicationcontext);

        ImageV=(ImageView)findViewById(R.id.imageView);
        UserName=(TextView)findViewById(R.id.UserNameTxTView);
        FrstName=(EditText)findViewById(R.id.ETxtFirstName);
        LstName=(EditText)findViewById(R.id.ETxtLastName);
        Email=(EditText)findViewById(R.id.ETxtemail);
        PhoneNo=(EditText)findViewById(R.id.ETxtPhone);
        Save=(Button)findViewById(R.id.SaveBtn);
        Edit=(ImageButton)findViewById(R.id.editBtn);

        SharedPreferences Preferences= getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        Username=Preferences.getString("UserName", "");
        email=Preferences.getString("Email","");
        DbId=Preferences.getLong("UserId", 0);
        SharedPreferences prefs = getSharedPreferences("UserDetails",Context.MODE_PRIVATE);
        regId = prefs.getString(REG_ID, "");

        registerForContextMenu(ImageV);
        if(!Username.equalsIgnoreCase("")) {
            UserName.setText(Username.toString());
            Email.setText(email.toString());
        }
        String Emailid="",Firstnmae="",lastname="",phoneno="",photo="";
        Cursor Cr=Db.GetProfileDetails(Db,Username);
        if(Cr!=null && Cr.moveToFirst())
        {
            do{
//                DbId=Cr.getInt(0);
                Emailid =Cr.getString(1);
                Firstnmae= Cr.getString(2);
                lastname=Cr.getString(3);
                phoneno=Cr.getString(4);
                photo=Cr.getString(5);
                encoded_image=photo;
            }while (Cr.moveToNext());
        }
        Cr.close();
        Db.close();

        if(Emailid!=null)
            Email.setText(Emailid.toString());
        if(Firstnmae!=null)
            FrstName.setText(Firstnmae.toString());
        if(lastname!=null)
            LstName.setText(lastname.toString());
        if(phoneno!=null)
            PhoneNo.setText(phoneno.toString());
        if(photo!=null && photo!=""){
            if(decodeBase64(photo)!=null)
            ImageV.setImageBitmap(decodeBase64(photo));
        }

        FrstName.setEnabled(false);
        LstName.setEnabled(false);
        Email.setEnabled(false);
        PhoneNo.setEnabled(false);
        ImageV.setEnabled(false);
        Save.setEnabled(false);
    }
    public void BackToMessages(View view){
        Intent i = new Intent(UserProfileActivity.this, MessagingActivity.class);
        i.putExtra("regId", regId);
        startActivity(i);
        finish();
    }
    public  void SaveProfileDetails(View view) {
        Db.UpdateUserDetails(Db,UserName.getText().toString(),FrstName.getText().toString(),LstName.getText().toString(),Long.parseLong(PhoneNo.getText().toString()),encoded_image);
//        db.open();
//        boolean IsSaved =db.updateprofile(DbId,FrstName.getText().toString(),LstName.getText().toString(),Long.parseLong(PhoneNo.getText().toString()),encoded_image);
//        Toast.makeText(UserProfileActivity.this, "Is Saved:"+IsSaved, Toast.LENGTH_LONG).show();
//        db.close();
        FrstName.setEnabled(false);
        LstName.setEnabled(false);
        PhoneNo.setEnabled(false);
        ImageV.setEnabled(false);
        Save.setEnabled(false);
        Edit.setEnabled(true);
        EditProfile(UserName.getText().toString(), Email.getText().toString());

    }
    public  void EditProfileDetails(View view) {
       FrstName.setEnabled(true);
        LstName.setEnabled(true);
        PhoneNo.setEnabled(true);
        ImageV.setEnabled(true);
        Save.setEnabled(true);
        Edit.setEnabled(false);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId()==R.id.imageView)
        {
            // to set the context menu's header icon
//            menu.setHeaderIcon(R.drawable.iconimage);

            // to set the context menu's title
            menu.setHeaderTitle("Upload Image");

            // to add a new item to the menu
            menu.add(0,0,0, "Take a Photo");
            menu.add(0,1,0,"Choose from Galary");
        }

    }
    /*
     * This method is called when an item in a context menu is selected.
     *
     */

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case 0:
                CapturePhoto(item.getActionView());
                break;
            case 1:
                UploadImage(item.getActionView());
                break;
        }
        return true;
    }

   public void UploadImage(View view) {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }
    public void CapturePhoto(View view) {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        UserImage=decodeUri(selectedImage);
                        ImageV.setImageBitmap(UserImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            case CAMERA_REQUEST:
                if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                   UserImage = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    Uri tempUri = getImageUri(getApplicationContext(), UserImage);

                    // CALL THIS METHOD TO GET THE ACTUAL PATH
                    File finalFile = new File(getRealPathFromURI(tempUri));

//                   Bitmap photo= null;
//                    try {
//                        photo = decodeUri(imageReturnedIntent.getData());
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    if(photo.getWidth() > photo.getHeight())
//                    { Uri selectedImage=imageReturnedIntent.getData();
//                        ExifInterface exif = null;
//                        try {
//                            exif = new ExifInterface(selectedImage.getPath());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        float rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//                        System.out.println(rotation);
//
//                        float rotationInDegrees = exifToDegrees(rotation);
//                        System.out.println(rotationInDegrees);
//
//                        Matrix matrix = new Matrix();
//                        matrix.postRotate(rotationInDegrees);
//                        Bitmap rotatedBitmap = Bitmap.createBitmap(photo , 0, 0, photo .getWidth(), photo .getHeight(), matrix, true);
//                        ImageV.setImageBitmap(rotatedBitmap);
//                        // landscape
//                    }else
//                    {
//                        // portrait
//                        ImageV.setImageBitmap(photo);
//
//                    }
                    ImageV.setImageBitmap(UserImage);


                }
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    private static float exifToDegrees(float exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }
    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 70;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
//        Bitmap ScaledImage=
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
//        ExifInterface exifInterface = null;
//        try {
//            exifInterface = new ExifInterface(selectedImage.getPath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        int degree = Integer.parseInt(exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));
//        return  rotateBitmap(ScaledImage, degree);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    private void EditProfile(String UserName, String E_mail) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected String doInBackground(String... params) {
                BufferedReader br = null;
                StringBuilder sb = new StringBuilder("");

                HttpURLConnection c = null;
                try {

                    int timeout=30;
                    if(UserImage!=null)
                    encoded_image=encodeTobase64(UserImage);
                    URL u = new URL(ApplicationConstants.APP_SERVER_URL+"/smartchat/public/editUser");
                    c = (HttpURLConnection) u.openConnection();
                    c.setRequestMethod("POST");
                    // c.setRequestProperty("Content-length", "0");
//                    c.setRequestProperty("Content-Type", "application/json");
                    c.setUseCaches(false);
//                    c.setAllowUserInteraction(false);
                    List<NameValuePair> data = new ArrayList<NameValuePair>();

                    data.add(new BasicNameValuePair("username", Username));
                    data.add(new BasicNameValuePair("email", email));
                    data.add(new BasicNameValuePair("first_name", FrstName.getText().toString()));
                    data.add(new BasicNameValuePair("last_name", LstName.getText().toString()));
                    data.add(new BasicNameValuePair("mobile_number", PhoneNo.getText().toString()));
                    data.add(new BasicNameValuePair("user_group_id", "1"));
                    data.add(new BasicNameValuePair("device_type_id","2"));
                    data.add(new BasicNameValuePair("device_id", "xxyyrryyxx"));
                    data.add(new BasicNameValuePair("is_verified", "1"));
                    data.add(new BasicNameValuePair("photo",encoded_image));

                    OutputStream os = c.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getQuery(data));
                    writer.flush();
                    writer.close();
                    os.close();
    //               c.setConnectTimeout(timeout);
    //               c.setReadTimeout(timeout);
                    c.connect();
                    int status = c.getResponseCode();

                    switch (status) {
                        case 200:
                        case 201:
                            br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                            sb = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line+"\n");
                            }
                            br.close();
                            return sb.toString();
                    }
                  } catch (MalformedURLException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if (c != null) {
                        try {
                            c.disconnect();
                        } catch (Exception ex) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                return sb.toString();
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    //ArrayList<HashMap<String, String>> medecins = new ArrayList<HashMap<String, String>>();
// ========THIS IS USED IF THE RESPONSE IS A JSON ARRAY=======================
//                JSONArray js = new JSONArray(result.toString());
//                for(int i=0; i<js.length(); i++){
                    JSONObject jsObj = new JSONObject(result.toString());
//                    int id = Integer.parseInt(jsObj.getString("id"));
//                    String username = jsObj.getString("username");
//                    String email = jsObj.getString("email");
//                    String firstname = jsObj.getString("first_name");
//                    String lastname = jsObj.getString("last_name");
//                    String mobile_no = jsObj.getString("mobile_number");
                    String status = jsObj.getString("reg_status");
                    Toast.makeText(UserProfileActivity.this,"Response status"+status,Toast.LENGTH_LONG).show();
                    Log.e("Response status",status);
//                    Bitmap bm =  decodeBase64(blob);
//                    txtResponse.setImageBitmap(bm);
//
//                    AppUser user= new AppUser(id,username,email,firstname,lastname,mobile_no,bm);
//
//                    Users.add(i,user);
//                }

                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("Erreur 4");
                }
            }
        }.execute();

    }
    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
    public static String encodeTobase64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }
}
