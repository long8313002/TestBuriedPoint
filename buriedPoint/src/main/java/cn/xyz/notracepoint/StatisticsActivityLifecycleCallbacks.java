package cn.xyz.notracepoint;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 *
 * Created by 张政 on 2016/8/11.
 */
public class StatisticsActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        ActivityLifeManager.getInstance().onActivityCreated(activity,savedInstanceState);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        ActivityLifeManager.getInstance().onActivityResumed(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ActivityLifeManager.getInstance().onActivityPaused(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ActivityLifeManager.getInstance().onActivityDestroyed(activity);
    }
}
