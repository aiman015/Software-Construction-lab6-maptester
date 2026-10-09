import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lab 6 - Task 1: MapTester
 *
 * A phone book backed by a HashMap<String, String>.
 *
 * Explore:
 * 1. Same KEY    -> the second put() OVERWRITES the first.
 *                   Reason: HashMap keys must be unique.
 * 2. Same VALUE  -> BOTH entries are stored successfully.
 *                   Reason: HashMap allows duplicate values.
 */
public class MapTester {

    private HashMap<String, String> phoneBook;

    public MapTester() {
        phoneBook = new HashMap<String, String>();
    }

    // Adds an entry using put(). Same key again replaces the old value.
    public void enterNumber(String name, String number) {
        phoneBook.put(name, number);
    }

    // Retrieves a number by name using get(). Returns null if not found.
    public String lookupNumber(String name) {
        return phoneBook.get(name);
    }

    // Reverse search: HashMap can only get() by key, so we loop the entries.
    public String lookupName(String number) {
        StringBuilder names = new StringBuilder();
        for (Map.Entry<String, String> entry : phoneBook.entrySet()) {
            if (entry.getValue().equals(number)) {
                if (names.length() > 0) names.append(", ");
                names.append(entry.getKey());
            }
        }
        return names.length() == 0 ? null : names.toString();
    }

    // Deletes an entry using remove().
    public String deleteContact(String name) {
        return phoneBook.remove(name);
    }

    // Returns all saved names, sorted A-Z (HashMap has no order).
    public List<String> getNames() {
        List<String> names = new ArrayList<String>(phoneBook.keySet());
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        return names;
    }

    // =====================================================================
    //  main() -- demonstrates the two explore questions
    // =====================================================================
    public static void main(String[] args) {
        MapTester phoneBook = new MapTester();

        System.out.println("===== EXPLORE: Duplicate Keys =====");
        phoneBook.enterNumber("Alice", "111-1111");
        System.out.println("After 1st put: Alice = " + phoneBook.lookupNumber("Alice"));

        phoneBook.enterNumber("Alice", "222-2222");
        System.out.println("After 2nd put: Alice = " + phoneBook.lookupNumber("Alice"));
        System.out.println("-> RESULT: The 2nd value OVERWRITES the 1st.");
        System.out.println("-> Reason: HashMap keys must be unique.");
        System.out.println("-> Total entries now: " + phoneBook.getNames().size());
        System.out.println();

        System.out.println("===== EXPLORE: Duplicate Values =====");
        phoneBook.enterNumber("Bob", "555-5555");
        phoneBook.enterNumber("Charlie", "555-5555");
        System.out.println("Bob     = " + phoneBook.lookupNumber("Bob"));
        System.out.println("Charlie = " + phoneBook.lookupNumber("Charlie"));
        System.out.println("-> RESULT: BOTH entries exist.");
        System.out.println("-> Reason: HashMap allows duplicate VALUES.");
        System.out.println("-> Total entries now: " + phoneBook.getNames().size());
    }
}