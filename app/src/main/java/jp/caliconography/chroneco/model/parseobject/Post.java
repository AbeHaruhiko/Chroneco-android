package jp.caliconography.chroneco.model.parseobject;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by abe on 2015/02/08.
 */
@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_OBJECT_ID = "objectId";
    public static final String KEY_CREATEDAT = "createdAt";
    public static final String KEY_NAME = "name";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }
}
