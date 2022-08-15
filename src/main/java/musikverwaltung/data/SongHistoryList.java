package musikverwaltung.data;

import java.util.LinkedList;

// https://stackoverflow.com/a/14322473/8980073
public class SongHistoryList extends LinkedList<Song> {

    private final int limit;

    public SongHistoryList(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(Song s) {
        boolean added = super.add(s);
        while (added && size() > limit) {
            super.remove();
        }
        return added;
    }
}
