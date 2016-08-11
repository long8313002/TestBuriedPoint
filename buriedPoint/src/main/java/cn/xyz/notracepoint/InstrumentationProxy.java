package cn.xyz.notracepoint;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Bundle;

/**
 *
 * Created by 张政 on 2016/8/11.
 */
 class InstrumentationProxy extends Instrumentation{

    private Instrumentation m_base;

    public InstrumentationProxy(Instrumentation base) {
        m_base = base;
    }


    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        super.callActivityOnCreate(activity, icicle);
        ActivityLifeManager.getInstance().onActivityCreated(activity,icicle);
    }


    @Override
    public void callActivityOnPause(Activity activity) {
        super.callActivityOnPause(activity);
        ActivityLifeManager.getInstance().onActivityPaused(activity);
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        super.callActivityOnDestroy(activity);
        ActivityLifeManager.getInstance().onActivityDestroyed(activity);
    }


    @Override
    public void callActivityOnResume(Activity activity) {
        super.callActivityOnResume(activity);
        ActivityLifeManager.getInstance().onActivityResumed(activity);
    }

    @Override
    public void callActivityOnStart(Activity activity) {
        super.callActivityOnStart(activity);
        ActivityLifeManager.getInstance().onActivityStarted(activity);
    }
}
