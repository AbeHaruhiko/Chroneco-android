package jp.caliconography.welco.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import jp.caliconography.welco.R;
import jp.caliconography.welco.model.parseobject.Member;

public class MemberAdapter extends ParseQueryAdapter<Member> {

    public static final float TRANSPARENT = 0f;
    public static final float OPAQUE = 1f;
    public static final String ALPHA = "alpha";
    public static final int FADEIN_DURATION = 400;

    public MemberAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Member>() {
            public ParseQuery<Member> create() {
                ParseQuery query = new ParseQuery(Member.class);
                query.addAscendingOrder(Member.KEY_DISP_ORDER);
                return query;
            }
        });
//
//        this.setTextKey(Member.KEY_NAME);
//        this.setImageKey(Member.KEY_PHOTO);
    }

    @Override
    public View getItemView(Member member, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.member_list, null);
        }
        super.getItemView(member, convertView, parent);

        TextView descriptionView = (TextView) convertView.findViewById(R.id.member_name);
        descriptionView.setText(member.getName());

        // フェイドインアニメーション
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(convertView, ALPHA, TRANSPARENT, OPAQUE);
        objectAnimator.setDuration(FADEIN_DURATION);
        objectAnimator.start();

        return convertView;
    }
}