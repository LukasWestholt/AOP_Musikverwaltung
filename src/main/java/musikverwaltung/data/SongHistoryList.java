package musikverwaltung.data;

import java.util.LinkedList;

/**
 * //TODO explain ...
 */
// https://stackoverflow.com/a/14322473/8980073
public class SongHistoryList extends LinkedList<Song> {

    private final int limit;

    /**
     * @param limit the length of songs the SongHistoryList will hold
     */
    public SongHistoryList(int limit) {
        this.limit = limit;
    }

    /** overrides ...
     *
     * @param s Song that will be added to SongHistoryList
     * @return the information whether a song has been added successfully
     */
    @Override
    public boolean add(Song s) {
        boolean added = super.add(s);
        while (added && size() > limit) {
            super.remove();
        }
        return added;
    }
}
