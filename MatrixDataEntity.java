package pl.jf.lab.db;

import jakarta.persistence.*;

/**
 * Persistence entity representing individual matrix metadata and contents.
 * Maps to the MATRIX_DATA table and avoids SQL reserved keywords like 'ROWS'.
 *
 * @author JakubFilipiak
 * @version 2.1
 */
@Entity
@Table(name = "MATRIX_DATA")
public class MatrixDataEntity {

    /** Unique primary key for the matrix entry. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Number of rows in the matrix. */
    @Column(name = "ROW_COUNT")
    private int rowCount;

    /** Number of columns in the matrix. */
    @Column(name = "COLUMN_COUNT")
    private int colCount;

    /** String representation of matrix data (e.g., "1,2;3,4"). */
    private String rawData;

    /** Type classification of the matrix (e.g., INPUT_A, INPUT_B, RESULT). */
    private String type;

    /** Reference to the parent calculation session. */
    @ManyToOne
    @JoinColumn(name = "CALCULATION_ID")
    private CalculationEntity calculation;

    /**
     * Default constructor required by the JPA specification.
     */
    public MatrixDataEntity() {}

    /**
     * Constructs a matrix entity with specified dimensions and data.
     *
     * @param rowCount Number of rows in the matrix.
     * @param colCount Number of columns in the matrix.
     * @param rawData  String format of the matrix elements.
     * @param type     Category of the matrix (input or output).
     */
    public MatrixDataEntity(int rowCount, int colCount, String rawData, String type) {
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.rawData = rawData;
        this.type = type;
    }

    /**
     * Assigns this matrix to a specific calculation record.
     *
     * @param calculation The parent {@link CalculationEntity}.
     */
    public void setCalculation(CalculationEntity calculation) {
        this.calculation = calculation;
    }

    /**
     * Retrieves the raw data string of the matrix.
     *
     * @return String of matrix elements.
     */
    public String getRawData() { return rawData; }

    /**
     * Retrieves the matrix role type.
     *
     * @return String describing the type.
     */
    public String getType() { return type; }
}