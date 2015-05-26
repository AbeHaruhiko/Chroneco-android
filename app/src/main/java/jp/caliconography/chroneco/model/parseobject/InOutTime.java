package jp.caliconography.chroneco.model.parseobject;

import android.support.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;

/**
 * Created by abe on 2015/05/13.
 */
@ParseClassName("InOutTime")
public class InOutTime extends ParseObject {
    public static final String KEY_OBJECT_ID = "objectId";
    public static final String KEY_CREATEDAT = "createdAt";
    public static final String KEY_MEMBER = "member";
    public static final String KEY_DATE = "date";
    public static final String KEY_IN = "in";
    public static final String KEY_OUT = "out";

    public InOutTime() {

    }

    public InOutTime(@NonNull String memberObjectId, Date date, Date in, Date out) {
        setMember(memberObjectId);
        setDate(date);
        setIn(in);
        setOut(out);
    }

    public static InOutTime createInTime(@NonNull String memberObjectId, @NonNull Date inTime) {
        return new InOutTime(memberObjectId, inTime, inTime, null);
    }

    public static InOutTime createOutTime(@NonNull String memberObjectId, @NonNull Date outTime) {
        return new InOutTime(memberObjectId, outTime, null, outTime);
    }

    /**
     * InOutTimeの最新レコードを取得するためのParseQueryを取得
     *
     * @return InOutTimeの最新レコードを取得するためのParseQuery
     */
    public static ParseQuery<InOutTime> getNewestInOutTimeParseQuery(String memberObjectId) {
        ParseQuery<InOutTime> query = ParseQuery.getQuery(InOutTime.class);
        query.whereEqualTo(InOutTime.KEY_MEMBER, ParseObject.createWithoutData(Member.class, memberObjectId));
        query.addDescendingOrder(InOutTime.KEY_DATE);
        return query;
    }

    public void setMember(@NonNull String memberObjectId) {
        Member member = (Member) ParseObject.createWithoutData("Member", memberObjectId);
        put(KEY_MEMBER, member);
    }

    public Date getDate() {
        return getDate(KEY_DATE);
    }

    public void setDate(@NonNull Date date) {
        put(KEY_DATE, date);
    }

    public Date getIn() {
        return getDate(KEY_IN);
    }

    public void setIn(@NonNull Date date) {
        put(KEY_IN, date);
    }

    public Date getOut() {
        return getDate(KEY_OUT);
    }

    public void setOut(@NonNull Date date) {
        put(KEY_OUT, date);
    }
}
