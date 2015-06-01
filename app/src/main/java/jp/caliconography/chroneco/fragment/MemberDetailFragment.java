package jp.caliconography.chroneco.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
import jp.caliconography.chroneco.util.ToastHelper;
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

    @InjectView(R.id.member_name)
    TextView mMemberName;

    @InjectView(R.id.chokko_chikki)
    Switch mChokkoChokki;

    @InjectView(R.id.real_time)
    TimePicker mTimePicker;

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

        // アイコンロード
//        mIcon.setPlaceholder(getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_square));
        ParseFile photoFile = mMember.getPhotoFile();
        if (photoFile == null) {
            mIcon.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        } else {
            mIcon.setParseFile(mMember.getPhotoFile());
            mIcon.loadInBackground();
        }

        // 名前セット
        mMemberName.setText(mMember.getName());

        return rootView;
    }

    @OnClick(R.id.in)
    public void onClickInButton(final Button inButton) {

        // 現在時刻（今日日付を使ったり、いま時刻を使ったり）
        final Date now = new Date();


        inButton.setEnabled(false);

        // 一覧で選択された社員の最新のInOutTimeレコードを取得する。
        ParseQuery<InOutTime> query = InOutTime.getNewestInOutTimeParseQuery(getArguments().getString(CURRENT_MEMBER_ID));

        getFirstAsync(query).continueWithTask(new Continuation<ParseObject, Task<ParseObjectAsyncProcResult>>() {
            @Override
            public Task<ParseObjectAsyncProcResult> then(Task<ParseObject> task) throws Exception {
                final InOutTime newestRecord = (InOutTime) task.getResult();
                InOutTime inTime = newestRecord;

                // 直行か
                inTime.setChokko(mChokkoChokki.isChecked());

                if (existsSameDateRecord(newestRecord, now)) {
                    // 今日のレコードがある

                    if (mChokkoChokki.isChecked()) {
                        inTime.setIn(getChokkoChokkiDate());
                        inTime.setChokkoDakokuTime(now);
                    } else {
                        inTime.setIn(now);
                    }
                } else {
                    // 今日のレコードがない

                    if (mChokkoChokki.isChecked()) {
                        inTime = InOutTime.createInTime(getArguments().getString(CURRENT_MEMBER_ID),
                                now,
                                getChokkoChokkiDate());
                    } else {
                        inTime = InOutTime.createInTime(getArguments().getString(CURRENT_MEMBER_ID),
                                now);
                    }
                }

                return saveAsync(inTime);
            }

        }).onSuccess(new Continuation<ParseObjectAsyncProcResult, Void>() {
            @Override
            public Void then(Task<ParseObjectAsyncProcResult> task) throws Exception {


                InOutTime inTime = (InOutTime) task.getResult().getProcTarget();

                ToastHelper.makeText(getActivity(), "保存しました。" + getString(R.string.slack_msg_in_out_time,
                                inTime.getDate(),
                                inTime.getIn(),
                                getString(R.string.in)),
                        Toast.LENGTH_LONG).show();

                inButton.setEnabled(true);
                new SlackClient().sendMessage(mMember.getSlackPath(),
                        new SlackClient.SlackMessage(getString(R.string.slack_msg_in_out_time,
                                inTime.getDate(),
                                inTime.getIn(),
                                getString(R.string.in)),
                                getString(R.string.app_name), ":gohst:"));

                return null;
            }
        });
    }

    @OnClick(R.id.out)
    public void onClickOutButton(final Button outButton) {

        // 現在時刻（今日日付を使ったり、いま時刻を使ったり）
        final Date now = new Date();

        outButton.setEnabled(false);

        // 一覧で選択された社員の最新のInOutTimeレコードを取得する。
        ParseQuery<InOutTime> query = InOutTime.getNewestInOutTimeParseQuery(getArguments().getString(CURRENT_MEMBER_ID));

        getFirstAsync(query).continueWithTask(new Continuation<ParseObject, Task<ParseObjectAsyncProcResult>>() {
            @Override
            public Task<ParseObjectAsyncProcResult> then(Task<ParseObject> task) throws Exception {
                final InOutTime newestRecord = (InOutTime) task.getResult();
                InOutTime outTime = newestRecord;

                // 直行・直帰か
                outTime.setChokki(mChokkoChokki.isChecked());

                if (existsSameDateRecord(newestRecord, now)) {
                    // 今日のレコードがある

                    if (mChokkoChokki.isChecked()) {
                        outTime.setOut(getChokkoChokkiDate());
                        outTime.setChokkiDakokuTime(now);
                    } else {
                        outTime.setOut(now);
                    }

                } else {
                    // 今日のレコードがない

                    if (mChokkoChokki.isChecked()) {
                        outTime = InOutTime.createOutTime(getArguments().getString(CURRENT_MEMBER_ID),
                                now,
                                getChokkoChokkiDate());

                    } else {
                        outTime = InOutTime.createOutTime(getArguments().getString(CURRENT_MEMBER_ID),
                                now);
                    }
                }

                return saveAsync(outTime);
            }

        }).onSuccess(new Continuation<ParseObjectAsyncProcResult, Void>() {
            @Override
            public Void then(Task<ParseObjectAsyncProcResult> task) throws Exception {

                InOutTime outTime = (InOutTime) task.getResult().getProcTarget();

                ToastHelper.makeText(getActivity(), "保存しました。" + getString(R.string.slack_msg_in_out_time,
                                outTime.getDate(),
                                outTime.getOut(),
                                getString(R.string.out)),
                        Toast.LENGTH_LONG).show();

                outButton.setEnabled(true);

                new SlackClient().sendMessage(mMember.getSlackPath(),
                        new SlackClient.SlackMessage(getString(R.string.slack_msg_in_out_time,
                                outTime.getDate(),
                                outTime.getOut(),
                                getString(R.string.out)),
                                getString(R.string.app_name), ":gohst:"));

                return null;
            }
        });

    }

    /**
     * 直行の時の実際の出勤時刻、直帰の時の予定退勤時刻
     *
     * @return
     */
    private Date getChokkoChokkiDate() {
        // 直行・直帰時刻
        // TODO: 午前様対応
        Date chokkoChokkiTime = null;
        if (mChokkoChokki.isChecked()) {
            Calendar cal = new GregorianCalendar();
            cal.set(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DATE),
                    mTimePicker.getCurrentHour(),
                    mTimePicker.getCurrentMinute());
            chokkoChokkiTime = cal.getTime();
        }
        return chokkoChokkiTime;
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


}
