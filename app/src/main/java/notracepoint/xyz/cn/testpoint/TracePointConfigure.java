package notracepoint.xyz.cn.testpoint;

import cn.xyz.notracepoint.ABSConfigure;

/**
 * Created by Administrator on 2016/8/12.
 */
public class TracePointConfigure extends ABSConfigure {
    @Override
    public String getPointFileAssetName() {
        return null;
    }

    @Override
    public String getRemoteUrl() {
        return null;
    }

    @Override
    public <T> T parseObject(String json, Class<T> clazz) {
        return null;
    }

    @Override
    public String toJSONString(Object obj) {
        return null;
    }
}
