package cn.xyz.notracepoint;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Administrator on 2016/8/11.
 */
public class EventLogManager {
    private static final Context context = Statistics.getInstance().getContext();
    private static File BASEPATH = new File(context.getExternalFilesDir(null), "logDir");
    private static File LOGFILE = new File(BASEPATH, "log.json");

    private static final String TAG = "XYZEventLog";
    private static EventLogManager logManger = new EventLogManager();

    private Future<EventPointLogs> future;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    ABSConfigure configure = Statistics.getInstance().getConfigure();
    private IJSONParse jsonParse = configure;

    public static EventLogManager getInstence() {
        return logManger;
    }

    private EventLogManager() {
        if (LOGFILE.exists()) {
            future = executorService.submit(new LoadWebEventPointsCallRunable());
        } else {
            future = executorService.submit(new LoadLocalEventPointsCallRunable());
        }
    }
    public void loadLogFileToLocal(){
        loadLogFileToLocal(configure.getRemoteUrl());
    }

    public void loadLogFileToLocal(String url) {
        if (!BASEPATH.exists()) {
            BASEPATH.mkdirs();
        }
        new HttpDownLoader().downLoad(url, LOGFILE.getAbsolutePath(), true, new HttpDownLoader.HttpDownLoaderListener() {
            @Override
            public void compute(boolean isSucess, String savePath) {
                downLogFile(isSucess, savePath);
            }
        });
    }

    private void downLogFile(boolean isSucess, String savePath) {
        if (isSucess) {
            future = executorService.submit(new LoadWebEventPointsCallRunable());
        }
    }

    private class LoadWebEventPointsCallRunable implements Callable<EventPointLogs> {

        @Override
        public EventPointLogs call() throws Exception {
            InputStream in = new FileInputStream(LOGFILE);
            String json = NOTracePointUtils.inputStream2String(in);
            EventPointLogs webLog = jsonParse.parseObject(json, EventPointLogs.class);
            if (webLog.getVerson() < NOTracePointUtils.getAppVersionCode(context)) {
                webLog = new LoadLocalEventPointsCallRunable().call();
            }
            return webLog;
        }
    }

    private class LoadLocalEventPointsCallRunable implements Callable<EventPointLogs> {
        @Override
        public EventPointLogs call() throws Exception {
            Properties properties = new Properties();
            properties.load(context.getAssets().open(configure.getPointFileAssetName()));
            return parseProperties(properties);
        }

        private EventPointLogs parseProperties(Properties properties) {
            List<EventBuriedPoint> eventBuriedPoints = new ArrayList<>();
            Set<Map.Entry<Object, Object>> entries = properties.entrySet();
            for (Map.Entry<Object, Object> values : entries) {
                String key = String.valueOf(values.getKey());
                String value = String.valueOf(values.getValue());
                EventBuriedPoint eventBuriedPoint = parseValueToPoint(key, value);
                if (eventBuriedPoint != null) {
                    eventBuriedPoint.setNumber(key);
                    eventBuriedPoints.add(eventBuriedPoint);
                }
            }
            EventPointLogs eventPointLogs = new EventPointLogs();
            eventPointLogs.setEventBuriedPoints(eventBuriedPoints);
            return eventPointLogs;
        }

        private EventBuriedPoint parseValueToPoint(String key, String value) {
            try {
                value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
                return jsonParse.parseObject(value, EventBuriedPoint.class);
            } catch (Exception e) {
                Log.e(TAG, "key==" + key + "  value==" + value + "的数据解析错误");
            }
            return null;
        }
    }

    public void recodeLog(String id, String activity, String tag) {
        id = (id == null ? "" : id);
        activity = (activity == null ? "" : activity);
        tag = (tag == null ? "" : tag);
        final EventBuriedPoint eventBuriedPoint = new EventBuriedPoint(id, activity, tag);
        new Thread(new Runnable() {
            @Override
            public void run() {
                startLog(eventBuriedPoint);
            }
        }).start();
    }


    private void startLog(EventBuriedPoint eventBuriedPoint) {
        List<EventBuriedPoint> stringEventBuriedPoints = getEventHashPoints().getEventBuriedPoints();
        for (EventBuriedPoint ep : stringEventBuriedPoints) {
            if (ep.equals(eventBuriedPoint)) {
                Log.i(TAG, ep.getNumber() + ":" + ep.getDescribe());
                NewLogEvent.with(context).umengLog(ep.getNumber()).googleLog("", ep.getDescribe());
            }
        }
    }

    private EventPointLogs getEventHashPoints() {
        try {
            return future.get();
        } catch (Exception ignored) {
        }
        return new EventPointLogs();
    }

    public String parseLocalToJson() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<EventPointLogs> submit = executorService.submit(new LoadLocalEventPointsCallRunable());
        try {
            EventPointLogs eventPointLogs = submit.get();
            return jsonParse.toJSONString(eventPointLogs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
