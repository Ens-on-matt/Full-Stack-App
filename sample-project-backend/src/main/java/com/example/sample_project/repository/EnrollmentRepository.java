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

    public Optional<Enrollment> getEnrollmentById(@NonNull Integer student, @NonNull Integer course) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT 
                      student,
                      course,
                      status
                FROM main.enrollment
                WHERE student=:student_id AND course=:course_id
                """;

        log.debug("Query {}", sql);

        parameters.addValue("student_id", student);
        parameters.addValue("course_id", course);

        List<Enrollment> enrollments = getEnrollments(sql, parameters, getEnrollmentRowMapper());
        if (enrollments.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(enrollments.get(0));
        }
    }

    public List<Enrollment> listAllEnrollmentsForStudent(@NonNull Integer student_id) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT 
                      student,
                      course,
                      status
                FROM main.enrollment
                WHERE student=:student_id
                """;

        parameters.addValue("student_id", student_id);

        return getEnrollments(sql, parameters, getEnrollmentRowMapper());
    }

    public List<Enrollment> listAllEnrollmentsInCourse(@NonNull Integer course_id) {
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

        parameters.addValue("course_id", course_id);

        return getEnrollments(sql, parameters, getEnrollmentRowMapper());
    }

    public List<Enrollment> getPageOfEnrollment(@NonNull Integer PageNo, @NonNull Integer PageSize, @NonNull Integer Offset) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
            SELECT 
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

    public Optional<Enrollment> saveNewEnrollment(@NonNull Enrollment enrollment) {
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
                RETURNING *
                """;

        log.debug("Query {}", sql);

        parameters.addValue("student", enrollment.getStudent());
        parameters.addValue("course", enrollment.getCourse());
        parameters.addValue("status", enrollment.getStatus());

        return Optional.of(getEnrollments(sql, parameters, getEnrollmentRowMapper()).get(0));
    }

    public Optional<Enrollment> updateEnrollmentStatus(Enrollment enrollment) {
        log.info("Repository updateEnrollmentMember called");

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                UPDATE main.enrollment
                SET status=:status
                WHERE student=:student_id AND course=:course_id
                RETURNING *
                """;

        log.debug("Query {}", sql);

        parameters.addValue("student_id", enrollment.getStudent());
        parameters.addValue("course_id", enrollment.getCourse());
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

    public Boolean deleteEnrollment(@NonNull Integer student, @NonNull Integer course) {
        log.info("Repository deleteEnrollment called");
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                DELETE FROM main.enrollment
                WHERE student=:student_id AND course=:course_id
                RETURNING *
                """;

        parameters.addValue("student_id", student);
        parameters.addValue("course_id", course);

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
                        .student(rs.getInt("student"))
                        .course(rs.getInt("course"))
                        .status(rs.getString("status"))
                        .build();
    }
}
