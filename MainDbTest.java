package pl.jf.lab.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;


public class MainDbTest {

    public static void main(String[] args) {
        System.out.println("--- START TESTU JPA ---");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("MatrixPU");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        CalculationEntity calc = new CalculationEntity("ADD");

   
        calc.addMatrix(new MatrixDataEntity(2, 2, "1,2;3,4", "INPUT_A"));

        calc.addMatrix(new MatrixDataEntity(2, 2, "5,6;7,8", "INPUT_B"));
        
        calc.addMatrix(new MatrixDataEntity(2, 2, "6,8;10,12", "RESULT"));

        em.persist(calc);

        em.getTransaction().commit();
        System.out.println("Zapisano dane do bazy! ID = " + calc.getId());

        em.clear();
        System.out.println("\n--- ODCZYT Z BAZY ---");
        
        List<CalculationEntity> results = em.createQuery("SELECT c FROM CalculationEntity c", CalculationEntity.class)
                                            .getResultList();

        for (CalculationEntity entity : results) {
            System.out.println("Obliczenie: " + entity);
            for (MatrixDataEntity m : entity.getMatrices()) {
                System.out.println(" -> " + m);
            }
        }

        em.close();
        emf.close();
        System.out.println("--- KONIEC TESTU JPA ---");
    }
}