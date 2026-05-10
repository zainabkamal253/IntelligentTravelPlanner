import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class FileHandler {
        public static <T> void save(String f, List<T> list) {
            try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(f))) {
                o.writeObject(new ArrayList<>(list));
            } catch (IOException e) { System.err.println("Save error: " + e.getMessage()); }
        }
        @SuppressWarnings("unchecked")
        public static <T> List<T> load(String f) {
            File file = new File(f);
            if (!file.exists()) return new ArrayList<>();
            try (ObjectInputStream o = new ObjectInputStream(new FileInputStream(file))) {
                return (List<T>) o.readObject();
            } catch (Exception e) { return new ArrayList<>(); }
        }
    }