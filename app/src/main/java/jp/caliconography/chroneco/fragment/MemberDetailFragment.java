package jp.caliconography.chroneco.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Date;

import bolts.Continuation;
import bolts.Task;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import jp.caliconography.chroneco.R;
import jp.caliconography.chroneco.activity.MemberDetailAdminActivity;
import jp.caliconography.chroneco.activity.MemberListAdminActivity;
import jp.caliconography.chroneco.activity.dummy.DummyContent;
import jp.caliconography.chroneco.model.parseobject.InOutTime;
import jp.caliconography.chroneco.model.parseobject.Member;
import jp.caliconography.chroneco.service.SlackClient;
import jp.caliconography.chroneco.util.parse.ParseObjectAsyncProcResult;
import jp.caliconography.chroneco.widget.CircleParseImageView;

import static jp.caliconography.chroneco.util.parse.ParseObjectAsyncUtil.getFirstAsync;
import static jp.caliconography.chroneco.util.parse.ParseObjectAsyncUtil.saveAsync;

/**
 * A fragment representing a single Member detail screen.
 * This fragment is either contained in a {@link MemberListAdminActivity}
 * in two-pane mode (on tablets) or a {@link MemberDetailAdminActivity}
 * on handsets.
 */
public class MemberDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String CURRENT_MEMBER_ID = "item_id";
    //    @InjectView(R.id.comment)
//    TextView mComment;
//
    @InjectView(android.R.id.icon)
    CircleParseImageView mIcon;

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;
    private Member mMember;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MemberDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(CURRENT_MEMBER_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(CURRENT_MEMBER_ID));
            mMember = ParseObject.createWithoutData(Member.class, getArguments().getString(CURRENT_MEMBER_ID));

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_member_detail, container, false);

        ButterKnife.inject(this, rootView);

        mIcon.setParseFile(mMember.getPhotoFile());
        mIcon.loadInBackground();
