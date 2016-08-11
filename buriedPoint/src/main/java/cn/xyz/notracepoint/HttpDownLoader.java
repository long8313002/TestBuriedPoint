package cn.xyz.notracepoint;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 下载文件的工具类
 * Created by zhangzheng on 2016/1/20.
 */
public class HttpDownLoader {

    public static final String TAG = "downLoad";

    /**
     * 下载完成回调
     * isSucess 是否下载成功
     * savePath 保存的路径
     */
    public interface HttpDownLoaderListener {
        void compute(boolean isSucess, String savePath);
    }

    /**
     * @param surl         下载的url
     * @param savePath     下载后保存的路径
     * @param judgeLength  是否比对与本地文件的大小 判断是否下载文件
     * @param downListener 下载监听器
     */
    public void downLoad(final String surl, final String savePath, final boolean judgeLength, final HttpDownLoaderListener downListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                down(surl, savePath, downListener, judgeLength);
            }
        }).start();
    }

    public void downLoad(final String surl, final String savePath, final HttpDownLoaderListener downListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                down(surl, savePath, downListener, false);
            }
        }).start();
    }

    private void down(String surl, String savePath, HttpDownLoaderListener downListener, boolean judgeLength) {
        boolean downSucess = false;
        try {
            URLConnection con = getConnection(surl);
            int contentLength = con.getContentLength();
            if (isConnectionException(contentLength)) {
                return;
            }
            File saveFile = new File(savePath);
            if (judgeLength && judgeLocalSameWeb(saveFile, contentLength)) {
                return;
            }
            saveFile.delete();
            InputStream is = con.getInputStream();
            downSaveToLocal(saveFile, is);
            downSucess = true;
        } catch (Exception e) {
            Log.i(TAG, "下载失败：" + e.toString());
        }
        downLoadCompute(downListener, downSucess, savePath);
    }

    //获得连接
    private URLConnection getConnection(String surl) throws IOException {
        URL url = new URL(surl);
        return url.openConnection();
    }

    //判断本地资源和服务器是否相同
    private boolean judgeLocalSameWeb(File saveFile, int contentLength) {
        int localLength = saveFile.exists() ? (int) saveFile.length() : 0;
        if (localLength == contentLength) {
            Log.i(TAG, "本地包含相同资源 不需要重复下载:"+saveFile.getAbsolutePath());
            return true;
        }
        return false;
    }

    //是否连接异常  如果文件长度为-1 说明网络连接失败
    private boolean isConnectionException(int contentLength) {
        return contentLength == -1;
    }

    //下载文件并且保存到本地
    private void downSaveToLocal(File saveFile, InputStream is) throws IOException {
        byte[] bs = new byte[1024];
        int len;
        OutputStream os = new FileOutputStream(saveFile);
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        os.flush();
        os.close();
        is.close();
        Log.i(TAG, "下载完成：" + saveFile.getAbsolutePath());
    }

    //下载完成
    private void downLoadCompute(HttpDownLoaderListener downListener, boolean isSucess, String savePath) {
        if (downListener == null) {
            return;
        }
        downListener.compute(isSucess, savePath);
        if (!isSucess) {
            new File(savePath).delete();
        }
    }
}
