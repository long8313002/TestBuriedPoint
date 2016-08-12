package cn.xyz.notracepoint;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Looper;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 代理rootView
 * Created by zhangzheng on 2016/8/12.
 */
public class ProxyDecorView extends FrameLayout {

    private PointF downPoint = new PointF();
    private PointF upPoint = new PointF();
    private Rect viewRect = new Rect();

    public ProxyDecorView(View dectorView) {
        super(dectorView.getContext());
        ViewGroup viewGroup = removeParent(dectorView);
        addView(dectorView);
        if (viewGroup != null) {
            viewGroup.addView(this);
        }
    }

    private ViewGroup removeParent(View childView) {
        ViewParent parent = childView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(childView);
            return (ViewGroup) parent;
        }
        return null;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downPoint.set(ev.getRawX(), ev.getRawY());
        }
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            upPoint.set(ev.getRawX(), ev.getRawY());
            sendPointMessage(downPoint, upPoint);
            upPoint.set(-100,-100);
        }
        return super.dispatchTouchEvent(ev);
    }

    private void sendPointMessage(PointF downPoint, PointF upPoint) {
        if(Looper.myLooper()!=Looper.getMainLooper()){
            final PointF newdownPoint =new PointF(downPoint.x,downPoint.y);
            final PointF newupPoint =new PointF(upPoint.x,upPoint.y);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendPointMessage(newdownPoint,newupPoint);
                }
            }).start();
            return;
        }
        List<View> views = getChildViewOnPoint(downPoint, upPoint);
        for (View view:views){
            sendPointLog(view);
        }
    }

    private void sendPointLog(View view){
        int viewId = view.getId();
        Activity activity = getActivity();
        if(activity == null){
            return;
        }
        String activityName = activity.getClass().getSimpleName();
        String sViewId = viewId<=0?"":activity.getResources().getResourceName(viewId).split("/")[1];
        String viewDescribe = NOTracePointUtils.getViewDescribe(view);
        EventLogManager.getInstence().recodeLog(sViewId, activityName, viewDescribe);
    }

    private Activity getActivity(){
        Context context = getContext();
        if(context instanceof Activity){
            return (Activity) context;
        }
        if(context instanceof ContextThemeWrapper){
            return (Activity) ((ContextThemeWrapper) context).getBaseContext();
        }
        return null;
    }

    private List<View> getChildViewOnPoint(PointF downPoint, PointF upPoint){
        return getChildViewOnPointByGroup(this,downPoint,upPoint);
    }

    private List<View> getChildViewOnPointByGroup(ViewGroup group, PointF downPoint, PointF upPoint) {
        List<View> childViews = new ArrayList<>();
        int childCount = group.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = group.getChildAt(i);
            child = getChildViewOnPointByView(child, downPoint, upPoint);
            if (child != null&&child.isClickable()) {
                childViews.add(child);
            }
            if (child instanceof ViewGroup) {
                List<View> childViewOnPoint = getChildViewOnPointByGroup((ViewGroup) child, downPoint, upPoint);
                if(childViewOnPoint!=null&&childViewOnPoint.size()>0){
                    childViews.addAll(childViewOnPoint);
                }
            }
        }
        return childViews;
    }

    private View getChildViewOnPointByView(View view, PointF downPoint, PointF upPoint) {
        view.getGlobalVisibleRect(viewRect);
        if (viewRect.contains((int) downPoint.x, (int) downPoint.y)
                && viewRect.contains((int) upPoint.x, (int) upPoint.y)) {
            return view;
        }
        return null;
    }
}
