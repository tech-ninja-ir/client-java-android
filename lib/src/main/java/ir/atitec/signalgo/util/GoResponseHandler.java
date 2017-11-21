
package ir.atitec.signalgo.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;

import ir.atitec.signalgo.Connector;
import ir.atitec.signalgo.annotations.GoError;
import ir.atitec.signalgo.annotations.GoMethodName;
import ir.atitec.signalgo.models.MessageContract;
import ir.atitec.signalgo.models.QueueMethods;
import needle.Needle;

/**
 * Created by whiteman on 7/12/2016.
 */
public abstract class GoResponseHandler<T> {
    private Connector connector;

    public GoResponseHandler(){
//        typeToken = new TypeToken<MessageContract<T>>(getClass()) {};
    }
    GoMethodName goMethodName;

    public void postResponse(final MessageContract<T> messageContract) {
        Needle.onMainThread().execute(new Runnable() {
            @Override
            public void run() {
                if (messageContract != null && messageContract.isSuccess) {
                    onSuccess(messageContract.data);
                } else if (messageContract != null && !messageContract.isSuccess) {
                    onError(messageContract.errorCode, messageContract.message, errorMessage(goMethodName.errors(), messageContract.errorCode));
                } else {
                    onConnectionError();
                }
            }
        });
    }

    public void setGoMethodName(GoMethodName goMethodName) {
        this.goMethodName = goMethodName;
    }

    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    private String errorMessage(GoError[] goError, int errorCode) {
        for (int i = 0; i < goError.length; i++) {
            if (goError[i].errorCode() == errorCode) {
                return goError[i].message();
            }
        }
        return null;
    }

    public TypeToken<MessageContract<T>> typeToken;
    public Type type;


//    public void setTypeToken(TypeToken<T> typeToken) {
//        this.typeToken = new TypeToken<MessageContract<T>>(typeToken.getRawType()) {
//        };
//    }

    public void setTypeToken(TypeToken<MessageContract<T>> typeToken) {
        this.typeToken = typeToken;
    }

    public Type getType() {
        return typeToken.getType();
    }

    public abstract void onSuccess(T t);

    public void onError(int errorCode, String message, String handleMessage) {
        if (connector.getMonitorableErrorMessage() == null) {
            return;
        }
        if (handleMessage != null) {
            connector.getMonitorableErrorMessage().onMonitor(handleMessage,errorCode,goMethodName.printErrors());
        } else {
            connector.getMonitorableErrorMessage().onMonitor(message,errorCode,goMethodName.printErrors());
        }
    }

    public void onConnectionError() {
        if (connector.getMonitorableErrorMessage() == null)
            return;
        connector.getMonitorableErrorMessage().onMonitor("خطا در ارتباط با سرور!",-1,goMethodName.printErrors());
    }


    public void onAbort() {
        if (connector.getMonitorableErrorMessage() == null)
            return;
        connector.getMonitorableErrorMessage().onMonitor("کمی صبر کنید، سپس درخواستتان را مجدد ارسال کنید!",-2,goMethodName.printErrors());
    }
}
