package musikverwaltung;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SettingFile {

    private static final String filename = "settings.ser";

    @SuppressWarnings("unchecked")
    public static List<String> load() {
        try {
            FileInputStream in = new FileInputStream(filename);
            ObjectInputStream s = new ObjectInputStream(in);
            return (List<String>) s.readObject();
        } catch (IOException | ClassNotFoundException ignored) {
            return new ArrayList<>();
        }
    }

    public static void save(ArrayList<String> list) {
        try (FileOutputStream f = new FileOutputStream(filename);
             ObjectOutputStream s = new ObjectOutputStream(f)) {
            s.writeObject(list);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }
}
