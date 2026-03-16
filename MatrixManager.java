package pl.jf.lab.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the state of the Matrix Calculator application.
 * Holds the history of calculations and provides business logic access.
 * This object is stored in the ServletContext to be shared across servlets.
 *
 * @author JakubFilipiak
 * @version 1.0
 */
public class MatrixManager {

    /**
     * List to store history entries. Synchronized list is used for thread safety.
     */
    private final List<HistoryEntry> history;

    /**
     * Constructor initializes the history list.
     */
    public MatrixManager() {
        this.history = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Adds a new entry to the calculation history.
     *
     * @param entry The history entry to add.
     */
    public void addHistoryEntry(HistoryEntry entry) {
        history.add(entry);
    }

    /**
     * Retrieves the full history of calculations.
     *
     * @return An unmodifiable view of the history list.
     */
    public List<HistoryEntry> getHistory() {
        return Collections.unmodifiableList(new ArrayList<>(history));
    }
}