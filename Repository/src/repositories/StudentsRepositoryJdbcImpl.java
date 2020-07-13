package repositories;

import models.Mentor;
import models.Student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class StudentsRepositoryJdbcImpl implements StudentsRepository {

    //language=SQL
    private static final String SQL_SELECT_BY_ID = "select *, mentor.id as m_id, mentor.first_name as m_first_name,mentor.last_name as m_last_name from student left join mentor on student.id = student_id where student.id = ";
    //language=SQL
    private static final String SQL_SELECT_BY_AGE = "select *, mentor.id as m_id, mentor.first_name as m_first_name,mentor.last_name as m_last_name from student left join mentor on student.id = student_id where age = ";
    private Connection connection;
    //language=SQL
    private static final String SQL_INSERT_1 = "insert into student(first_name,last_name,age,group_number) VALUES(";
    //language=SQL
    private static final String SQL_INSERT_2 = ") returning id";
    private static final String SQL_DELETE = "delete from mentor where student_id = ";
    //language=SQL
    private static final String SQL_UPDATE = "update student set first_name = %s, last_name = %s," +
            " age = %d, group_number = %d where id = %d ";
    private static final String SQL_SELECT_ALL = "select *, mentor.id as m_id, mentor.first_name as m_first_name,mentor.last_name as m_last_name" +
            " from student left join mentor on student.id = student_id " +
            "order by student.id";

    public StudentsRepositoryJdbcImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Student> findAllByAge(int age) {
        Statement statement = null;
        ResultSet result = null;
        List<Student> students = new ArrayList<>();
        Student s = null;
        try {
            statement = connection.createStatement();
            result = statement.executeQuery(SQL_SELECT_BY_AGE + age);
            long curId = 0;
            while (result.next()) {
                if (s == null || curId != s.getId())
                    s = (new Student(
                            result.getLong("id"),
                            result.getString("first_name"),
                            result.getString("last_name"),
                            result.getInt("age"),
                            result.getInt("group_number"),
                            new ArrayList<>()
                    ));

                if (result.getObject("m_id") != null) {
                    s.getMentors().add(new Mentor(
                            result.getLong("m_id"),
                            result.getString("m_first_name"),
                            result.getString("m_last_name"), s));

                }
                if (curId != s.getId())
                    students.add(s);
                curId = s.getId();
            }
            return students;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }

    // Необходимо вытащить список всех студентов, при этом у каждого студента должен быть проставлен список менторов
    // у менторов в свою очередь ничего проставлять (кроме имени, фамилии, id не надо)
    // student1(id, firstName, ..., mentors = [{id, firstName, lastName, null}, {}, ), student2, student3
    // все сделать одним запросом
    @Override
    public List<Student> findAll() {
        Statement statement = null;
        ResultSet result = null;
        List<Student> students = new ArrayList<>();
        Student s = null;

        try {
            statement = connection.createStatement();
            result = statement.executeQuery(SQL_SELECT_ALL);
            long curId = 0;
            while (result.next()) {
                if (s == null || curId != s.getId()) {
                    s = new Student(
                            result.getLong("id"),
                            result.getString("first_name"),
                            result.getString("last_name"),
                            result.getInt("age"),
                            result.getInt("group_number"),
                            new ArrayList<>()
                    );
                }
                if (result.getObject("m_id") != null) {
                    s.getMentors().add(new Mentor(
                            result.getLong("m_id"),
                            result.getString("m_first_name"),
                            result.getString("m_last_name"),
                            s
                    ));
                }
                if (curId != s.getId())
                    students.add(s);
                curId = result.getLong("id");
            }
            return students;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }

    @Override
    public Student findById(Long id) {
        Statement statement = null;
        ResultSet result = null;

        try {
            statement = connection.createStatement();
            result = statement.executeQuery(SQL_SELECT_BY_ID + id);
            if (result.next()) {
                Student s = new Student(
                        result.getLong("id"),
                        result.getString("first_name"),
                        result.getString("last_name"),
                        result.getInt("age"),
                        result.getInt("group_number"),
                        new ArrayList<>()
                );
                if (result.getObject("m_id") != null)
                    s.getMentors().add(new Mentor(
                            result.getLong("m_id"),
                            result.getString("m_first_name"),
                            result.getString("m_last_name"), s));
                return s;
            } else return null;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }

    // просто вызывается insert для сущности
    // student = Student(null, 'Марсель', 'Сидиков', 26, 915)
    // studentsRepository.save(student);
    // // student = Student(3, 'Марсель', 'Сидиков', 26, 915)
    @Override
    public void save(Student entity) {
        Statement statement = null;
        ResultSet result = null;

        try {
            statement = connection.createStatement();
            result = statement.executeQuery(SQL_INSERT_1
                    + ((entity.getFirstName() == null) ? null : ('\'' + entity.getFirstName() + '\''))
                    + ','
                    + ((entity.getLastName() == null) ? null : ('\'' + entity.getLastName() + '\''))
                    + ','
                    + entity.getAge()
                    + ','
                    + entity.getGroupNumber()
                    + SQL_INSERT_2
            );
            if (result.next()) {
                entity.setId(result.getLong("id"));
            }


            List<Mentor> mentors = entity.getMentors();
            if (!mentors.isEmpty()) {
                StringBuilder SQL_SAVE_MENTORS = new StringBuilder("insert into mentor(first_name,last_name,student_id) values");
                for (Mentor m : mentors) {
                    SQL_SAVE_MENTORS.append("(").append((m.getFirstName() == null) ? null : ('\'' + m.getFirstName() + '\''))
                            .append(',').append((m.getLastName() == null) ? null : ('\'' + m.getLastName() + '\''))
                            .append(',')
                            .append(entity.getId())
                            .append(')');
                    SQL_SAVE_MENTORS.append(',');
                }
                SQL_SAVE_MENTORS.deleteCharAt(SQL_SAVE_MENTORS.length() - 1);
                SQL_SAVE_MENTORS.append(" returning id;");
                String SQL_SAVE_MENTORS1 = SQL_SAVE_MENTORS.toString();
                result = statement.executeQuery(SQL_SAVE_MENTORS1);
                int i = 0;
                while (result.next()) {
                    mentors.get(i).setId(result.getLong("id"));
                    i++;
                }
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }

    // для сущности, у которой задан id выполнить обновление всех полей

    // student = Student(3, 'Марсель', 'Сидиков', 26, 915)
    // student.setFirstName("Игорь")
    // student.setLastName(null);
    // studentsRepository.update(student);
    // (3, 'Игорь', null, 26, 915)

    @Override
    public void update(Student entity) {
        Statement statement = null;

        try {
            statement = connection.createStatement();
            statement.executeUpdate(String.format(SQL_UPDATE,
                    ((entity.getFirstName() == null) ? null : ('\'' + entity.getFirstName() + '\'')),
                    ((entity.getLastName() == null) ? null : ('\'' + entity.getLastName() + '\'')),
                    entity.getAge(),
                    entity.getGroupNumber(),
                    entity.getId()));

//            statement.executeUpdate(SQL_DELETE + entity.getId());
//
//            List<Mentor> mentors = entity.getMentors();
//            if (!mentors.isEmpty()) {
//                StringBuilder SQL_SAVE_MENTORS = new StringBuilder("insert into mentor(id,first_name,last_name,student_id) values");
//                for (Mentor m : mentors) {
//                    SQL_SAVE_MENTORS.append("(").append(m.getId()).append(',')
//                            .append((m.getFirstName() == null) ? null : ('\'' + m.getFirstName() + '\''))
//                            .append(',')
//                            .append((m.getLastName() == null) ? null : ('\'' + m.getLastName() + '\''))
//                            .append(',')
//                            .append(entity.getId())
//                            .append(')');
//                    SQL_SAVE_MENTORS.append(',');
//                }
//                SQL_SAVE_MENTORS.deleteCharAt(SQL_SAVE_MENTORS.length() - 1);
//
//                SQL_SAVE_MENTORS.append(";");
//                statement.executeUpdate(SQL_SAVE_MENTORS.toString());
//            }

        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }
}
