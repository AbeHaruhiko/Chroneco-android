package jp.caliconography.chroneco.service;


import android.util.Log;

import com.parse.ConfigCallback;
import com.parse.GetCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.mail.internet.InternetAddress;

import bolts.Capture;
import bolts.Continuation;
import bolts.Task;
import bolts.TaskCompletionSource;
import jp.caliconography.chroneco.model.parseobject.MailNotification;
import jp.caliconography.chroneco.model.parseobject.Member;

/**
 * Created by abe on 2016/01/09.
 */
public class MailSender {

    private static final String TAG = MailSender.class.getSimpleName();

    public void send(final Member member, final String subject, final String body) {

        final Capture<ParseConfig> config = new Capture<>();

        getParseConfigAsync().onSuccessTask(new Continuation<ParseConfig, Task<MailNotification>>() {
            @Override
            public Task<MailNotification> then(Task<ParseConfig> task) throws Exception {
                config.set(task.getResult());
                return getToAddress(member);
            }
        })

                .onSuccessTask(new Continuation<MailNotification, Task<Void>>() {
                    @Override
                    public Task<Void> then(Task<MailNotification> task) throws Exception {

                        final ArrayList<InternetAddress> toList = new ArrayList<InternetAddress>();
                        if (task.getResult() == null || task.getResult().getToAddressList() == null || task.getResult().getToAddressList().isEmpty()) {
                            return null;
                        } else {
                            for (String toAddress : task.getResult().getToAddressList()) {
                                toList.add(new InternetAddress(toAddress));
                            }
                        }


                        Task.callInBackground(new Callable<Void>() {
                            public Void call() {

                                try {
                                    Email email = new SimpleEmail();
                                    email.setHostName(config.get().getString("mailServer"));
                                    email.setSmtpPort(config.get().getInt("mailPort"));
                                    email.setAuthenticator(new DefaultAuthenticator(config.get().getString("mailFrom"), config.get().getString("mailPassword")));
                                    email.setFrom(config.get().getString("mailFrom"));
                                    email.setSubject(subject);
                                    email.setMsg(body);
                                    email.setTo(toList);
                                    email.send();
                                } catch (EmailException e) {
                                    e.printStackTrace();
                                    Log.e(TAG, "error on sending mail.", e);
                                } finally {
                                    Log.d(TAG, "finally block on mail sender.");
                                }
                                return null;
                            }
                        });
                        return null;
                    }
                });
    }

    private Task<ParseConfig> getParseConfigAsync() {
        final TaskCompletionSource<ParseConfig> tcs = new TaskCompletionSource<>();
        ParseConfig.getInBackground(new ConfigCallback() {
            @Override
            public void done(ParseConfig parseConfig, ParseException e) {

                if (e == null) {
                    tcs.setResult(parseConfig);
                } else {
                    tcs.setResult(ParseConfig.getCurrentConfig());
                }
            }
        });
        return tcs.getTask();
    }

    private Task<MailNotification> getToAddress(Member member) {
        final TaskCompletionSource<MailNotification> tcs = new TaskCompletionSource<>();
        ParseQuery<MailNotification> query = ParseQuery.getQuery("MailNotification");
        query.whereEqualTo("member", member);
        try {
            query.getFirstInBackground(new GetCallback<MailNotification>() {
                @Override
                public void done(MailNotification mailNotification, ParseException e) {

                    if (e == null) {
                        tcs.setResult(mailNotification);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
        return tcs.getTask();
    }
}
