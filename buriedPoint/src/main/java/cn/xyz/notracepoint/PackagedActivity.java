package cn.xyz.notracepoint;

import android.app.Activity;

import java.lang.ref.SoftReference;

/**
 * Created by Administrator on 2016/8/11.
 */
public class PackagedActivity {

    public interface OnPausedCall {
        void call();
    }

    public interface OnDestroyedCall {
        void call();
    }

    private SoftReference<Activity> activity;
    private String pagerTag;
    private OnPausedCall onPausedCall;
    private OnDestroyedCall onDestroyedCall;

    public Activity getActivity() {
        return activity.get();
    }

    public void setActivity(Activity activity) {
        this.activity = new SoftReference<>(activity);
    }

    public String getPagerTag() {
        return pagerTag;
    }

    public void setPagerTag(String pagerTag) {
        this.pagerTag = pagerTag;
    }

    public OnPausedCall getOnPausedCall() {
        return onPausedCall;
    }

    public void setOnPausedCall(OnPausedCall onPausedCall) {
        this.onPausedCall = onPausedCall;
    }

    public OnDestroyedCall getOnDestroyedCall() {
        return onDestroyedCall;
    }

    public void setOnDestroyedCall(OnDestroyedCall onDestroyedCall) {
        this.onDestroyedCall = onDestroyedCall;
    }

    public void notifyOnPaused() {
        if (onPausedCall != null) {
            onPausedCall.call();
        }
    }

    public void notifyOnDestroyed() {
        if (onDestroyedCall != null) {
            onDestroyedCall.call();
        }
    }

}
