package musikverwaltung.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface StringListenerManager {
    List<Consumer<String>> listeners = new ArrayList<>();
    // TODO (LW) can i replace "string" with a generic placeholder?

    default void addListenerIfNotContains(Consumer<String> toAdd) {
        if (toAdd != null && !listeners.contains(toAdd)) {
            listeners.add(toAdd);
        }
        assert listeners.size() <= 1; // TODO
    }

    default void triggerListener(String e) {
        for (Consumer<String> hl : listeners) {
            hl.accept(e);
        }
    }
}
