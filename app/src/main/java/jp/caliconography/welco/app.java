package jp.caliconography.welco;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import jp.caliconography.welco.model.parseobject.Member;
import jp.caliconography.welco.model.parseobject.Post;

/**
 * Created by abe on 2015/02/08.
 */
public class App extends Application {

    public static final String PARSE_APPLICATION_ID = "Ikzt3vnq6LwIKSb4WDP8RkOcUW3wRlsQuLUlrrFN";
    public static final String PARSE_CLIENT_KEY = "2mBGwz3A3YD8SMo1UuKXD6M9wEl9QOCUkBV1KDf6";

    public void onCreate() {

        ParseObject.registerSubclass(Member.class);
        ParseObject.registerSubclass(Post.class);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
    }
}
