package cn.xyz.notracepoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/11.
 */
public class EventPointLogs {
    private int verson = NOTracePointUtils.getAppVersionCode(Statistics.getInstance().getContext());

    public int getVerson() {
        return verson;
    }

    private List<EventBuriedPoint> eventBuriedPoints = new ArrayList<>();

    public List<EventBuriedPoint> getEventBuriedPoints() {
        return eventBuriedPoints;
    }

    public void setEventBuriedPoints(List<EventBuriedPoint> eventBuriedPoints) {
        this.eventBuriedPoints = eventBuriedPoints;
    }
}
