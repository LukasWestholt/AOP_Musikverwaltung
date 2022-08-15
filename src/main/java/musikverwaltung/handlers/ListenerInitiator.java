package musikverwaltung.handlers;

import java.util.ArrayList;
import java.util.List;

public class ListenerInitiator<T> {
    private final List<T> listeners = new ArrayList<>();

    public void addListenerIfNotContains(T toAdd) {
        if (toAdd != null && !listeners.contains(toAdd)) {
            listeners.add(toAdd);
        }
    }

    public List<T> getListeners() {
        return listeners;
    }
}
