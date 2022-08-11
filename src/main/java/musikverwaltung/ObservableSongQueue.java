package musikverwaltung;

import java.util.*;
import javafx.collections.ObservableListBase;

/**
 * This data structure builds an observable deque for songs.
 * https://stackoverflow.com/a/28468340/8980073
 */
public class ObservableSongQueue extends ObservableListBase<Song> implements Deque<Song> {
    private final ArrayDeque<Song> queue;

    public ObservableSongQueue() {
        this.queue = new ArrayDeque<>();
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
            nextAdd(0, 1);
        }
        endChange();
        return result;
    }

    @Override
    public boolean offerLast(Song song) {
        beginChange();
        boolean result = queue.offerLast(song);
        if (result) {
            nextAdd(queue.size() - 1, queue.size());
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
            nextAdd(0, 1);
        } finally {
            endChange();
        }
    }

    @Override
    public void addLast(Song song) {
        beginChange();
        try {
            queue.addLast(song);
            nextAdd(queue.size() - 1, queue.size());
        } finally {
            endChange();
        }
    }

    @Override
    public boolean addAll(Collection<? extends Song> c) {
        beginChange();
        try {
            queue.addAll(c);
            nextAdd(queue.size() - c.size(), queue.size());
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
            nextReplace(0, queue.size(), new ArrayList<Song>(c));
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
            final ArrayList<Song> copy = new ArrayList<>(queue);
            Song song = copy.remove(index);
            queue.clear();
            queue.addAll(copy);
            nextRemove(index, song);
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
            nextRemove(0, s);
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
            nextRemove(queue.size() - 1, s);
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
                // TODO equals should be ==
                if (iterator.next().equals(o)) {
                    boolean success = queue.removeFirstOccurrence(o);
                    if (success) {
                        nextRemove(i, (Song) o);
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
                // TODO equals should be ==
                if (iterator.next().equals(o)) {
                    boolean success = queue.removeLastOccurrence(o);
                    if (success) {
                        nextRemove(i, (Song) o);
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
            nextRemove(0, s);
        }
        endChange();
        return s;
    }

    @Override
    public Song pollLast() {
        beginChange();
        Song s = queue.pollLast();
        if (s != null) {
            nextRemove(queue.size() - 1, s);
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
