package pl.jf.lab.model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
/**
 * Represents a mathematical matrix.
 * This class handles the operations of a matrix,
 * while composing a MatrixData object to hold its STATE.
 *
 * @author JakubFilipiak
 * @version 4.0
 */
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Matrix {

    /**
     * Composition: The Matrix class holds a MatrixData record.
     */
    private final MatrixData matrixData;

    /**
     * Constructor creating a Matrix from a List of Lists.
     * @param data List of Lists of Doubles.
     */
    public Matrix(List<List<Double>> data) {
        int r = data.size();
        int c = (r > 0) ? data.get(0).size() : 0;
        this.matrixData = new MatrixData(data,r,c);
    }
    
    /**
     * Factory method using VarArgs.
     *
     * @param rows Variable number of rows (Lists).
     * @return New Matrix object.
     */
    @SafeVarargs
    public static Matrix of(List<Double>... rows) {
        return new Matrix(Arrays.asList(rows));
    }
    /**
     * Gets the number of rows in the matrix.
     * Delegates the call to the underlying MatrixData record.
     *
     * @return The number of rows.
     */
    public int getRows() { return matrixData.rows(); }

    /**
     * Gets the number of columns in the matrix.
     * Delegates the call to the underlying MatrixData record.
     *
     * @return The number of columns.
     */
    public int getCols() { return matrixData.cols(); }

    /**
     * Retrieves the raw matrix data structure.
     *
     * @return The matrix elements as a {@code List<List<Double>>}.
     */
    public List<List<Double>> getData() { return matrixData.data(); }
    
    
    /**
     * Adds another matrix to this matrix.
     *
     * @param other The matrix to add.
     * @return A new Matrix object representing the sum.
     * @throws MatrixDimensionException if the matrices have different dimensions.
     */
    public Matrix add(Matrix other) throws MatrixDimensionException {
        if (getRows() != other.getRows() || getCols() != other.getCols()) {
            throw new MatrixDimensionException("Wymiary niezgodne do dodawania.");
        }  
        List<List<Double>> resultList = IntStream.range(0, getRows())
            .mapToObj(i -> IntStream.range(0, getCols())
                .mapToObj(j -> this.getData().get(i).get(j) + other.getData().get(i).get(j))
                .collect(Collectors.toList()))
            .collect(Collectors.toList());

        return new Matrix(resultList);
    }

    /**
     * Subtracts another matrix from this matrix.
     *
     * @param other The matrix to subtract.
     * @return A new Matrix object representing the difference.
     * @throws MatrixDimensionException if the matrices have different dimensions.
     */
    public Matrix subtract(Matrix other) throws MatrixDimensionException {
        if (getRows() != other.getRows() || getCols() != other.getCols()) {
            throw new MatrixDimensionException("Macierze nie mają tych samych wymiarów do odejmowania!");
        }
        
        List<List<Double>> resultList = IntStream.range(0, getRows())
            .mapToObj(i -> IntStream.range(0, getCols())
                .mapToObj(j -> this.getData().get(i).get(j) - other.getData().get(i).get(j))
                .collect(Collectors.toList()))
            .collect(Collectors.toList());
        
        return new Matrix(resultList);
    }

    /**
     * Multiplies this matrix by another matrix.
     *
     * @param other The matrix to multiply by.
     * @return A new Matrix object representing the product.
     * @throws MatrixDimensionException if this.cols does not equal other.rows.
     */
    public Matrix multiply(Matrix other) throws MatrixDimensionException {
        if (this.getCols() != other.getRows()) {
            throw new MatrixDimensionException("Liczba kolumn macierzy A musi być równa liczbie wierszy macierzy B!");
        }

        List<List<Double>> res = new ArrayList<>();

        for (int i = 0; i < getRows(); i++) {
            List<Double> row = new ArrayList<>();
            for (int j = 0; j < other.getCols(); j++) {
                double sum = 0;
                for (int k = 0; k < getCols(); k++) {
                    sum += this.getData().get(i).get(k) * other.getData().get(k).get(j);
                }
                row.add(sum);
            }
            res.add(row);   
        }
        return new Matrix(res);
    }
    @Override
    public int hashCode() {
        return matrixData.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix = (Matrix) o;
        return this.matrixData.equals(matrix.matrixData);
    }
    @Override
    public String toString() {
        return "Matrix{" +
                "rows=" + getRows() +
                ", cols=" + getCols() +
                ", data=" + getData() +
                '}';
    }
}