package cn.xyz.notracepoint;

import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * Created by 张政 on 2016/8/11.
 */
public class NOTracePointUtils {

    /**
     * 替换系统默认的Instrumentation
     */
    public static void replaceInstrumentation() {
        Class<?> activityThreadClass;
        try {
            //加载activity thread的class
            activityThreadClass = Class.forName("android.app.ActivityThread");

            //找到方法currentActivityThread
            Method method = activityThreadClass.getDeclaredMethod("currentActivityThread");
            //由于这个方法是静态的，所以传入Null就行了
            Object currentActivityThread = method.invoke(null);

            //把之前ActivityThread中的mInstrumentation替换成我们自己的
            Field field = activityThreadClass.getDeclaredField("mInstrumentation");
            field.setAccessible(true);
            Instrumentation instrumentation = (Instrumentation) field.get(currentActivityThread);
            InstrumentationProxy instrumentationProxy = new InstrumentationProxy(instrumentation);
            field.set(currentActivityThread, instrumentationProxy);
        } catch (Exception e) {

        }
    }


    /**获得所有正在显示的窗口View*/
    public static List<View> getAllWindowViews(){
        List<View> views = new ArrayList<>();
        try {
            Class<?> aClass = Class.forName("android.view.WindowManagerGlobal");
            Method getInstance = aClass.getMethod("getInstance");
            Object invoke = getInstance.invoke(null);
            Field mRoots = aClass.getDeclaredField("mViews");
            mRoots.setAccessible(true);
            Object oViews = mRoots.get(invoke);

            if (oViews instanceof ArrayList) {
                views.addAll((ArrayList<View>) mRoots.get(invoke));
            } else {
                View[] arrayViews = (View[]) mRoots.get(invoke);
                views.addAll(Arrays.asList(arrayViews));
            }
        } catch (Exception e) {
        }
        return views;
    }

    public static int getAppVersionCode(Context context) {
        int versionCode=-1;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionCode;
    }

    public static String inputStream2String(InputStream in) {
        return inputStream2String(in,"UTF-8");
    }

    public static String inputStream2String(InputStream in,String decode) {
        try {
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[4096];
            for (int n; (n = in.read(b)) != -1; ) {
                out.append(new String(b, 0, n,decode));
            }
            return out.toString();
        } catch (Exception e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        return "";
    }

    /**
     *获得当前View中所有正在显示的文字
     */
    public static String getViewDescribe(View view) {
        StringBuilder describe = new StringBuilder();
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                String des = getViewDescribe(group.getChildAt(i));
                describe.append(des);
            }
        }

        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            describe.append(tv.getText().toString());
            return describe.toString();
        }
        return describe.toString();

    }
}
