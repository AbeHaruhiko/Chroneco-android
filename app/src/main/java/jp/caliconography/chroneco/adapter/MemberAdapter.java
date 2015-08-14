package jp.caliconography.chroneco.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.caliconography.chroneco.R;
import jp.caliconography.chroneco.model.parseobject.Member;

public class MemberAdapter extends ParseQueryAdapter<Member> {

    public static final float TRANSPARENT = 0f;
    public static final float OPAQUE = 1f;
    public static final String ALPHA = "alpha";
    public static final int FADEIN_DURATION = 400;

    public MemberAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Member>() {
            public ParseQuery<Member> create() {
                ParseQuery query = new ParseQuery(Member.class);
                query.whereGreaterThan(Member.KEY_DISP_ORDER, 0);
                query.addAscendingOrder(Member.KEY_DISP_ORDER);
                return query;
            }
        });
        this.setPlaceholder(context.getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_square));

        this.setTextKey(Member.KEY_NAME);

        this.setObjectsPerPage(30);
    }

    @Override
    public View getItemView(Member member, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.member_list, null);
        }
        super.getItemView(member, convertView, parent);

        TextView time = (TextView) convertView.findViewById(R.id.in_or_out_time);
        TextView comment = (TextView) convertView.findViewById(R.id.comment);
        TextView name = (TextView) convertView.findViewById(R.id.member_name);
        CircleImageView icon = (CircleImageView) convertView.findViewById(android.R.id.icon);

        comment.setText(member.getComment());
        name.setText(member.getName());

        ParseFile iconFile = member.getPhotoFile();
        if (iconFile == null) {
            // アイコンが登録されていない場合はプレースホルダを表示。
            icon.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        } else {
//            icon.setParseFile(iconFile);
//            icon.loadInBackground();
            Picasso.with(getContext())
                    .load(iconFile.getUrl())
                    .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                    .into(icon);
        }

        // フェイドインアニメーション
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(convertView, ALPHA, TRANSPARENT, OPAQUE);
        objectAnimator.setDuration(FADEIN_DURATION);
        objectAnimator.start();

        return convertView;
    }
}