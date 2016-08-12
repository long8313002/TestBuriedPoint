package cn.xyz.notracepoint;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * 埋点的入门类
 * Created by 张政 on 2016/8/11.
 */
public class Statistics implements IActivityLifeChange {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UserProxyTouchPattern {
    }

    private static Statistics statistics;
    private Application application;
    private ABSConfigure configure;
    private SparseArray<PackagedActivity> packagedActivityHashMap = new SparseArray<>();

    public static void init(Application application, ABSConfigure configure) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {//4.0以上
            application.registerActivityLifecycleCallbacks(new StatisticsActivityLifecycleCallbacks());
        } else {
            NOTracePointUtils.replaceInstrumentation();
        }

        statistics = new Statistics(application, configure);
        ActivityLifeManager.getInstance().addIActivityLifeChange(statistics);
        EventLogManager.getInstence().loadLogFileToLocal();
    }

    public static Statistics getInstance() {
        if (statistics == null) {
            throw new RuntimeException("请首先调用init方法");
        }
        return statistics;
    }

    public Context getContext() {
        return application;
    }

    public ABSConfigure getConfigure() {
        return configure;
    }

    private Statistics(Application application, ABSConfigure configure) {
        this.application = application;
        this.configure = configure;
    }


    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (packagedActivityHashMap.indexOfKey(activity.hashCode()) > 0) {
            return;
        }
        PackagedActivity packagedActivity = new PackagedActivity();
        packagedActivity.setActivity(activity);
        packagedActivityHashMap.put(activity.hashCode(), packagedActivity);
        if (isUserProxyTouch(activity)) {
            View decorView = getRootView(activity.getWindow());
            ProxyDecorView proxyDecorView = new ProxyDecorView(decorView);
            setWindowProxyDecorView(activity.getWindow(),proxyDecorView);
        } else {
            ProxyListenerControl.getInstance().init(packagedActivity);
        }
    }

    private void setWindowProxyDecorView(Window window, ProxyDecorView proxyDecorView) {
        Class aClass = window.getClass();
        while (aClass != null) {
            try {
                Field mContentParent = aClass.getDeclaredField("mContentParent");
                if(mContentParent == null){
                    aClass = aClass.getSuperclass();
                    continue;
                }
                mContentParent.setAccessible(true);
                mContentParent.set(window,proxyDecorView);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                aClass = aClass.getSuperclass();
            }
        }
    }

    private View getRootView(Window window){
        Class aClass = window.getClass();
        while (aClass != null) {
            try {
                Field mContentParent = aClass.getDeclaredField("mContentParent");
                if(mContentParent == null){
                    aClass = aClass.getSuperclass();
                    continue;
                }
                mContentParent.setAccessible(true);
                return (View) mContentParent.get(window);
            } catch (Exception e) {
                e.printStackTrace();
                aClass = aClass.getSuperclass();
            }
        }
        return null;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        PackagedActivity packagedActivity = packagedActivityHashMap.get(activity.hashCode());
        if (packagedActivity != null) {
            packagedActivity.notifyOnPaused();
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        PackagedActivity packagedActivity = packagedActivityHashMap.get(activity.hashCode());
        if (packagedActivity != null) {
            packagedActivity.notifyOnDestroyed();
        }
        packagedActivityHashMap.remove(activity.hashCode());
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    public boolean isUserProxyTouch(Activity activity) {
        UserProxyTouchPattern annotation = getAnnotation(activity, UserProxyTouchPattern.class);
        return annotation != null ;
    }

    private static <T extends Annotation> T getAnnotation(Object position, Class<T> cla) {
        Class<?> aClass = position.getClass();
        T annotation = null;
        while (annotation == null) {
            annotation = aClass.getAnnotation(cla);
            aClass = aClass.getSuperclass();
            if (aClass == null) {
                return annotation;
            }
        }

        return annotation;
    }
}
