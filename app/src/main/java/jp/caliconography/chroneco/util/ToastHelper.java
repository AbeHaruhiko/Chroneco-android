package jp.caliconography.chroneco.util;

import android.content.Context;
import android.widget.Toast;

public class ToastHelper extends Toast {
    static Toast sToast = null;

    public ToastHelper(Context context) {
        super(context);
    }

    public static void setToast(Toast toast) {
        if (sToast != null)
            sToast.cancel();
        sToast = toast;
    }

    public static void cancelToast() {
        if (sToast != null)
            sToast.cancel();
        sToast = null;
    }

    @Override
    public void show() {
        ToastHelper.setToast(this);
        super.show();
    }
}