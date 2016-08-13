package cn.xyz.notracepoint;

import android.app.Activity;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Administrator on 2016/8/11.
 */
public class ProxyListenerControl {
    private static ProxyListenerControl control = new ProxyListenerControl();
    public static final String TAG = "ProxyEvent";
    private PackagedActivity mActivity;
    private List<Future<?>> proxyPeddings = new ArrayList<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private SparseBooleanArray hasProxyViews = new SparseBooleanArray();

    public static ProxyListenerControl getInstance() {
        return control;
    }

    public void init(PackagedActivity activity) {
        this.mActivity = activity;
        if (mActivity.getActivity() == null) {
            return;
        }
        registerEvent(mActivity.getActivity().getWindow().getDecorView());
        initDialogMonitor(activity);
        registerDestoryActivity(activity);
    }

    public void registerEvent(final View rootView) {
        proxyPeddings.add(executorService.submit(new Runnable() {
            @Override
            public void run() {
                proxy(rootView);
            }
        }));
    }

    private void registerDestoryActivity(PackagedActivity mActivity) {
        mActivity.setOnDestroyedCall(new PackagedActivity.OnDestroyedCall() {
            @Override
            public void call() {
                cancle();
            }
        });
    }

    private void cancle() {
        try {
            cancelFuture();
        } catch (Exception ignored) {
        }
    }

    private void cancelFuture() throws Exception {
        for (Future future : proxyPeddings) {
            future.cancel(true);
        }
    }


    private void initDialogMonitor(PackagedActivity mActivity) {
        mActivity.setOnPausedCall(new PackagedActivity.OnPausedCall() {
            @Override
            public void call() {
                onWindowFourceChange();
            }
        });
    }

    //窗口改变时 为DIalog设置代理
    private void onWindowFourceChange() {

        List<View> allWindowViews = NOTracePointUtils.getAllWindowViews();
        if (allWindowViews == null || allWindowViews.size() <= 1) {
            return;
        }
        for (int i = 1; i < allWindowViews.size(); i++) {
            registerEvent(allWindowViews.get(i));
        }
    }

    //设置代理
    private void proxy(View view) {
        if (view == null) {
            return;
        }
        if (view instanceof ViewGroup) {
            proxyGroupView((ViewGroup) view);
        }
        proxyChildView(view);
    }

    private void proxyChildView(View view) {
        if (!isNeedProxy(view)) {
            return;
        }
        View.OnClickListener clickListener = getClickListener(view);
        AdapterView.OnItemClickListener onItemClickListener = getOnItemClickListener(view);
        RadioGroup.OnCheckedChangeListener onCheckedChangeListener = getOnCheckedChangeListener(view);

        setProxyOnClickListener(view, clickListener);
        setProxyOnItemClick(view, onItemClickListener);
        setProxyOnCheckedChangeListener(view, onCheckedChangeListener);
        hasProxyViews.put(view.hashCode(), true);
    }

    private boolean isNeedProxy(View view) {
        if (!hasProxyViews.get(view.hashCode(), false)) {
            return true;
        }
        Object tag = view.getTag(ABSConfigure.proxy_listener_tag);
        return tag != null && (boolean) tag;
    }

    //为ViewGroup 设置代理
    private void proxyGroupView(ViewGroup groupView) {
        for (int i = 0; i < groupView.getChildCount(); i++) {
            View child = groupView.getChildAt(i);
            proxy(child);
        }
    }

    private void setProxyOnClickListener(View view, final View.OnClickListener clickListener) {
        if (clickListener != null && !isProxy(clickListener.getClass())) {
            View.OnClickListener proxyClick = ProxyManager.getProxyListener(clickListener, null, afterCallListener);
            view.setOnClickListener(proxyClick);
        }
    }

    private void setProxyOnItemClick(View view, final AdapterView.OnItemClickListener onItemClickListener) {
        if (onItemClickListener != null && !isProxy(onItemClickListener.getClass())) {
            AdapterView.OnItemClickListener proxyClick = ProxyManager.getProxyListener(onItemClickListener, null, afterCallListener);
            ((AdapterView) view).setOnItemClickListener(proxyClick);
        }
    }

    private void setProxyOnCheckedChangeListener(View view, final RadioGroup.OnCheckedChangeListener onCheckedChangeListener) {
        if (onCheckedChangeListener != null && !isProxy(onCheckedChangeListener.getClass())) {
            RadioGroup.OnCheckedChangeListener proxyClick = ProxyManager.getProxyListener(onCheckedChangeListener, null, afterCallListener);
            ((RadioGroup) view).setOnCheckedChangeListener(proxyClick);
        }
    }


