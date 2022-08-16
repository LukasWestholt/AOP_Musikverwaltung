package musikverwaltung.data;

import java.util.*;
import javafx.collections.ObservableListBase;

/**
 * data structure functions as an observable deque for songs.
 * holds information about songs it contains and the number of songs there is
 *
 * https://stackoverflow.com/a/28468340/8980073
 */

public class ObservableSongQueue extends ObservableListBase<Song> implements Deque<Song> {
    private final ArrayDeque<Song> queue;
    private Song firstSong;
    private int remainingSongs;

    /**
     * created with an empty ArrayDeque and therefor no songs
     */
    public ObservableSongQueue() {
        this.queue = new ArrayDeque<>();
        this.remainingSongs = 0;
    }

    /**
     * @return number of songs in deque
     */
    public int getRemainingSongs() {
        return this.remainingSongs;
    }

    /**
     * number of songs in deque gets set to size of deque
     */
    private void resetRemainingSongs() {
        this.remainingSongs = queue.size();
    }

    /**
     * @return realative position of first Song
     */
    public int getRelativePositionOfFirstSong() {
        if (firstSong == null) {
            return -1;
        }
        int r = 0;
        for (Song song : queue) {
            if (firstSong != song) {
                r++;
            } else {
                break;
            }
        }
        assert r < queue.size();
        return r;
    }

    /**
     * @param i = remaining songs
     */
    public void setRemainingSongs(int i) {
        this.remainingSongs = i;
    }

    /**
     * resets to ObservableSongQueue to default settings
     */
    public void reset() {
        this.remainingSongs = queue.size();
        while (firstSong != null && queue.peekFirst() != firstSong) {
            circleForwards();
        }
        assert firstSong == null || queue.peekFirst() == firstSong;
    }
    /**
     * @return changes direction of ObservableSongQueue -> forwards
     */
    public Song circleForwards() {
        beginChange();
        try {
            Song s = queue.removeFirst();
            nextRemove(0, s);
            queue.addLast(s);
            nextAdd(queue.size() - 1, queue.size());
            return s;
        } finally {
            endChange();
        }
    }

    /**
     * @return changes direction of ObservableSongQueue -> backwards
     */
    public Song circleBackwards() {
        beginChange();
        try {
            Song s = queue.removeLast();
            nextRemove(queue.size() - 1, s);
            queue.addFirst(s);
            nextAdd(0, 1);
            return s;
        } finally {
            endChange();
        }
    }

    /**
     * @param i = number of songs that get added to deque
     */
    public void addToRemainingSongs(int i) {
        this.remainingSongs += i;
    }

    //TODO
    /**
     * @param i1
     * @param i2
     */
    private void documentAdd(int i1, int i2) {
        nextAdd(i1, i2);
        remainingSongs += i2 - i1;
        if (firstSong == null) {
            firstSong = get(i1);
        }
    }

    //TODO
    /**
     * @param i
     * @param song
     */
    private void documentRemove(int i, Song song) {
        nextRemove(i, song);
        remainingSongs--;
        if (firstSong == song) {
            if (queue.size() == 0) {
                firstSong = null;
            } else {
                firstSong = get(i);
            }
        }
    }

    //TODO
    /**
     * @param size
     * @param list
     */
    private void documentSet(int size, List<Song> list) {
        nextReplace(0, size, list);
        resetRemainingSongs();
        if (firstSong == null && !list.isEmpty()) {
            firstSong = list.get(0);
        }
    }

    /**
     * @param s the element to add
     * @return whether adding the Song was successful
     */
    @Override
    public boolean offer(Song s) {
        return offerLast(s);
    }

    /**
     * @param song the element to add
     * @return whether adding the Song was successful
     */
    @Override
    public boolean offerFirst(Song song) {
        beginChange();
        boolean result = queue.offerFirst(song);
        if (result) {
            documentAdd(0, 1);
        }
        endChange();
        return result;
    }

    /**
     * @param song the element to add
     * @return whether adding the Song was successful
     */
    @Override
    public boolean offerLast(Song song) {
        beginChange();
        boolean result = queue.offerLast(song);
        if (result) {
            documentAdd(queue.size() - 1, queue.size());
        }
        endChange();
        return result;
    }

    /**
     * @param s element whose presence in this collection is to be ensured
     * @return whether adding the Song was successful
     */
    @Override
    public boolean add(Song s) {
        addLast(s);
        return true;
    }

    /**
     * @param song the element to add
     */
    @Override
    public void addFirst(Song song) {
        beginChange();
        try {
            queue.addFirst(song);
            documentAdd(0, 1);
        } finally {
            endChange();
        }
    }

    /**
     * @param song the element to add
     */
    @Override
    public void addLast(Song song) {
        beginChange();
        try {
            queue.addLast(song);
            documentAdd(queue.size() - 1, queue.size());
        } finally {
            endChange();
        }
    }

    /**
     * @param c collection containing elements to be added to this collection
     * @return whether adding the songs was successful
     */
    @Override
    public boolean addAll(Collection<? extends Song> c) {
        beginChange();
        try {
            queue.addAll(c);
            documentAdd(queue.size() - c.size(), queue.size());
            return true;
        } finally {
            endChange();
        }
    }


