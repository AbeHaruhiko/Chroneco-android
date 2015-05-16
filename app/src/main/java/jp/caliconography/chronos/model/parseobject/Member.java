package jp.caliconography.chronos.model.parseobject;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by abe on 2015/02/08.
 */
@ParseClassName("Member")
public class Member extends ParseObject {
    public static final String KEY_OBJECT_ID = "objectId";
    public static final String KEY_CREATEDAT = "createdAt";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_POST = "post";
    public static final String KEY_DISP_ORDER = "dispOrder";
    public static final String KEY_SLACK_PATH = "slackPath";
    public static final String KEY_COMMENT = "comment";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public ParseObject getPost() {
        return getParseObject(KEY_POST);
    }

    public void setPost(ParseObject post) {
        put(KEY_NAME, post);
    }

    public int getDispOrder() {
        return getInt(KEY_DISP_ORDER);
    }

    public void setDispOrder(int order) {
        put(KEY_DISP_ORDER, order);
    }

    public ParseFile getPhotoFile() {
        return getParseFile(KEY_PHOTO);
    }

    public void setPhotoFile(ParseFile file) {
        put(KEY_PHOTO, file);
    }

    public String getSlackPath() {
        return getString(KEY_SLACK_PATH);
    }

    public void setSlackPath(String path) {
        put(KEY_SLACK_PATH, path);
    }

    public String getComment() {
        return getString(KEY_COMMENT);
    }

    public void setComment(String comment) {
        put(KEY_COMMENT, comment);
    }
}
