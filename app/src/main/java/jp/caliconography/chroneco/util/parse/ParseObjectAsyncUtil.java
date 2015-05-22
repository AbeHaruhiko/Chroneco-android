package jp.caliconography.chroneco.util.parse;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

import bolts.Task;

/**
 * Created by abe on 2014/11/15.
 * ParseObject Async Utils
 */
public class ParseObjectAsyncUtil {

    public static Task<ParseObjectAsyncProcResult> deleteAsync(final ParseObject obj) {
        final Task<ParseObjectAsyncProcResult>.TaskCompletionSource task = Task.<ParseObjectAsyncProcResult>create();
        obj.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    task.setResult(new ParseObjectAsyncProcResult(ProcType.PROC_TYPE_DELETE, obj));
                } else {
                    task.setError(e);
                }
            }
        });
        return task.getTask();
    }

    public static Task<ParseObjectAsyncProcResult> saveAsync(final ParseObject obj) {
        final Task<ParseObjectAsyncProcResult>.TaskCompletionSource task = Task.<ParseObjectAsyncProcResult>create();
        obj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    task.setResult(new ParseObjectAsyncProcResult(ProcType.PROC_TYPE_SAVE, obj));
                } else {
                    task.setError(e);
                }
            }
        });
        return task.getTask();
    }

    public static Task<ParseObjectAsyncProcResult> saveAsync(final ParseFile obj) {
        final Task<ParseObjectAsyncProcResult>.TaskCompletionSource task = Task.<ParseObjectAsyncProcResult>create();
        obj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    task.setResult(new ParseObjectAsyncProcResult(ProcType.PROC_TYPE_SAVE, obj));
                } else {
                    task.setError(e);
                }
            }
        });
        return task.getTask();
    }

    public static Task<List<ParseObject>> findAsync(ParseQuery query) {
        final Task<List<ParseObject>>.TaskCompletionSource task = Task.<List<ParseObject>>create();
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List list, ParseException e) {
                if (e == null) {
                    task.setResult(list);
                } else {
                    task.setError(e);
                }
            }

        });
        return task.getTask();
    }

    public static Task<List<ParseObject>> find(ParseQuery query) {
        final Task<List<ParseObject>>.TaskCompletionSource task = Task.<List<ParseObject>>create();
        try {
            task.setResult(query.find());
        } catch (ParseException e) {
            e.printStackTrace();
            task.setError(e);
        }
        return task.getTask();
    }

    public static Task<ParseObject> fetchAsync(final ParseObject obj) {
        final Task<ParseObject>.TaskCompletionSource task = Task.<ParseObject>create();
        if (obj == null) {
            task.setResult(null);
        } else {
            obj.fetchInBackground(new GetCallback() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        task.setResult(parseObject);
                    } else {
                        task.setError(e);
                    }
                }
            });
        }
        return task.getTask();
    }

/*
    public static Task<GraphUser> getFbUser(Activity activity) {
        final Task<GraphUser>.TaskCompletionSource task = Task.<GraphUser>create();

        final Session session = ParseFacebookUtils.getSession();

        if (session != null && session.isOpened()) {
            // If the session is open, make an API call to get user data
            // and define a new callback to handle the response
            final Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        task.setResult(user);
                    }
                    if (response.getError() != null) {
                        task.setError(response.getError().getException());
                    }
                }
            });
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new RequestAsyncTask(request).execute();
                }
            });
        }
        return task.getTask();
    }
*/

    public static Task<byte[]> getDataAsync(final ParseFile obj) {
        final Task<byte[]>.TaskCompletionSource task = Task.<byte[]>create();
        if (obj == null) {
            task.setResult(null);
        } else {
            obj.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        task.setResult(bytes);
                    } else {
                        task.setError(e);
                    }
                }
            });
        }
        return task.getTask();
    }

    public enum ProcType {
        PROC_TYPE_SAVE(0),
        PROC_TYPE_DELETE(1);

        private int id;

        private ProcType(int id) {
            this.id = id;
        }
    }

}