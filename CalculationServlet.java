package pl.jf.lab.controller;

import jakarta.persistence.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import pl.jf.lab.db.*;
import pl.jf.lab.model.*;

/**
 * Controller servlet for matrix calculations and result presentation.
 * This servlet processes input, executes mathematical operations, 
 * persists data to the database, and renders the final result view.
 * * @author JakubFilipiak
 * @version 3.3
 */
@WebServlet(name = "CalculationServlet", urlPatterns = {"/calculate"})
public class CalculationServlet extends HttpServlet {

    /**
     * Handles POST requests. Performs calculation, saves to DB, 
     * and generates a detailed HTML response with the result.
     *
     * @param request  the {@link HttpServletRequest}
     * @param response the {@link HttpServletResponse}
     * @throws ServletException for servlet-related errors.
     * @throws IOException for I/O related errors.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String rawA = request.getParameter("matrixA");
        String rawB = request.getParameter("matrixB");
        String opType = request.getParameter("operation");

        String previousOp = "Brak (pierwsze uruchomienie)";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("lastOp".equals(c.getName())) {
                    previousOp = URLDecoder.decode(c.getValue(), "UTF-8");
                }
            }
        }

        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        EntityManager em = emf.createEntityManager();
        
        String resultHtml = "";
        String statusMessage = "";

        try {
            Matrix matrixA = parseMatrix(rawA);
            CalculationEntity calculation = new CalculationEntity(opType);
            calculation.addMatrix(new MatrixDataEntity(matrixA.getRows(), matrixA.getCols(), rawA, "WEJŚCIE_A"));

            em.getTransaction().begin();

            if (opType.equals("DETERMINANT")) {
                SquareMatrix sA = new SquareMatrix(matrixA.getData());
                double det = sA.determinant();
                resultHtml = "<h3>Wyznacznik (det A): " + det + "</h3>";
                statusMessage = "Obliczono wyznacznik.";
                calculation.addMatrix(new MatrixDataEntity(1, 1, String.valueOf(det), "WYNIK_SKALAR"));
            } 
            else if (opType.equals("INVERSE")) {
                SquareMatrix sA = new SquareMatrix(matrixA.getData());
                SquareMatrix inv = sA.inverse();
                resultHtml = "<h3>Macierz odwrotna (A⁻¹):</h3>" + matrixToHtml(inv.getData());
                statusMessage = "Obliczono macierz odwrotną.";
                calculation.addMatrix(new MatrixDataEntity(inv.getRows(), inv.getCols(), inv.toString(), "WYNIK_MACIERZ"));
            } 
            else {
                Matrix matrixB = parseMatrix(rawB);
                calculation.addMatrix(new MatrixDataEntity(matrixB.getRows(), matrixB.getCols(), rawB, "WEJŚCIE_B"));
                
                Matrix resultMatrix;
                switch (opType) {
                    case "ADD": resultMatrix = matrixA.add(matrixB); break;
                    case "SUBTRACT": resultMatrix = matrixA.subtract(matrixB); break;
                    case "MULTIPLY": resultMatrix = matrixA.multiply(matrixB); break;
                    default: throw new Exception("Nieznana operacja.");
                }
                
                resultHtml = "<h3>Wynik operacji " + opType + ":</h3>" + matrixToHtml(resultMatrix.getData());
                statusMessage = "Wykonano operację dwuargumentową.";
                calculation.addMatrix(new MatrixDataEntity(resultMatrix.getRows(), resultMatrix.getCols(), resultMatrix.toString(), "WYNIK_MACIERZ"));
            }

            em.persist(calculation);
            em.getTransaction().commit();

            response.addCookie(new Cookie("lastOp", URLEncoder.encode(opType, "UTF-8")));

            out.println("<html><head><link rel='stylesheet' type='text/css' href='style.css'></head><body>");
            out.println("<div class='container'>");
            out.println("<h1>Wynik Obliczeń</h1>");

            out.println("<div class='info-box' style='background-color: #fff9f0; border-color: #f39c12; color: #d35400;'>");
            out.println("<span class='source-tag tag-cookie'>Źródło: Ciastko</span><br>");
            out.println("Twoja poprzednia operacja: <b>" + previousOp + "</b>");
            out.println("</div>");

            out.println("<div class='success-box'>");
            out.println("<span class='source-tag tag-db'>Źródło: Model & Baza Danych</span><br>");
            out.println(resultHtml);
            out.println("<hr><p style='font-size: 12px;'>Status: " + statusMessage + " i zapisano w bazie.</p>");
            out.println("</div>");

            out.println("<br><div style='text-align: center;'>");
            out.println("<a href='index.html'>← Nowe obliczenie</a> | ");
            out.println("<a href='history'>Pełna historia wpisów →</a>");
            out.println("</div></div></body></html>");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            renderError(out, e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Formats matrix data provided as a List of Lists into an HTML table.
     * Iterates directly over the collection to avoid double[][] conversion.
     *
     * @param matrixData The nested list structure containing matrix elements.
     * @return A string containing the HTML table representation of the matrix.
     */
    private String matrixToHtml(List<List<Double>> matrixData) {
       if (matrixData == null || matrixData.isEmpty()) return "<p>Brak danych macierzy.</p>";
        
        StringBuilder sb = new StringBuilder("<table>");
        for (List<Double> row : matrixData) {
            sb.append("<tr>");
            for (Double value : row) {
                sb.append("<td>").append(value).append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    /**
     * Parses a string "1,2;3,4" into a Matrix object.
     * * @param input Raw input string.
     * @return Matrix object.
     * @throws Exception if parsing fails.
     */
    private Matrix parseMatrix(String raw) {
        List<List<Double>> data = new ArrayList<>();
        String[] rows = raw.split(";");
        for (String r : rows) {
            List<Double> rowData = new ArrayList<>();
            String[] cols = r.split(",");
            for (String c : cols) {
                try {
                    rowData.add(Double.parseDouble(c.trim()));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Nieprawidlowy format liczby: '" + c + "'");
                }
            }
            data.add(rowData);
        }
        return new Matrix(data);
    }

    /**
     * Standardized error renderer.
     * * @param out Writer.
     * @param msg Error message.
     */
    private void renderError(PrintWriter out, String msg) {
        out.println("<html><head><link rel='stylesheet' type='text/css' href='style.css'></head><body>");
        out.println("<div class='container'><div class='error-box'>");
        out.println("<strong>Błąd:</strong> " + msg);
        out.println("</div><br><a href='index.html'>Powrót</a></div></body></html>");
    }
}