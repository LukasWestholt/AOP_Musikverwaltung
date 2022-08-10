package musikverwaltung.handler;

import java.util.ArrayList;

public interface ActionListenerManager {
    ArrayList<Runnable> listeners = new ArrayList<>();

    default void addActionListenerIfNotContains(Runnable toAdd) {
        if (toAdd != null && !listeners.contains(toAdd)) {
            listeners.add(toAdd);
        }
        assert listeners.size() <= 1; // TODO
    }

    default void triggerActionListener() {
        for (Runnable hl : listeners) {
            hl.run();
        }
    }
}
