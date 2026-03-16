package pl.jf.lab.model;

import java.util.List;

/**
 * A record representing the state of a matrix using Collections.
 * Replaces the old class with a Java Record.
 * Uses {@code List<List<Double>>} instead of arrays.
 *
 * @param data The matrix elements represented as a list of lists.
 * @param rows Number of rows.
 * @param cols Number of columns.
 * @author JakubFilipiak
 * @version 3.0
 */
public record MatrixData(List<List<Double>> data, int rows, int cols) {
    
    /**
     * Compact constructor for validation.
     */
    public MatrixData {
        if (data == null) {
            throw new IllegalArgumentException("Data list cannot be null");
        }
    }
}