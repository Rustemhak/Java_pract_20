package jdbc;


import java.sql.*;

public class Main {

    private static final String URL = "jdbc:postgresql://localhost:5432/java_lab_pract_2020";
    private static final String USER = "postgres";
    private static final String PASSWORD = "qwerty007";


    public static void main(String[] args) throws SQLException {
        SimpleDataSource dataSource = new SimpleDataSource();
        // подключение к базе данных
        Connection connection = dataSource.openConnection(URL, USER, PASSWORD);
        // создаем выражение для отправки запросов в бд
        Statement statement = connection.createStatement();
        // получаем результат запроса
        ResultSet resultSet = statement.executeQuery("select * from student");
        // пробегаем по результирующему множеству
        while (resultSet.next()) {
            // выводим информацию по каждому столбцу каждой строки
            System.out.println("ID " + resultSet.getInt("id"));
            System.out.println("First Name " + resultSet.getString("first_name"));
            System.out.println("Last Name " + resultSet.getString("last_name"));
            System.out.println("Age " + resultSet.getInt("age"));
            System.out.println("Group Number " + resultSet.getInt("group_number"));
        }
        System.out.println("-------------------");

        resultSet.close();

        resultSet = statement.executeQuery("select s.id as s_id, *\n" +
                "from student s\n" +
                "         full outer join mentor m on s.id = m." +
                "student_id;");

        while (resultSet.next()) {
            System.out.println("ID " + resultSet.getInt("s_id"));
        }

        connection.close();
    }
}
