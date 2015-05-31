package jp.caliconography.chroneco;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

import jp.caliconography.chroneco.model.parseobject.InOutTime;
import jp.caliconography.chroneco.model.parseobject.Member;
import jp.caliconography.chroneco.model.parseobject.Post;

/**
 * Created by abe on 2015/02/08.
 */
public class App extends Application {

    public static final String PARSE_APPLICATION_ID = "mQeWb7iTmJSAcUjkSGwPT52D8bCJ6jfeevEk8tm6";
    public static final String PARSE_CLIENT_KEY = "IDai8FC4G4QfH94itzYghmucc9LFgoMXrHqXmidB";

    public void onCreate() {

//        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access while disabling public write access.
        defaultACL.setPublicReadAccess(true);
        defaultACL.setRoleWriteAccess("admin", true);
        ParseACL.setDefaultACL(defaultACL, true);

        ParseObject.registerSubclass(Member.class);
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(InOutTime.class);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
    }
}
