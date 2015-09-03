package Model;

import android.graphics.Bitmap;

/**
 * Created by Arun on 08-10-2015.
 */
public class Message
{
        private long id;
        private int type;
        private String message;
        private int adminId;
        private String adminName;
        private Bitmap adminImage;
        private String dateTime;
    public int getType()
    {
        return type;
    }
    public void setType(int Type)
    {
        this.type = Type;
    }
    public String getAdminName()
    {
        return adminName;
    }
    public void setAdminName(String adminname)
    {
        this.adminName = adminname;
    }
    public Bitmap getAdminImage()
    {
        return adminImage;
    }
    public void setAdminImage(Bitmap userImage) {
        this.adminImage = userImage;
    }
        public long getId() {
            return id;
        }
        public void setId(long id) {
            this.id = id;
        }
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public int getAdminId() {
            return adminId;
        }

        public void setAdminId(int userId) {
            this.adminId = userId;
        }

        public String getDate() {
            return dateTime;
        }

        public void setDate(String dateTime) {
            this.dateTime = dateTime;
        }

}
