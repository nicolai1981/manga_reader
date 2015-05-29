package itocorp.mangaextractor.control.event;

public abstract class BaseEvent {
    public enum RESULT {
        SUCCESS,
        UPDATE,
        ERROR,
    }
    public String mMessage;
    public RESULT mResult;
}
