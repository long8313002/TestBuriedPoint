package cn.xyz.notracepoint;

/**
 * 配置文件
 * Created by Administrator on 2016/8/12.
 */
public abstract class ABSConfigure implements IJSONParse{

    public static final int proxy_listener_tag = 0x7f0b0007;
    /**
     * 获得埋点文件在Assert文件夹中的名字
     * @return
     */
    public abstract String getPointFileAssetName();


    /**
     * 获得文件在远程服务器中的地址
     * @return
     */
    public abstract String getRemoteUrl();


}
