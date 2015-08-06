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
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import DataBaseSQlite.DBAdapter;


/**
 * Created by Arun on 07-27-2015.
 */
public class UserProfileActivity extends Activity{
    private static final String MY_PREFS_NAME = "MyPrefs";
    ImageView ImageV;
    EditText FrstName,LstName,PhoneNo,Email;
    TextView UserName;
    long DbId=-1;
    DBAdapter db;
    private static final int SELECT_PHOTO = 100;
    private static final int CAMERA_REQUEST=1888;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userprofile_layout);
        Intent myIntent= getIntent();
        Bundle b = myIntent.getExtras();

        ImageV=(ImageView)findViewById(R.id.imageView);
        UserName=(TextView)findViewById(R.id.UserNameTxTView);
        FrstName=(EditText)findViewById(R.id.ETxtFirstName);
        LstName=(EditText)findViewById(R.id.ETxtLastName);
        Email=(EditText)findViewById(R.id.ETxtemail);
        PhoneNo=(EditText)findViewById(R.id.ETxtPhone);

        SharedPreferences Preferences= getSharedPreferences(MY_PREFS_NAME,Context.MODE_PRIVATE);
        String Username=Preferences.getString("UserName", "");
        String email=Preferences.getString("Email","");
        Toast.makeText(this,Username+"and Email"+email, Toast.LENGTH_LONG).show();
        registerForContextMenu(ImageV);
        if(!Username.equalsIgnoreCase("")) {
            UserName.setText(Username.toString());
        }


        db= new DBAdapter(this);
        Cursor cursor = null;
        String Emailid="",Firstnmae="",lastname="",phoneno="";
        try {
            db.open();
            cursor = db.getProfileDetails(Username);
//
            if (cursor.moveToFirst()) {
                do {
                    Toast.makeText(this, "id:" + cursor.getString(0) + "/n" + "User Name:" + cursor.getString(1) + "/n" + "Emailid:" + cursor.getString(2) + "/n" +
                            "First Name:" + cursor.getString(3) + "/n" + "LastName:" + cursor.getString(4) + "/n" + "Phone No" + cursor.getString(5), Toast.LENGTH_LONG).show();
                    DbId=cursor.getLong(0);
                    Emailid =cursor.getString(2);
                    Firstnmae= cursor.getString(3);
                    lastname=cursor.getString(4);
                    phoneno=cursor.getString(5);
                } while (cursor.moveToNext());
            }
            db.close();
        }catch (Exception e)
        {
            Log.e("exception caught",e.getMessage().toString());
            Toast.makeText(this,e.getMessage().toString(),Toast.LENGTH_LONG).show();
        }
        if(Emailid!=null)
            Email.setText(Emailid.toString());
        if(Firstnmae!=null)
            FrstName.setText(Firstnmae.toString());
        if(lastname!=null)
            LstName.setText(lastname.toString());
        if(phoneno!=null)
            PhoneNo.setText(phoneno.toString());


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
                        ImageV.setImageBitmap(decodeUri(selectedImage));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            case CAMERA_REQUEST:
                if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                    Bitmap photo = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    Uri tempUri = getImageUri(getApplicationContext(), photo);

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
                    ImageV.setImageBitmap(photo);


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
}
