package jp.caliconography.chroneco.model.parseobject;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by abe on 2016/01/12.
 */
@ParseClassName("MailNotification")
public class MailNotification extends ParseObject {
    public static final String KEY_TO_ADDRESS = "toAddress";

    public List<String> getToAddressList() {
        return getList(KEY_TO_ADDRESS);
    }
}
