package musikverwaltung.handler;

import java.util.ArrayList;
import java.util.function.Consumer;

public interface StringListenerManager {
    ArrayList<Consumer<String>> listeners = new ArrayList<>();
    // TODO (LW) can i replace "string" with a generic placeholder?

    default void addStringListenerIfNotContains(Consumer<String> toAdd) {
        if (toAdd != null && !listeners.contains(toAdd)) {
            listeners.add(toAdd);
        }
        assert listeners.size() <= 1; // TODO failed once
    }

    default void triggerStringListener(String e) {
        for (Consumer<String> hl : listeners) {
            hl.accept(e);
        }
    }
}
