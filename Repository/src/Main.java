
import jdbc.SimpleDataSource;
import models.Mentor;
import models.Student;
import repositories.StudentsRepository;
import repositories.StudentsRepositoryJdbcImpl;


import java.sql.*;
import java.util.*;

public class Main {

    private static final String URL = "jdbc:postgresql://localhost:5432/java_lab_prart_20";
    private static final String USER = "postgres";
    private static final String PASSWORD = "384953";


    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        StudentsRepository studentsRepository = new StudentsRepositoryJdbcImpl(connection);
        System.out.println(studentsRepository.findById(1L));
        System.out.println(studentsRepository.findAllByAge(19));
        List<Mentor> mentors = new ArrayList<>();
        mentors.add(new Mentor(2345L,"Ментор 2345", "Ментор 2345"));
        Student student = new Student("Максим", "Иванов", 19, 904, mentors);
        studentsRepository.save(student);
        Student student1 = new Student(22L,"Макc", "Иванов", 19, 904, mentors);
        studentsRepository.update(student1);
        System.out.println(studentsRepository.findAll());
        connection.close();

    }
}
