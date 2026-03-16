package pl.jf.lab.model;

import lombok.NoArgsConstructor;

/**
 * Custom exception thrown for errors in matrix dimensions.
 * Occurs when operations are attempted on matrices of incompatible sizes
 * or when a matrix is expected to be square but is not.
 *
 * @author JakubFilipiak
 * @version 3.1
 */
@NoArgsConstructor
public class MatrixDimensionException extends Exception {

    /**
     * Constructs a new MatrixDimensionException with the specified detail message.
     * 
     * @param message the detail message.
     */
    public MatrixDimensionException(String message) {
        super(message);
    }
}