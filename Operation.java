package pl.jf.lab.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing available matrix operations.
 *
 * @author JakubFilipiak
 * @version 4.0
 */
public enum Operation {
    /**
     * Represents the matrix addition operation.
     */
    ADD("+", "Dodawanie"),

    /**
     * Represents the matrix subtraction operation.
     */
    SUBTRACT("-", "Odejmowanie"),

    /**
     * Represents the matrix multiplication operation.
     */
    MULTIPLY("*", "Mnożenie"),

    /**
     * Represents the calculation of the matrix determinant.
     * This operation requires a square matrix.
     */
    DETERMINANT("det", "Wyznacznik"),

    /**
     * Represents the calculation of the inverse matrix.
     * This operation requires a square matrix.
     */
    INVERSE("inv", "Odwrotność");

    /**
     * The mathematical symbol or short code associated with the operation.
     */
    private final String symbol;

    /**
     * The human-readable description of the operation (in Polish).
     */
    private final String description;
    
/**
     * Private constructor for the enum constants.
     * Initializes the operation with a symbol and a description.
     *
     * @param symbol      The short symbol representing the operation (e.g., "+").
     * @param description The full human-readable description of the operation.
     */
    Operation(String symbol, String description) {
        this.symbol = symbol;
        this.description = description;
    }

    /**
     * Retrieves the description of the operation.
     *
     * @return The description string (e.g., "Dodawanie").
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Retrieves the symbol associated with the operation.
     *
     * @return The symbol string (e.g., "+").
     */
    public String getSymbol() {
        return this.symbol;
    }
}
