package cn.xyz.notracepoint;


/**
 * Created by Administrator on 2016/8/11.
 */
public interface IJSONParse {

    public <T> T parseObject(String json,Class<T> clazz);

    public String toJSONString(Object obj);
}
