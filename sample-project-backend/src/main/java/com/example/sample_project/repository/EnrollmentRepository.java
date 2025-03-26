package com.example.sample_project.repository;

import com.example.sample_project.model.Enrollment;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

import static com.example.sample_project.repository.GenericRowMappers.getTableCountRowMapper;

@Slf4j
@Repository
public class EnrollmentRepository {
    private final JdbcOperations jdbcOperations;

    @Autowired public EnrollmentRepository(final JdbcOperations jdbcOperations) {
        log.info ("In Enrollment Repository");
        this.jdbcOperations = jdbcOperations;
    }

    public List<Enrollment> listAllEnrollments() {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT 
                      id,
                      student,
                      course,
                      status
                FROM main.enrollment
                WHERE 1=1
                """;

        log.debug("Query {}", sql);

        return getEnrollments(sql, parameters, getEnrollmentRowMapper());
    }

    public Optional<Enrollment> getEnrollmentById(@NonNull Integer id) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT 
                      id,
                      student,
                      course,
                      status
                FROM main.enrollment
                WHERE id=:enrollment_id
                """;

        log.debug("Query {}", sql);

        parameters.addValue("enrollment_id", id);

        return Optional.of(getEnrollments(sql, parameters, getEnrollmentRowMapper()).get(0));
    }

    public List<Enrollment> listAllEnrollmentsForStudent(Integer id) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT 
                      id,
                      student,
                      course,
                      status
                FROM main.enrollment
                WHERE student=:student_id
                """;

        parameters.addValue("student_id", id);

        return getEnrollments(sql, parameters, getEnrollmentRowMapper());
    }

    public List<Enrollment> listAllEnrollmentsInCourse(Integer id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT 
                      id,
                      student,
                      course,
                      status
                FROM main.enrollment
                WHERE course=:course_id
                """;

        parameters.addValue("course_id", id);

        return getEnrollments(sql, parameters, getEnrollmentRowMapper());
    }

    public List<Enrollment> getPageOfEnrollment(@NonNull Integer PageNo, @NonNull Integer PageSize, @NonNull Integer Offset) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
            SELECT 
                  id,
                  student,
                  course,
                  status
            FROM main.enrollment
            ORDER BY id ASC 
            OFFSET :EntryOffset ROWS
            FETCH FIRST :PageSize ROWS ONLY
                    """;

        log.debug("Query {}", sql);
        parameters.addValue("EntryOffset", PageNo*PageSize + Offset);
        parameters.addValue("PageSize", PageSize);

        return getEnrollments(sql, parameters, getEnrollmentRowMapper());
    }

    public List<Enrollment> searchEnrollments (@NonNull String searchTerm, @NonNull Integer PageSize) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """                
            SELECT 
                  id,
                  student,
                  course,
                  status
            FROM main.enrollment
            ORDER BY similarity(name, :searchTerm) DESC
            FETCH FIRST :PageSize ROWS ONLY
                """;

        parameters.addValue("searchTerm", searchTerm);
        parameters.addValue("PageSize", PageSize);
        return getEnrollments(sql, parameters, getEnrollmentRowMapper());
    }

    public Optional<Integer> saveNewEnrollment(@NonNull Enrollment enrollment) {
        log.info("Repository saveNewEnrollment called");

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                INSERT INTO main.enrollment (
                                        student,
                                        course,
                                        status) 
                VALUES (
                        :student,
                        :course,
                        :status
                        ) 
                RETURNING id
                """;

        log.debug("Query {}", sql);

        parameters.addValue("student", enrollment.getStudent());
        parameters.addValue("course", enrollment.getCourse());
        parameters.addValue("status", enrollment.getStatus());

        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        return Optional.of(namedJdbcTemplateObject.query(sql, parameters, GenericRowMappers.getIDRowMapper()).get(0));
    }

    public Optional<Enrollment> updateEnrollmentMember(Enrollment enrollment) {
        log.info("Repository updateEnrollmentMember called");

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                UPDATE main.enrollment
                SET student=:student,
                    course=:course,
                    status=:status
                WHERE id=:id
                RETURNING *
                """;

        log.debug("Query {}", sql);

        parameters.addValue("id", enrollment.getId());
        parameters.addValue("student", enrollment.getStudent());
        parameters.addValue("course", enrollment.getCourse());
        parameters.addValue("status", enrollment.getStatus());

        return Optional.of(getEnrollments(sql, parameters, getEnrollmentRowMapper()).get(0));
    }

    public Integer getSizeOfEnrollmentTable() {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT COUNT(*) FROM main.enrollment
                """;

        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        return namedJdbcTemplateObject.query(sql, parameters, getTableCountRowMapper()).get(0);
    }

    public Boolean deleteEnrollment(Integer id) {
        log.info("Repository deleteEnrollment called");
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                DELETE FROM main.enrollment
                WHERE id = :id
                RETURNING *
                """;

        parameters.addValue("id", id);

        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        try {
            namedJdbcTemplateObject.query(sql, parameters, getEnrollmentRowMapper());
            return true;
        } catch (Exception e) {
            log.info("Error when deleting enrollment");
            return false;
        }
    }

    private List<Enrollment> getEnrollments(String sql, MapSqlParameterSource parameters, RowMapper<Enrollment> rowMapper) {
        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        return namedJdbcTemplateObject.query(
                sql,
                parameters,
                rowMapper
        );
    }

    private RowMapper<Enrollment> getEnrollmentRowMapper() {
        return (ResultSet rs, int row) ->
                Enrollment.builder()
                        .id(rs.getInt("id"))
                        .student(rs.getInt("student"))
                        .course(rs.getInt("course"))
                        .status(rs.getString("status"))
                        .build();
    }
}
