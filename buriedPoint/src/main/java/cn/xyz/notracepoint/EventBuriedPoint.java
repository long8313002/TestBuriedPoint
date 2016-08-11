package cn.xyz.notracepoint;

import android.text.TextUtils;

import java.util.Arrays;

/**
 * Created by Administrator on 2016/8/11.
 */
public class EventBuriedPoint {
    private String describe;
    private String id;
    private String activity;
    private String tag = "";
    private String number;//编号

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public EventBuriedPoint() {
    }

    public EventBuriedPoint(String id, String activity, String tag) {
        this.id = id;
        this.activity = activity;
        this.tag = tag;
    }

    @Override
    public boolean equals(Object target) {
        if (target instanceof EventBuriedPoint) {
            EventBuriedPoint eventTarget = (EventBuriedPoint) target;
            if ((id).equals(eventTarget.id)) {
                if (!TextUtils.isEmpty(activity)) {
                    if (activity.contains("|") && !eventTarget.activity.equals(activity)) {
                        return false;
                    }
                    String[] splitActivity = eventTarget.activity.split("\\|");
                    if (!activity.contains("|") && !Arrays.asList(splitActivity).contains(activity)) {
                        return false;
                    }
                }

                if (TextUtils.isEmpty(tag)) {
                    return true;
                }
                if (eventTarget.tag.contains(tag)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return activity+"_"+id+"_"+tag;
    }
}

