package ir.atitec.signalgo.models;

/**
 * Created by whiteman on 7/12/2016.
 */
public class MessageContract<T> {

    public T data;
    public boolean isSuccess;
    public String message;
    public int errorCode;
    public String stackTrace;

    @Override
    public String toString() {
        return "success: " + isSuccess + " ErrorMessage: " + message + " errorCode: " + errorCode + " stackTrace : " + stackTrace;
    }
}