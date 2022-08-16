package musikverwaltung.data;

import java.util.*;
import javafx.collections.ObservableListBase;

/**
 * This data structure builds an observable deque for songs.
 * https://stackoverflow.com/a/28468340/8980073
 */
public class ObservableSongQueue extends ObservableListBase<Song> implements Deque<Song> {
    private final ArrayDeque<Song> queue;
    private Song firstSong;
    private int remainingSongs;

    public ObservableSongQueue() {
        this.queue = new ArrayDeque<>();
        this.remainingSongs = 0;
    }

    public int getRemainingSongs() {
        return this.remainingSongs;
    }

    private void resetRemainingSongs() {
        this.remainingSongs = queue.size();
    }

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

    public void setRemainingSongs(int i) {
        this.remainingSongs = i;
    }

    public void reset() {
        this.remainingSongs = queue.size();
        while (firstSong != null && queue.peekFirst() != firstSong) {
            circleForwards();
        }
        assert firstSong == null || queue.peekFirst() == firstSong;
    }

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

    public void addToRemainingSongs(int i) {
        this.remainingSongs += i;
    }

    private void documentAdd(int i1, int i2) {
        nextAdd(i1, i2);
        remainingSongs += i2 - i1;
        if (firstSong == null) {
            firstSong = get(i1);
        }
    }

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

    private void documentSet(int size, List<Song> list) {
        nextReplace(0, size, list);
        resetRemainingSongs();
        if (firstSong == null) {
            firstSong = list.get(0);
        }
    }

    @Override
    public boolean offer(Song s) {
        return offerLast(s);
    }

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

    @Override
    public boolean add(Song s) {
        addLast(s);
        return true;
    }

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

    @Override
    public void push(Song song) {
        addFirst(song);
    }

    @Override
    public Song pop() {
        return removeFirst();
    }

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

    @Override
    public Song remove() {
        return removeFirst();
    }

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

    @Override
    public Song poll() {
        return pollFirst();
    }

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

    @Override
    public Song element() {
        return getFirst();
    }

    @Override
    public Song getFirst() {
        return queue.getFirst();
    }

    @Override
    public Song getLast() {
        return queue.getLast();
    }

    @Override
    public Song peek() {
        return peekFirst();
    }

    @Override
    public Song peekFirst() {
        return queue.peekFirst();
    }

    @Override
    public Song peekLast() {
        return queue.peekLast();
    }

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

    @Override
    public Iterator<Song> descendingIterator() {
        return queue.descendingIterator();
    }

    @Override
    public int size() {
        return queue.size();
    }
}
