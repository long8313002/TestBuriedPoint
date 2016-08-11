package cn.xyz.notracepoint;

import android.app.Activity;
import android.os.Bundle;

/**
 *
 * Created by 张政 on 2016/8/11.
 */
public interface IActivityLifeChange {

    public void onActivityStarted(Activity activity);

    public void onActivityResumed(Activity activity);

    public void onActivityPaused(Activity activity) ;

    public void onActivityDestroyed(Activity activity);

    public void onActivityCreated(Activity activity, Bundle savedInstanceState);
}
