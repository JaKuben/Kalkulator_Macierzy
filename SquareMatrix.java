package pl.jf.lab.model;
import java.util.List;
import java.util.ArrayList;
/**
 * Represents a square matrix (number of rows equals number of columns).
 * Extends the basic Matrix class.
 *
 * @author JakubFilipiak
 * @version 3.1
 */
public class SquareMatrix extends Matrix{
    
    /**
     * Constructs a SquareMatrix.
     *
     * @param data The 2D array data.
     * @throws MatrixDimensionException if the provided data does not form a square matrix.
     */
    
    public SquareMatrix(List<List<Double>> data) throws MatrixDimensionException{
       super(data);
       if(getCols()!=getRows())
       throw new MatrixDimensionException("Macierz nie jest kwadratowa");
    }
    /**
     * Calculates the determinant of this square matrix.
     * Uses a recursive Laplace expansion.
     *
     * @return The determinant value.
     */
    public double determinant() {
        return calculateDeterminant(this.getData());
    }
/**
     * Calculates the inverse of this matrix.
     *
     * @return A new SquareMatrix object representing the inverse.
     * @throws MatrixDimensionException if the matrix is singular (determinant is 0).
     */
    public SquareMatrix inverse() throws MatrixDimensionException {
        double det = determinant();
        
        if (det == 0) {
            throw new MatrixDimensionException("Macierz jest osobliwa (wyznacznik = 0), nie można odwrócić.");
        }

        int n = getRows();

       
        if (n == 1) {
            List<List<Double>> single = new ArrayList<>();
            List<Double> row = new ArrayList<>();
            row.add(1.0/det);
            single.add(row);
            return new SquareMatrix(single);
        }

       
        List<List<Double>> adjugateData = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            List<Double> row = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                List<List<Double>> minor = getSubmatrix(getData(), j, i); 
                double val = Math.pow(-1, i + j) * calculateDeterminant(minor);
                row.add(val);
            }
            adjugateData.add(row);
        }

        
        List<List<Double>> inverseData = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            List<Double> row = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                row.add(adjugateData.get(i).get(j)/det);
            }
            inverseData.add(row);
        }

        return new SquareMatrix(inverseData);
    }
    /**
     * Private recursive helper to calculate determinant of a given 2D array.
     *
     * @param matrixData The data of the matrix/submatrix.
     * @return The determinant.
     */
    private double calculateDeterminant(List<List<Double>> matrixData) {
        int n = matrixData.size();

        if (n == 0) {
            return 0;
        }

        if (n == 1) {
            return matrixData.get(0).get(0);
        }

        if (n == 2) {
            return matrixData.get(0).get(0) * matrixData.get(1).get(1) - matrixData.get(0).get(1) * matrixData.get(1).get(0);
        }

        double det = 0;
        for (int j = 0; j < n; j++) {
            det += Math.pow(-1, j) * matrixData.get(0).get(j) * calculateDeterminant(getSubmatrix(matrixData, 0, j));
        }
        return det;
    }

    /**
     * Creates a minor (submatrix) by removing a given row and column.
     *
     * @param matrixData The original matrix data.
     * @param rowToRemove The row index to remove.
     * @param colToRemove The column index to remove.
     * @return The (n-1)x(n-1) submatrix data.
     */
    private List<List<Double>> getSubmatrix(List<List<Double>> matrixData, int rowToRemove, int colToRemove) {
        int n = matrixData.size();
        List<List<Double>> submatrix = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (i == rowToRemove) {
                continue; 
            }
            List<Double> newRow = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if (j == colToRemove) {
                    continue;
                }
                newRow.add(matrixData.get(i).get(j));
            }
            submatrix.add(newRow);
        }
        return submatrix;
    }
}
