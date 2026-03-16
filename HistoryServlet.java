package pl.jf.lab.controller;

import jakarta.persistence.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.net.URLDecoder;
import java.time.format.DateTimeFormatter;
import java.util.List;
import pl.jf.lab.db.*;

/**
 * Controller responsible for retrieving and displaying the full calculation history.
 * It fetches data from the relational database via JPA and renders a styled HTML view
 * without client-side scripting.
 *
 * @author JakubFilipiak
 * @version 4.0
 */
@WebServlet(name = "HistoryServlet", urlPatterns = {"/history"})
public class HistoryServlet extends HttpServlet {

    /**
     * Handles the GET request to display history.
     * Retrieving data from Cookies and Database, then merging them into a single view.
     * Parses raw database strings into visual HTML matrices.
     *
     * @param request  the {@link HttpServletRequest}
     * @param response the {@link HttpServletResponse}
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String lastOp = "Brak danych (pusta sesja)";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("lastOp".equals(c.getName())) {
                    lastOp = URLDecoder.decode(c.getValue(), "UTF-8");
                }
            }
        }

        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        EntityManager em = emf.createEntityManager();

        out.println("<html><head><title>Historia Obliczeń</title>");
        out.println("<link rel='stylesheet' type='text/css' href='style.css'></head><body>");
        
        out.println("<div class='container'>");
        out.println("<h1>Pełna Historia Obliczeń</h1>");

        out.println("<div class='info-box' style='background-color: #fff9f0; border-color: #f39c12; color: #d35400;'>");
        out.println("<span class='source-tag tag-cookie'>Źródło: Przeglądarka (Cookie)</span><br>");
        out.println("Ostatnia operacja wykonana w tej przeglądarce: <b>" + lastOp + "</b>");
        out.println("</div>");

        out.println("<h3>Zapisane rekordy</h3>");
        out.println("<span class='source-tag tag-db'>Źródło: Relacyjna Baza Danych (Derby)</span>");

        try {
            List<CalculationEntity> results = em.createQuery(
                "SELECT c FROM CalculationEntity c ORDER BY c.timestamp DESC", 
                CalculationEntity.class).getResultList();

            if (results.isEmpty()) {
                out.println("<div class='info-box'>Baza danych jest pusta. Wykonaj pierwsze obliczenia.</div>");
            } else {
                out.println("<table>");
                out.println("<tr>");
                out.println("<th style='width: 5%'>ID</th>");
                out.println("<th style='width: 20%'>Data i Czas</th>");
                out.println("<th style='width: 15%'>Operacja</th>");
                out.println("<th>Szczegóły (Macierze i Wyniki)</th>");
                out.println("</tr>");
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                for (CalculationEntity entity : results) {
                    out.println("<tr>");
                    out.println("<td>" + entity.getId() + "</td>");
                    out.println("<td>" + entity.getTimestamp().format(formatter) + "</td>");
                    out.println("<td><strong>" + entity.getOperationName() + "</strong></td>");
                    
                    out.println("<td style='text-align: left;'>");
                    for (MatrixDataEntity m : entity.getMatrices()) {
                        String friendlyType = translateType(m.getType());
                        String formattedMatrix = formatMatrixToHtml(m.getRawData());
                        
                        out.println("<div style='margin-bottom: 8px; display: flex; align-items: center;'>");
                        out.println("<span style='width: 130px; font-weight: bold; color: #555;'>" 
                                    + friendlyType + ":</span> ");
                        out.println(formattedMatrix);
                        out.println("</div>");
                    }
                    out.println("</td>");
                    out.println("</tr>");
                }
                out.println("</table>");
            }

        } catch (Exception e) {
            out.println("<div class='error-box'>Błąd odczytu bazy danych: " + e.getMessage() + "</div>");
        } finally {
            em.close();
        }

        out.println("<br><div style='text-align: center;'>");
        out.println("<a href='index.html'>← Powrót do kalkulatora</a>");
        out.println("</div></div>");
        out.println("</body></html>");
    }

    /**
     * Converts raw database strings (CSV or Java toString) into a styled HTML matrix table.
     * Handles "1,2;3,4" and "Matrix{... data=[[1,2],[3,4]]}" formats.
     */
    private String formatMatrixToHtml(String rawData) {
        if (rawData == null) return "";
        
        String cleanData = rawData;

        if (rawData.startsWith("Matrix{") || rawData.contains("data=[[")) {
            try {
                int start = rawData.indexOf("[[");
                int end = rawData.lastIndexOf("]]");
                if (start != -1 && end != -1) {
                    String content = rawData.substring(start + 2, end);
                    content = content.replace("], [", ";");
                    content = content.replace("],[", ";"); 
                    cleanData = content;
                }
            } catch (Exception e) {
                return "<code>" + rawData + "</code>";
            }
        }

        if (cleanData.contains(";") || cleanData.contains(",")) {
            StringBuilder sb = new StringBuilder();
            sb.append("<div class='matrix-render'><table>");
            
            String[] rows = cleanData.split(";");
            for (String row : rows) {
                sb.append("<tr>");
                String[] cols = row.split(",");
                for (String col : cols) {
                    String val = col.trim();
                    try {
                        double d = Double.parseDouble(val);
                        if (val.contains(".")) val = String.format("%.2f", d);
                    } catch (NumberFormatException ignored) {}
                    
                    sb.append("<td>").append(val).append("</td>");
                }
                sb.append("</tr>");
            }
            sb.append("</table></div>");
            return sb.toString();
        } 
        
        return "<b>" + cleanData + "</b>";
    }

    /**
     * Translates internal matrix type codes into user-friendly Polish labels.
     */
    private String translateType(String type) {
        if (type == null) return "Nieznany";
        switch (type) {
            case "WEJŚCIE_A": return "Wejście A";
            case "WEJŚCIE_B": return "Wejście B";
            case "WYNIK_MACIERZ": return "Wynik";
            case "WYNIK_SKALAR": return "Wynik";
            case "INPUT_A": return "Wejście A"; 
            case "INPUT_B": return "Wejście B";
            default: return type;
        }
    }
}