//        Button inButton = (Button) rootView.findViewById(R.id.in);
//        inButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Date now = new Date();
//                InOutTime inTime = new InOutTime(getArguments().getString(CURRENT_MEMBER_ID), now, now, null);
//                inTime.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                    }
//                });
//            }
//        });

        return rootView;
    }

    @OnClick(R.id.in)
    public void onClickInButton(final Button inButton) {

        final Date now = new Date();

        inButton.setEnabled(false);

        // 一覧で選択された社員の最新のInOutTimeレコードを取得する。
        ParseQuery<InOutTime> query = getNewestInOutTimeParseQuery();
        query.getFirstInBackground(new GetCallback<InOutTime>() {
            @Override
            public void done(InOutTime newestRecord, ParseException e) {

                final SaveCallback inButtonSaveCallback = new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
//                        onClickSaveComment();
                        inButton.setEnabled(true);
                    }
                };

                if (existsSameDateRecord(newestRecord, now)) {
                    // 今日のレコードがある
                    updateInTime(newestRecord, inButtonSaveCallback);
                } else {
                    // 今日のレコードがない
                    createInTime(inButtonSaveCallback);
                }
            }

            private void updateInTime(InOutTime newestRecord, SaveCallback inButtonSaveCallback) {
                newestRecord.setIn(now);
                newestRecord.saveInBackground(inButtonSaveCallback);
            }

            private void createInTime(SaveCallback inButtonSaveCallback) {
                InOutTime inTime = new InOutTime(getArguments().getString(CURRENT_MEMBER_ID), now, now, null);
                inTime.saveInBackground(inButtonSaveCallback);
            }
        });
    }

    @OnClick(R.id.out)
    public void onClickOutButton(final Button outButton) {
        final Date now = new Date();

        outButton.setEnabled(false);

        // 一覧で選択された社員の最新のInOutTimeレコードを取得する。
        ParseQuery<InOutTime> query = getNewestInOutTimeParseQuery();
//        query.getFirstInBackground(new GetCallback<InOutTime>() {
//            @Override
//            public void done(InOutTime newestRecord, ParseException e) {
//
//                final SaveCallback outButtonSaveCallback = new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
////                        onClickSaveComment();
//                        outButton.setEnabled(true);
//                    }
//                };
//
//                if (existsSameDateRecord(newestRecord, now)) {
//                    // 今日のレコードがある
//                    updateOutTime(newestRecord, outButtonSaveCallback);
//                } else {
//                    // 今日のレコードがない
//                    createOutTime(outButtonSaveCallback);
//                }
//            }
//
//            private void updateOutTime(InOutTime newestRecord, SaveCallback inButtonSaveCallback) {
//                newestRecord.setOut(now);
//                newestRecord.saveInBackground(inButtonSaveCallback);
//            }
//
//            private void createOutTime(SaveCallback inButtonSaveCallback) {
//                InOutTime outTime = new InOutTime(getArguments().getString(CURRENT_MEMBER_ID), now, null, now);
//                outTime.saveInBackground(inButtonSaveCallback);
//            }
//        });

        getFirstAsync(query).continueWithTask(new Continuation<ParseObject, Task<ParseObjectAsyncProcResult>>() {
            @Override
            public Task<ParseObjectAsyncProcResult> then(Task<ParseObject> task) throws Exception {
                final InOutTime newestRecord = (InOutTime) task.getResult();
                InOutTime inTime = newestRecord;
                if (existsSameDateRecord(newestRecord, now)) {
                    // 今日のレコードがある
                    updateOutTime(newestRecord);
                } else {
                    // 今日のレコードがない
                    inTime = createOutTime();
                }

                return saveAsync(inTime);
            }

            private void updateOutTime(InOutTime newestRecord) {
                newestRecord.setOut(now);
            }

            private InOutTime createOutTime() {
                return new InOutTime(getArguments().getString(CURRENT_MEMBER_ID), now, null, now);
            }

        }).onSuccess(new Continuation<ParseObjectAsyncProcResult, Void>() {
            @Override
            public Void then(Task<ParseObjectAsyncProcResult> task) throws Exception {

                outButton.setEnabled(true);

                // TODO: fetch???
                new SlackClient().sendMessage(mMember.getSlackPath(),
                        new SlackClient.SlackMessage(getString(R.string.slack_msg_guest_has_come),
                                getString(R.string.app_name), ":gohst:"));

                return null;
            }
        });

    }

//    @OnClick(R.id.save_comment)
//    public void onClickSaveComment() {
//        mMember.setComment(mComment.getText().toString());
//        mMember.saveInBackground();
//    }

    /**
     * 1件以上のレコードが存在している && そのレコードは今日のレコード
     * @param newestRecord
     * @param now
     * @return
     */
    private boolean existsSameDateRecord(InOutTime newestRecord, Date now) {
        return newestRecord != null && isSameDate(newestRecord.getDate(InOutTime.KEY_DATE), now);
    }

    /**
     * 同じ日か判定する
     * @param date1
     * @param date2
     * @return 同じ日の場合 true
     */
    private boolean isSameDate(Date date1, Date date2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateString1 = dateFormat.format(date1);
        String dateString2 = dateFormat.format(date2);
        return dateString1.equals(dateString2);
    }

    /**
     * InOutTimeの最新レコードを取得するためのParseQueryを取得
     * @return InOutTimeの最新レコードを取得するためのParseQuery
     */
    private ParseQuery<InOutTime> getNewestInOutTimeParseQuery() {
        ParseQuery<InOutTime> query = ParseQuery.getQuery(InOutTime.class);
        query.whereEqualTo(InOutTime.KEY_MEMBER, ParseObject.createWithoutData(Member.class, getArguments().getString(CURRENT_MEMBER_ID)));
        query.addDescendingOrder(InOutTime.KEY_DATE);
        return query;
    }

}