    //是否是代理类
    private boolean isProxy(Class mClass) {
        return mClass.getSimpleName().contains("$Proxy");
    }

    //获得View的OnClickListener
    private View.OnClickListener getClickListener(View v) {
        Class mclass = v.getClass();
        while (mclass != null) {
            try {
                return getClickListener(v, mclass);
            } catch (Exception ignored) {
            }
            mclass = mclass.getSuperclass();
        }
        return null;
    }

    //获得View的OnClickListener
    private View.OnClickListener getClickListener(View v, Class aClass) throws Exception {
        Field file = aClass.getDeclaredField("mListenerInfo");
        file.setAccessible(true);
        Object mListenerInfo = file.get(v);
        if (mListenerInfo == null) {
            return null;
        }
        Field mOnClickListenerFile = mListenerInfo.getClass().getField("mOnClickListener");
        mOnClickListenerFile.setAccessible(true);
        return (View.OnClickListener) mOnClickListenerFile.get(mListenerInfo);
    }

    //获得View的OnItemClickListener
    private AdapterView.OnItemClickListener getOnItemClickListener(View view) {
        if (!(view instanceof AdapterView)) {
            return null;
        }
        return ((AdapterView) view).getOnItemClickListener();
    }

    //获得View的OnCkeckedListener
    private RadioGroup.OnCheckedChangeListener getOnCheckedChangeListener(View view) {
        if (!(view instanceof RadioGroup)) {
            return null;
        }
        try {
            String FieldName = "mOnCheckedChangeListener";
            Field mOnCheckedChangeListener = view.getClass().getDeclaredField(FieldName);
            mOnCheckedChangeListener.setAccessible(true);
            Object onCheckLis = mOnCheckedChangeListener.get(view);
            return (RadioGroup.OnCheckedChangeListener) onCheckLis;
        } catch (Exception ignored) {
        }
        return null;
    }

    private ProxyManager.AfterCallListener afterCallListener = new ProxyManager.AfterCallListener() {
        @Override
        public void call(Object[] args) {
            try {
                String activityName = mActivity.getClass().getSimpleName();
                if (!TextUtils.isEmpty(mActivity.getPagerTag())) {
                    activityName = activityName + "|" + mActivity.getPagerTag();
                }
                boolean isOnclick = args.length == 1;
                boolean isOnItem = args.length == 4;
                boolean isOnChecked = args.length == 2;
                afterCallOnClick(isOnclick, args, activityName);
                afterCallOnItem(isOnItem, args, activityName);
                afterCallOnCheck(isOnChecked, args, activityName);
            } catch (Exception ignored) {
            }
        }
    };

    //在OnClick方法调用前调用
    private void afterCallOnClick(boolean isCall, Object[] args, String activityName) {
        if (!isCall) {
            return;
        }
        View view = ((View) args[0]);
        int viewId = view.getId();
        String sViewId = getViewIdResourceName(viewId);
        String viewDescribe = NOTracePointUtils.getViewDescribe(view);
        EventLogManager.getInstence().recodeLog(sViewId, activityName, viewDescribe);
    }

    private String getViewIdResourceName(int viewId) {
        if (viewId == -1) {
            return "";
        }
        Activity activity = mActivity.getActivity();
        if (activity == null) {
            return "";
        }
        return activity.getResources().getResourceName(viewId).split("/")[1];
    }

    //在OnItem方法调用前调用
    private void afterCallOnItem(boolean isCall, Object[] args, String activityName) {
        if (!isCall) {
            return;
        }
        Activity activity = mActivity.getActivity();
        if (activity == null) {
            return;
        }
        int viewId = ((View) args[0]).getId();
        String sViewId = activity.getResources().getResourceName(viewId).split("/")[1];
        String viewDescribe = NOTracePointUtils.getViewDescribe((View) args[1]);
        EventLogManager.getInstence().recodeLog(sViewId, activityName, viewDescribe);
    }

    //在OnCheck方法调用前调用
    private void afterCallOnCheck(boolean isCall, Object[] args, String activityName) {
        if (!isCall) {
            return;
        }
        Activity activity = mActivity.getActivity();
        if (activity == null) {
            return;
        }
        int viewId = (int) args[1];
        View view = activity.findViewById(viewId);
        String sViewId = activity.getResources().getResourceName(viewId).split("/")[1];
        String viewDescribe = NOTracePointUtils.getViewDescribe(view);
        EventLogManager.getInstence().recodeLog(sViewId, activityName, viewDescribe);
    }


}
