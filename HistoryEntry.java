package pl.jf.lab.model;

/**
 * Represents a single record in the calculation history..
 *
 * @author JakubFilipiak
 * @version 1.1
 */
public class HistoryEntry {
    
    /** The first operand (Matrix A). */
    private final Matrix matrixA;
    
    /** The second operand (Matrix B), can be null. */
    private final Matrix matrixB;
    
    /** The operation performed. */
    private final Operation operation;
    
    /** The result of the operation (Matrix object). Null if result is a scalar. */
    private final Matrix resultMatrix;
    
    /** The result of the operation (Scalar value). Null if result is a matrix. */
    private final Double resultValue;

    /**
     * Constructs a new history entry.
     * * @param matrixA      The first matrix.
     * @param matrixB      The second matrix (or null).
     * @param operation    The operation performed.
     * @param resultMatrix The result matrix (or null).
     * @param resultValue  The result value (or null).
     */
    public HistoryEntry(Matrix matrixA, Matrix matrixB, Operation operation, Matrix resultMatrix, Double resultValue) {
        this.matrixA = matrixA;
        this.matrixB = matrixB;
        this.operation = operation;
        this.resultMatrix = resultMatrix;
        this.resultValue = resultValue;
    }


 /**
     * Retrieves the first matrix operand used in the calculation.
     *
     * @return The first matrix (Matrix A).
     */
    public Matrix getMatrixA() {
        return matrixA;
    }

    /**
     * Retrieves the second matrix operand used in the calculation.
     * This may be null if the operation was unary (e.g., determinant, inverse).
     *
     * @return The second matrix (Matrix B) or null.
     */
    public Matrix getMatrixB() {
        return matrixB;
    }

    /**
     * Retrieves the operation type performed.
     *
     * @return The {@link Operation} enum constant.
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * Retrieves the matrix result of the operation.
     * This will be null if the result is a scalar value (e.g., determinant).
     *
     * @return The resulting {@link Matrix} or null.
     */
    public Matrix getResultMatrix() {
        return resultMatrix;
    }

    /**
     * Retrieves the scalar result of the operation.
     * This will be null if the result is a matrix (e.g., addition, multiplication).
     *
     * @return The resulting {@link Double} value or null.
     */
    public Double getResultValue() {
        return resultValue;
    }
    
    /**
     * Returns a string representation of the history entry.
     *
     * @return A string containing the operation and result summary.
     */
    @Override
    public String toString() {
        return "HistoryEntry{" + "op=" + operation + ", result=" + (resultValue != null ? resultValue : "Matrix") + '}';
    }
}