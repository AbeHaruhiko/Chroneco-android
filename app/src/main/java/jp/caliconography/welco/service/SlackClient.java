package jp.caliconography.welco.service;

import android.util.Log;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by abe on 2015/02/14.
 */
public class SlackClient {

    private static final String TAG = SlackClient.class.getSimpleName();

    private static final String HTTPS_HOOKS_SLACK_COM = "https://hooks.slack.com";

    public interface SlackService {
        @POST("/services/{path1}/{path2}/{path3}")
        void sendMessage(@Path("path1") String path1,
                         @Path("path2") String path2,
                         @Path("path3") String path3,
                         @Body SlackMessage message,
                         Callback<Response> callback);
    }

    public void sendMessage(String path, SlackMessage message) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(HTTPS_HOOKS_SLACK_COM)
                .build();

        SlackClient.SlackService service = restAdapter.create(SlackClient.SlackService.class);
        service.sendMessage(path.split("/")[0], path.split("/")[1], path.split("/")[2], message, getCallback());
    }

    private Callback<Response> getCallback() {
        return new Callback<Response>() {
            @Override
            public void success(Response result, Response response) {
                Log.i(TAG, "succeed.");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, null, retrofitError);
            }
        };
    }

    public static class SlackMessage {
        /* Gsonはクラス名は自由、変数名をJSONのKeyにすること */
        public String text;
        public String username;
        public String user_emoji;

        public SlackMessage(String text, String username, String user_emoji) {
            this.text = text;
            this.username = username;
            this.user_emoji = user_emoji;
        }
    }
}
