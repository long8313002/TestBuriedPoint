package cn.xyz.notracepoint;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by Administrator on 2016/8/11.
 */
public class ProxyManager {

    public interface BeforeCallListener {
        public void call(Object[] args);
    }

    public interface AfterCallListener {
        public void call(Object[] args);
    }

    private static class ProxyInvocationHandler implements InvocationHandler {
        protected Object tagerPosition;
        private BeforeCallListener beforeLis;
        private AfterCallListener afterLis;

        public ProxyInvocationHandler(Object tagerPosition) {
            this.tagerPosition = tagerPosition;
        }

        public void setBeforeLis(BeforeCallListener beforeLis) {
            this.beforeLis = beforeLis;
        }

        public void setAfterLis(AfterCallListener afterLis) {
            this.afterLis = afterLis;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (beforeLis != null) {
                beforeLis.call(args);
            }
            Object retuenOc = method.invoke(tagerPosition, args);
            if (afterLis != null) {
                afterLis.call(args);
            }
            return retuenOc;
        }

        public Object create(){
            return  Proxy.newProxyInstance(tagerPosition.getClass().getClassLoader(), tagerPosition.getClass().getInterfaces(), this);
        }
    }

    /**
     * 获得一个点击事件的代理
     *
     * @param target
     * @return
     */
    public static <T> T getProxyListener(T target, BeforeCallListener beforeCall, AfterCallListener afterCall) {
        ProxyInvocationHandler onclickListenerProxyHandler = new ProxyInvocationHandler(target);
        onclickListenerProxyHandler.setAfterLis(afterCall);
        onclickListenerProxyHandler.setBeforeLis(beforeCall);
        return (T) onclickListenerProxyHandler.create();
    }

}
