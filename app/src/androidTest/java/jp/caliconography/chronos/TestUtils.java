package jp.caliconography.chronos;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class TestUtils {

    private static final int RESULT_ENABLED = 0;
    private static final int RESULT_DISABLED = 1;
    private static final int RESULT_NG = -1;

    /**
     * EspressoでUIテストを実行する場合は setUp メソッドで無効にし，tearDownで有効にしてください
     *
     * @param context
     * @param TAG
     * @param isEnable
     */
    public static void toggleAnimationEnable(Context context, String TAG, boolean isEnable) {
        int permStatus = context
                .checkCallingOrSelfPermission("android.permission.SET_ANIMATION_SCALE");
        if (permStatus == PackageManager.PERMISSION_GRANTED) {
            int result;
            if ((result = reflectivelyToggleAnimation(TAG, isEnable)) != RESULT_NG) {
                if (result == RESULT_ENABLED) {
                    Log.i(TAG, "All animations enabled.");
                } else {
                    Log.i(TAG, "All animations disabled.");
                }
            } else {
                Log.i(TAG, "Could not toggle animations.");
            }
        } else {
            Log.i(TAG, "Cannot disable animations due to lack of permission.");
        }
    }

    private static int reflectivelyToggleAnimation(String TAG, boolean isEnable) {
        try {
            Class<?> windowManagerStubClazz = Class.forName("android.view.IWindowManager$Stub");
            Method asInterface = windowManagerStubClazz.getDeclaredMethod("asInterface",
                    IBinder.class);
            Class<?> serviceManagerClazz = Class.forName("android.os.ServiceManager");
            Method getService = serviceManagerClazz.getDeclaredMethod("getService", String.class);
            Class<?> windowManagerClazz = Class.forName("android.view.IWindowManager");
            Method setAnimationScales = windowManagerClazz.getDeclaredMethod("setAnimationScales",
                    float[].class);
            Method getAnimationScales = windowManagerClazz.getDeclaredMethod("getAnimationScales");

            IBinder windowManagerBinder = (IBinder) getService.invoke(null, "window");
            Object windowManagerObj = asInterface.invoke(null, windowManagerBinder);
            float[] currentScales = (float[]) getAnimationScales.invoke(windowManagerObj);
            for (int i = 0; i < currentScales.length; i++) {
                if (isEnable) {
                    currentScales[i] = 1.0f;
                } else {
                    currentScales[i] = 0.0f;
                }
            }
            setAnimationScales.invoke(windowManagerObj, currentScales);

            return isEnable ? RESULT_ENABLED : RESULT_DISABLED;
        } catch (ClassNotFoundException cnfe) {
            Log.w(TAG, "Cannot disable animations reflectively.", cnfe);
        } catch (NoSuchMethodException mnfe) {
            Log.w(TAG, "Cannot disable animations reflectively.", mnfe);
        } catch (SecurityException se) {
            Log.w(TAG, "Cannot disable animations reflectively.", se);
        } catch (InvocationTargetException ite) {
            Log.w(TAG, "Cannot disable animations reflectively.", ite);
        } catch (IllegalAccessException iae) {
            Log.w(TAG, "Cannot disable animations reflectively.", iae);
        } catch (RuntimeException re) {
            Log.w(TAG, "Cannot disable animations reflectively.", re);
        }
        return RESULT_NG;
    }

    private TestUtils() {
        // インスタンス化の禁止
    }

}