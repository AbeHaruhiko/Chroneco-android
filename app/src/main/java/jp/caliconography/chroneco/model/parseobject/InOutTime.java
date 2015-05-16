package jp.caliconography.chroneco.model.parseobject;

import com.parse.ParseClassName;
import com.parse.ParseObject;

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

    public InOutTime(String memberObjectId, Date date, Date in, Date out) {
        setMember(memberObjectId);
        setDate(date);
        setIn(in);
        setOut(out);
    }

    public void setMember(String memberObjectId) {
        Member member = (Member) ParseObject.createWithoutData("Member", memberObjectId);
        put(KEY_MEMBER, member);
    }

    public void setDate(Date date) {
        if (date != null) {
            put(KEY_DATE, date);
        }
    }

    public void setIn(Date date) {
        if (date != null) {
            put(KEY_IN, date);
        }
    }

    public void setOut(Date date) {
        if (date != null) {
            put(KEY_OUT, date);
        }
    }
}
