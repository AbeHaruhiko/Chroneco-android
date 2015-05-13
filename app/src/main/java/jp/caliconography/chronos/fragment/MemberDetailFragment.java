package jp.caliconography.chronos.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.caliconography.chronos.R;
import jp.caliconography.chronos.activity.MemberDetailAdminActivity;
import jp.caliconography.chronos.activity.MemberListAdminActivity;
import jp.caliconography.chronos.activity.dummy.DummyContent;
import jp.caliconography.chronos.model.parseobject.InOutTime;

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
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MemberDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_member_detail, container, false);

        ButterKnife.inject(this, rootView);

//        Button inButton = (Button) rootView.findViewById(R.id.in);
//        inButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Date now = new Date();
//                InOutTime inTime = new InOutTime(getArguments().getString(ARG_ITEM_ID), now, now, null);
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
        inButton.setEnabled(false);
        Date now = new Date();
        InOutTime inTime = new InOutTime(getArguments().getString(ARG_ITEM_ID), now, now, null);
        inTime.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                inButton.setEnabled(true);
            }
        });
    }

    @OnClick(R.id.out)
    public void onClickOutButton(final Button outButton) {
        outButton.setEnabled(false);
        Date now = new Date();
        InOutTime inTime = new InOutTime(getArguments().getString(ARG_ITEM_ID), now, null, now);
        inTime.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                outButton.setEnabled(true);
            }
        });
    }
}
