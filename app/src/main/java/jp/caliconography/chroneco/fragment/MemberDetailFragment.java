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

import com.google.common.collect.Range;
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
import jp.caliconography.chroneco.util.Utils;
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

    @InjectView(R.id.in)
    Button mIn;

    @InjectView(R.id.out)
    Button mOut;

    @InjectView(R.id.chokko_chikki)
    Switch mChokkoChokki;

    @InjectView(R.id.real_time)
    TimePicker mTimePicker;

    @InjectView(R.id.real_time_is_unknown)
    Switch mRealTimeIsUnknown;

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

        // ボタンの制御（その日クリック済みなら押せないように）
        ParseQuery<InOutTime> query = InOutTime.getNewestInOutTimeParseQuery(getArguments().getString(CURRENT_MEMBER_ID));

        getFirstAsync(query).continueWith(new Continuation<ParseObject, Void>() {
            @Override
            public Void then(Task<ParseObject> task) throws Exception {
                final InOutTime newestRecord = (InOutTime) task.getResult();

                // 現在時刻
                final Date now = new Date();

                if (existsSameDateRecord(newestRecord, now)) {
                    // 今日のレコードがある

                    if (newestRecord.getIn() == null) {
                        // 出勤済み
                        mIn.setEnabled(true);
                    }

                    if (newestRecord.getOut() == null) {
                        // 退勤済み
                        mOut.setEnabled(true);
                    }
                } else {
                    mIn.setEnabled(true);
                    mOut.setEnabled(true);
                }
                return null;
            }
        });


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

        // 24時間表示
        mTimePicker.setIs24HourView(true);

        return rootView;
    }

    @OnClick(R.id.in)
    public void onClickInButton(final Button inButton) {

        // ネットワークに接続できないとき
        if (Utils.isOffline(getActivity())) {
            ToastHelper.makeText(getActivity(), getString(R.string.network_disconncted), Toast.LENGTH_LONG).show();
            return;
        }

        // 現在時刻（今日日付を使ったり、いま時刻を使ったり）
        final Date now = new Date();


        inButton.setEnabled(false);

        // 一覧で選択された社員の最新のInOutTimeレコードを取得する。
        final ParseQuery<InOutTime> query = InOutTime.getNewestInOutTimeParseQuery(getArguments().getString(CURRENT_MEMBER_ID));

        getFirstAsync(query).continueWithTask(new Continuation<ParseObject, Task<ParseObjectAsyncProcResult>>() {
            @Override
            public Task<ParseObjectAsyncProcResult> then(Task<ParseObject> task) throws Exception {
                final InOutTime newestRecord = (InOutTime) task.getResult();

                final InOutTime inTime = existsSameDateRecord(newestRecord, now) ?
                        newestRecord
                        : new InOutTime(getArguments().getString(CURRENT_MEMBER_ID), now, null, null);    /* 対象日が今日のデータ作成 */

                inTime.setIn(now);

                // 直行か
                inTime.setChokko(mChokkoChokki.isChecked());

                if (mChokkoChokki.isChecked()) {
                    // 直行なら打刻時刻をセット
                    inTime.setChokkoDakokuTime(now);
                    // 直行の時は、TimePickerの指定時刻を出勤時刻とする。（nowを上書き）
                    inTime.setIn(getChokkoChokkiDate());
                }

                return saveAsync(inTime);
            }

        }).onSuccess(new Continuation<ParseObjectAsyncProcResult, Void>() {
            @Override
            public Void then(Task<ParseObjectAsyncProcResult> task) throws Exception {


                InOutTime inTime = (InOutTime) task.getResult().getProcTarget();

                ToastHelper.makeText(getActivity(), getString(R.string.saved) + getString(R.string.slack_msg_in_out_time,
                                inTime.getDate(),
                                inTime.getIn(),
                                getString(R.string.in)),
                        Toast.LENGTH_LONG).show();

                new SlackClient().sendMessage(mMember.getSlackPath(),
                        new SlackClient.SlackMessage(getString(R.string.slack_msg_in_out_time,
                                inTime.getDate(),
                                inTime.getIn(),
                                getString(R.string.in)),
                                getString(R.string.app_name), getString(R.string.slack_icon)));

                return null;
            }
        });
    }

    @OnClick(R.id.out)
    public void onClickOutButton(final Button outButton) {

        // ネットワークに接続できないとき
        if (Utils.isOffline(getActivity())) {
            ToastHelper.makeText(getActivity(), getString(R.string.network_disconncted), Toast.LENGTH_LONG).show();
            return;
        }

        // 現在時刻（今日日付を使ったり、いま時刻を使ったり）
        final Date now = new Date();

        outButton.setEnabled(false);

        // 一覧で選択された社員の最新のInOutTimeレコードを取得する。
        final ParseQuery<InOutTime> query = InOutTime.getNewestInOutTimeParseQuery(getArguments().getString(CURRENT_MEMBER_ID));

        getFirstAsync(query).continueWithTask(new Continuation<ParseObject, Task<ParseObjectAsyncProcResult>>() {
            @Override
            public Task<ParseObjectAsyncProcResult> then(Task<ParseObject> task) throws Exception {
                final InOutTime newestRecord = (InOutTime) task.getResult();

                // 午前様対応
                // 0:00~9:00の退勤は前日の退勤として扱う。
                final Calendar cal = Calendar.getInstance();
                cal.setTime(now);
                cal.add(Calendar.DATE, -1);
                final Date yesterday = cal.getTime();

                final Date targetDate = Range.closed(0, 9).contains(cal.get(Calendar.HOUR_OF_DAY)) ? yesterday : now;

                final InOutTime outTime = existsSameDateRecord(newestRecord, targetDate) ?
                        newestRecord
                        : new InOutTime(getArguments().getString(CURRENT_MEMBER_ID), targetDate, null, null);    /* 対象日が今日のデータ作成 */

                outTime.setOut(now);

                // 直行・直帰か
                outTime.setChokki(mChokkoChokki.isChecked());

                if (mChokkoChokki.isChecked()) {
                    // 直行なら打刻時刻をセット
                    outTime.setChokkiDakokuTime(now);
                    // 直帰の時は、TimePickerの指定時刻を退勤時刻とする。（nowを上書き）
                    outTime.setOut(getChokkoChokkiDate());
                }

                return saveAsync(outTime);
            }

        }).onSuccess(new Continuation<ParseObjectAsyncProcResult, Void>() {
            @Override
            public Void then(Task<ParseObjectAsyncProcResult> task) throws Exception {

                final InOutTime outTime = (InOutTime) task.getResult().getProcTarget();

                ToastHelper.makeText(getActivity(), getString(R.string.saved) + getString(R.string.slack_msg_in_out_time,
                                outTime.getDate(),
                                outTime.getOut(),
                                getString(R.string.out)),
                        Toast.LENGTH_LONG).show();

                new SlackClient().sendMessage(mMember.getSlackPath(),
                        new SlackClient.SlackMessage(getString(R.string.slack_msg_in_out_time,
                                outTime.getDate(),
                                outTime.getOut(),
                                getString(R.string.out)),
                                getString(R.string.app_name), getString(R.string.slack_icon)));

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
