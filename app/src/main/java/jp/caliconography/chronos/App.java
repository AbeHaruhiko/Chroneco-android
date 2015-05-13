package jp.caliconography.chronos;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import jp.caliconography.chronos.model.parseobject.InOutTime;
import jp.caliconography.chronos.model.parseobject.Member;
import jp.caliconography.chronos.model.parseobject.Post;

/**
 * Created by abe on 2015/02/08.
 */
public class App extends Application {

    public static final String PARSE_APPLICATION_ID = "mQeWb7iTmJSAcUjkSGwPT52D8bCJ6jfeevEk8tm6";
    public static final String PARSE_CLIENT_KEY = "IDai8FC4G4QfH94itzYghmucc9LFgoMXrHqXmidB";

    public void onCreate() {

        ParseObject.registerSubclass(Member.class);
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(InOutTime.class);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
    }
}
