package net.lzzy.practicesonline.activities.practicesonline.utils;

import java.lang.ref.WeakReference;
import android.os.Handler;
import android.os.Message;

/**
 * Created by lzzy_gxy on 2019/4/12.
 * Description:
 */
public  abstract class AbstractStaticHandler<T> extends Handler {
    private final WeakReference<T> context;
    protected AbstractStaticHandler(T context){
        this.context=new WeakReference<>(context);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        T t=context.get();
        handleMessage(msg,t);
    }
    public abstract void handleMessage(Message msg,T t);

    public abstract void sendMessag(Object o);
}
