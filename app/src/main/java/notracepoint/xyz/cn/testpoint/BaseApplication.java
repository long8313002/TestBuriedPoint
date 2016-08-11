package notracepoint.xyz.cn.testpoint;

import android.app.Application;
import android.content.Context;

import cn.xyz.notracepoint.Statistics;

/**
 * Created by Administrator on 2016/8/12.
 */
public class BaseApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Statistics.init(this,new TracePointConfigure());
    }
}