    /**
     * @param c collection containing elements that will be the new contents
     * @return whether setting the songs was successful
     */
    @Override
    public boolean setAll(Collection<? extends Song> c) {
        beginChange();
        try {
            queue.clear();
            queue.addAll(c);
            documentSet(queue.size(), new ArrayList<>(c));
            return true;
        } finally {
            endChange();
        }
    }

    /**
     * @param song the element to push
     */
    @Override
    public void push(Song song) {
        addFirst(song);
    }

    /**
     * @return first element of ObservableSongQueue (no longer contained)
     */
    @Override
    public Song pop() {
        return removeFirst();
    }

    /**
     * @param index the index of the element to be removed
     * @return element at index position (no longer contained)
     */
    @Override
    public Song remove(int index) {
        beginChange();
        try {
            ArrayList<Song> copy = new ArrayList<>(queue);
            Song song = copy.remove(index);
            queue.clear();
            queue.addAll(copy);
            documentRemove(index, song);
            return song;
        } finally {
            endChange();
        }
    }

    /**
     * @return first element of ObservableSongQueue (no longer contained)
     */
    @Override
    public Song remove() {
        return removeFirst();
    }

    /**
     * @return first element of ObservableSongQueue (no longer contained)
     */
    @Override
    public Song removeFirst() {
        beginChange();
        try {
            Song s = queue.removeFirst();
            documentRemove(0, s);
            return s;
        } finally {
            endChange();
        }
    }

    /**
     * @return last element of ObservableSongQueue (no longer contained)
     */
    @Override
    public Song removeLast() {
        beginChange();
        try {
            Song s = queue.removeLast();
            documentRemove(queue.size() - 1, s);
            return s;
        } finally {
            endChange();
        }
    }


    /**
     * Removes the first occurrence of the specified element in this deque (when traversing the deque from head to tail). If the deque does not contain the element, it is unchanged
     *
     * @param o element to be removed from this deque, if present
     * @return true if the deque contained the specified element, else: false
     */
    @Override
    public boolean removeFirstOccurrence(Object o) {
        beginChange();
        try {
            Iterator<Song> iterator = queue.iterator();
            for (int i = 0; i < queue.size(); i++) {
                Song temp = iterator.next(); // TODO
                assert (temp == o) == temp.equals(o);
                if (temp == o) {
                    boolean success = queue.removeFirstOccurrence(o);
                    if (success) {
                        documentRemove(i, (Song) o);
                    }
                    return success;
                }
            }
            return false;
        } finally {
            endChange();
        }
    }

    /**
     * Removes the last occurrence of the specified element in this deque (when traversing the deque from head to tail). If the deque does not contain the element, it is unchanged
     *
     * @param o element to be removed from this deque, if present
     * @return if the deque contained the specified element, else: false
     */
    @Override
    public boolean removeLastOccurrence(Object o) {
        beginChange();
        try {
            Iterator<Song> iterator = queue.descendingIterator();
            for (int i = 0; i < queue.size(); i++) {
                Song temp = iterator.next(); // TODO
                assert (temp == o) == temp.equals(o);
                if (temp == o) {
                    boolean success = queue.removeLastOccurrence(o);
                    if (success) {
                        documentRemove(i, (Song) o);
                    }
                    return success;
                }
            }
            return false;
        } finally {
            endChange();
        }
    }

    /**
     * @return pollFirst
     */
    @Override
    public Song poll() {
        return pollFirst();
    }

    /**
     * @return pollFirst
     */
    @Override
    public Song pollFirst() {
        beginChange();
        Song s = queue.pollFirst();
        if (s != null) {
            documentRemove(0, s);
        }
        endChange();
        return s;
    }

    /**
     * @return pollLast
     */
    @Override
    public Song pollLast() {
        beginChange();
        Song s = queue.pollLast();
        if (s != null) {
            documentRemove(queue.size() - 1, s);
        }
        endChange();
        return s;
    }

    /**
     * @return first element of ObservableSongQueue
     */
    @Override
    public Song element() {
        return getFirst();
    }

    /**
     * @return first element of ObservableSongQueue
     */
    @Override
    public Song getFirst() {
        return queue.getFirst();
    }

    @Override
    public Song getLast() {
        return queue.getLast();
    }

    /**
     * @return peekFirst
     */
    @Override
    public Song peek() {
        return peekFirst();
    }

    /**
     * @return peekFirst
     */
    @Override
    public Song peekFirst() {
        return queue.peekFirst();
    }

    /**
     * @return peekLast
     */
    @Override
    public Song peekLast() {
        return queue.peekLast();
    }

    /**
     * @param index index of the element to return
     * @return next element of Iterator
     */
    @Override
    public Song get(int index) {
        if (index < 0) {
            throw new NoSuchElementException();
        }
        Iterator<Song> iterator = queue.iterator();
        for (int i = 0; i < index; i++) {
            iterator.next();
        }
        return iterator.next();
    }

    /**
     * @return Iterator over songs
     */
    @Override
    public Iterator<Song> descendingIterator() {
        return queue.descendingIterator();
    }

    /**
     * @return size of ObservableSongQueue
     */
    @Override
    public int size() {
        return queue.size();
    }
}
