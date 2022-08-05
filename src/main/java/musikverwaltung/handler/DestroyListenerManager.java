package musikverwaltung.handler;

import java.util.ArrayList;
import java.util.List;

public interface DestroyListenerManager {
    List<Runnable> listeners = new ArrayList<>();

    default void setDestroyListener(Runnable toSet) {
        if (toSet != null) {
            listeners.clear();
            listeners.add(toSet);
        }
    }

    default void triggerDestroyListener() {
        for (Runnable hl : listeners) {
            hl.run();
        }
    }
}
