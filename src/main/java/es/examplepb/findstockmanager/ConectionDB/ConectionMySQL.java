package es.examplepb.findstockmanager.ConectionDB;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConectionMySQL {
        private static final String url = "jdbc:mysql://localhost:3306/datafsm";
        private static final String user = "root";
        private static final String password = "1234567fsm.";

        public static void main(String[] args) {
            try {
                Connection connection = DriverManager.getConnection(url, user, password);
                if (connection != null) {
                    System.out.println("Conexión exitosa a la base de datos.");
                } else {
                    System.out.println("Error al conectar a la base de datos.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
