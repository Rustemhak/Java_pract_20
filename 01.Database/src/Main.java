

import java.sql.*;

public class Main {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "384953";


    public static void main(String[] args) throws SQLException {
        SimpleDataSource dataSource = new SimpleDataSource();
        // подключение к базе данных
        Connection connection = dataSource.openConnection(URL, USER, PASSWORD);
        // создаем выражение для отправки запросов в бд
        Statement statement = connection.createStatement();
        // получаем результат запроса
        ResultSet resultSet = statement.executeQuery("select * from exam");
        // пробегаем по результирующему множеству
        while (resultSet.next()) {
            // выводим информацию по каждому столбцу каждой строки
            System.out.println("ID " + resultSet.getInt("id"));
            System.out.println("Title " + resultSet.getString("title"));
            System.out.println("Day " + resultSet.getInt("day_number"));
//            System.out.println("Age " + resultSet.getInt("age"));
//            System.out.println("Group Number " + resultSet.getInt("group_number"));
        }
        System.out.println("-------------------");

        resultSet.close();

        resultSet = statement.executeQuery("select m.value as m_value, exam_id,student_id,last_name   from mark as m  left join student s on s.id = m.student_id;");

        while (resultSet.next()) {
            System.out.println("value  " + resultSet.getInt("m_value"));
            System.out.println("Exam ID " + resultSet.getInt("exam_id"));
            System.out.println("Student ID " + resultSet.getInt("student_id"));
            System.out.println("Student Last Name " + resultSet.getString("last_name"));
            System.out.println();
        }

        connection.close();
    }
}
