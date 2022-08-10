package musikverwaltung.handler;

public interface DestroyListenerManager {
    void setDestroyListener(Runnable toSet);

    void triggerDestroyListener();
}
