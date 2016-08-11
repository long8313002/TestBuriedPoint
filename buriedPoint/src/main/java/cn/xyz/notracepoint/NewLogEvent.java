package cn.xyz.notracepoint;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/8/11.
 */
public class NewLogEvent {

    private Context mContext;

    private NewLogEvent(Context context) {
        mContext = context;
    }

    public static NewLogEvent with(Context context) {
        return new NewLogEvent(context);
    }

    /**
     * 友盟事件统计
     *
     * @param eventId
     */
    public NewLogEvent umengLog(String eventId) {
        return this;
    }

    public NewLogEvent umengLog(String eventId, HashMap<String, String> params) {
        return this;
    }

    /**
     * 谷歌事件统计
     *
     * @param pager
     * @param eventName
     */
    public void googleLog(String pager, String eventName) {
    }
}

