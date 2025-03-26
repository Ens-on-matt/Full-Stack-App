package com.example.sample_project.repository;

import com.example.sample_project.model.Staff;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.sample_project.model.Student;

import static com.example.sample_project.repository.GenericRowMappers.getIDRowMapper;
import static com.example.sample_project.repository.GenericRowMappers.getTableCountRowMapper;

@Slf4j
@Repository
public class StudentRepository {
    private final JdbcOperations jdbcOperations;

    @Autowired public StudentRepository(final JdbcOperations jdbcOperations) {
        log.info ("In Student Repository");
        this.jdbcOperations = jdbcOperations;
    }

    public List<Student> listAllStudents() {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT 
                      id,
                      name,
                      email,
                      phone_number,
                      degree_id
                FROM main.student
                WHERE 1=1
                """;

        log.debug("Query {}", sql);

        return getStudentMembers(sql, parameters, getStudentRowMapper());
    }

    public Optional<Student> getStudentById(@NonNull Integer id) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT 
                      id,
                      name,
                      email,
                      phone_number,
                      degree_id
                FROM main.student
                WHERE id=:student_id
                """;

        log.debug("Query {}", sql);

        parameters.addValue("student_id", id);

        return Optional.of(getStudentMembers(sql, parameters, getStudentRowMapper()).get(0));
    }

    public List<Student> getPageOfStudent(@NonNull Integer PageNo, @NonNull Integer PageSize, @NonNull Integer Offset) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
            SELECT 
                  id,
                  name,
                  email,
                  phone_number,
                  degree_id
            FROM main.student
            ORDER BY name ASC 
            OFFSET :EntryOffset ROWS
            FETCH FIRST :PageSize ROWS ONLY
                    """;

        log.debug("Query {}", sql);
        parameters.addValue("EntryOffset", PageNo*PageSize + Offset);
        parameters.addValue("PageSize", PageSize);

        return getStudentMembers(sql, parameters, getStudentRowMapper());
    }

    public List<Student> searchStudentMembers (@NonNull String searchTerm, @NonNull Integer PageSize) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """                
            SELECT 
                  id,
                  name,
                  email,
                  phone_number,
                  degree_id
            FROM main.student
            WHERE similarity(name, :searchTerm) > 0.05
            ORDER BY similarity(name, :searchTerm) DESC
            FETCH FIRST :PageSize ROWS ONLY
                """;

        parameters.addValue("searchTerm", searchTerm);
        parameters.addValue("PageSize", PageSize);
        return getStudentMembers(sql, parameters, getStudentRowMapper());
    }

    public Integer getSizeOfStudentTable() {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT COUNT(*) FROM main.student
                """;

        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        return namedJdbcTemplateObject.query(sql, parameters, getTableCountRowMapper()).get(0);
    }

    public Optional<Integer> saveNewStudent(@NonNull Student student) {
        log.info("Repository saveNewStudent called");

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                INSERT INTO main.student (
                                        name,
                                        email,
                                        phone_number,
                                        degree_id) 
                VALUES (
                        :name,
                        :email,
                        :phone_number,
                        :degree_id
                        ) 
                RETURNING id
                """;

        log.debug("Query {}", sql);

        parameters.addValue("name", student.getName());
        parameters.addValue("email", student.getEmail());
        parameters.addValue("phone_number", student.getPhone_number());
        parameters.addValue("degree_id", student.getDegree_id());

        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        return Optional.of(namedJdbcTemplateObject.query(sql, parameters, GenericRowMappers.getIDRowMapper()).get(0));
    }

    public Optional<Student> updateStudentMember(Student student) {
        log.info("Repository updateStudentMember called");

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                UPDATE main.student
                SET name=:name,
                    email=:email,
                    phone_number=:phone_number,
                    degree_id=:degree_id
                WHERE id=:id
                RETURNING *
                """;

        log.debug("Query {}", sql);

        parameters.addValue("id", student.getId());
        parameters.addValue("name", student.getName());
        parameters.addValue("email", student.getEmail());
        parameters.addValue("phone_number", student.getPhone_number());
        parameters.addValue("degree_id", student.getDegree_id());

        return Optional.of(getStudentMembers(sql, parameters, getStudentRowMapper()).get(0));
    }

    public Boolean deleteStudent(Integer id) {
        log.info("Repository deleteStudent called");
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                DELETE FROM main.student
                WHERE id = :id
                RETURNING *
                """;

        parameters.addValue("id", id);

        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        try {
            namedJdbcTemplateObject.query(sql, parameters, getStudentRowMapper());
            return true;
        } catch (Exception e) {
            log.info("Error when deleting student");
            return false;
        }
    }


    private List<Student> getStudentMembers(String sql, MapSqlParameterSource parameters, RowMapper<Student> rowMapper) {
        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        return namedJdbcTemplateObject.query(
                sql,
                parameters,
                rowMapper
        );
    }


    private RowMapper<Student> getStudentRowMapper() {
        return (ResultSet rs, int row) ->
                Student.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .email(rs.getString("email"))
                        .phone_number(rs.getString("phone_number"))
                        .degree_id(rs.getInt("degree_id"))
                        .build();
    }
}

