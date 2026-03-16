package pl.jf.lab.controller;

import jakarta.persistence.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;

/**
 * Lifecycle listener responsible for initializing the Persistence layer upon startup.
 * Ensures the EntityManagerFactory is created once for the entire application duration.
 *
 * @author JakubFilipiak
 * @version 1.5
 */
@WebListener
public class AppInitializer implements ServletContextListener {

    /**
     * Called when the web application is being initialized. 
     * Creates the EntityManagerFactory using parameters from web.xml.
     *
     * @param sce The {@link ServletContextEvent} containing information about the context.
     * @throws RuntimeException if the persistence unit cannot be initialized.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        String puName = sc.getInitParameter("persistenceUnitName");
        
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(puName);
            sc.setAttribute("emf", emf);
        } catch (Exception e) {
            sc.log("JPA Boot Error: " + e.getMessage());
            throw new RuntimeException("Persistence initialization failed", e);
        }
    }

    /**
     * Called when the web application is shut down.
     * Safely closes the global EntityManagerFactory.
     *
     * @param sce The {@link ServletContextEvent}.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        EntityManagerFactory emf = (EntityManagerFactory) sce.getServletContext().getAttribute("emf");
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}