import java.io.*;
import java.util.ArrayList;
import java.util.List;

// This class handles saving and loading our data to and from a file
public class FileHandler {

    // Method to save any list of objects to a file
    public static <T> void save(String f, List<T> list) {
        // We use ObjectOutputStream to write the list as an actual object
        try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(f))) {
            // We create a new ArrayList copy to make sure it's serializable
            o.writeObject(new ArrayList<>(list));
        } catch (IOException e) { 
            // If something goes wrong, print the error message
            System.err.println("Save error: " + e.getMessage()); 
        }
    }

    // Method to load the list back from the file
    @SuppressWarnings("unchecked")
    public static <T> List<T> load(String f) {
        File file = new File(f);
        
        // If the file isn't there yet, just return an empty list so the app doesn't crash
        if (!file.exists()) return new ArrayList<>();

        // Try to read the file and turn it back into a List
        try (ObjectInputStream o = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) o.readObject();
        } catch (Exception e) { 
            // If the file is corrupted or empty, just return a fresh list
            return new ArrayList<>(); 
        }
    }
}