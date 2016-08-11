package cn.xyz.notracepoint;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by 张政 on 2016/8/11.
 */
public class ActivityLifeManager implements IActivityLifeChange{

    private static ActivityLifeManager manager;
    private List<IActivityLifeChange> lifeChanges = new ArrayList<>();

    public static synchronized ActivityLifeManager getInstance(){
        if(manager == null){
            manager = new ActivityLifeManager();
        }
        return manager;
    }

    private ActivityLifeManager() {
    }

    public void addIActivityLifeChange(IActivityLifeChange lis){
        lifeChanges.add(lis);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        for (IActivityLifeChange lis:lifeChanges){
            lis.onActivityStarted(activity);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        for (IActivityLifeChange lis:lifeChanges){
            lis.onActivityResumed(activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        for (IActivityLifeChange lis:lifeChanges){
            lis.onActivityPaused(activity);
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        for (IActivityLifeChange lis:lifeChanges){
            lis.onActivityDestroyed(activity);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        for (IActivityLifeChange lis:lifeChanges){
            lis.onActivityCreated(activity, savedInstanceState);
        }
    }
}
