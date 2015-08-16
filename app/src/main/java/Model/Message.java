package Model;

import android.graphics.Bitmap;

/**
 * Created by Arun on 08-10-2015.
 */
public class Message
{
        private long id;
        private String message;
        private Long userId;
        private Bitmap userImage;
        private String dateTime;

    public Bitmap getuserImage() {
        return userImage;
    }
    public void setuserImage(Bitmap userImage) {
        this.userImage = userImage;
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
        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public String getDate() {
            return dateTime;
        }

        public void setDate(String dateTime) {
            this.dateTime = dateTime;
        }
}
