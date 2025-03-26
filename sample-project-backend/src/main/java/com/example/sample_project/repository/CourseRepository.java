package com.example.sample_project.repository;

import com.example.sample_project.model.Student;
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

import com.example.sample_project.model.Course;

import static com.example.sample_project.repository.GenericRowMappers.getIDRowMapper;
import static com.example.sample_project.repository.GenericRowMappers.getTableCountRowMapper;

@Slf4j
@Repository
public class CourseRepository {
    private final JdbcOperations jdbcOperations;

    @Autowired public CourseRepository(final JdbcOperations jdbcOperations) {
        log.info ("In Course Repository");
        this.jdbcOperations = jdbcOperations;
    }

    public List<Course> listAllCourses() {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT 
                      id,
                      professor_id,
                      name,
                      degree_id
                FROM main.course
                WHERE 1=1
                """;

        log.debug("Query {}", sql);

        return getCourses(sql, parameters, getCourseRowMapper());
    }

    public Optional<Course> getCourseById(@NonNull Integer id) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT 
                      id,
                      professor_id,
                      name,
                      degree_id
                FROM main.course
                WHERE id=:course_id
                """;

        log.debug("Query {}", sql);

        parameters.addValue("course_id", id);

        return Optional.of(getCourses(sql, parameters, getCourseRowMapper()).get(0));
    }

    public List<Course> getPageOfCourse(@NonNull Integer PageNo, @NonNull Integer PageSize, @NonNull Integer Offset) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
            SELECT 
                  id,
                  professor_id,
                  name,
                  degree_id
            FROM main.course
            ORDER BY name ASC 
            OFFSET :EntryOffset ROWS
            FETCH FIRST :PageSize ROWS ONLY
                    """;

        log.debug("Query {}", sql);
        parameters.addValue("EntryOffset", PageNo*PageSize + Offset);
        parameters.addValue("PageSize", PageSize);

        return getCourses(sql, parameters, getCourseRowMapper());
    }

    public List<Course> searchCourses (@NonNull String searchTerm, @NonNull Integer PageSize) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """                
            SELECT 
                  id,
                  professor_id,
                  name,
                  degree_id
            FROM main.course
            WHERE similarity(name, :searchTerm) > 0.05
            ORDER BY similarity(name, :searchTerm) DESC
            FETCH FIRST :PageSize ROWS ONLY
                """;

        parameters.addValue("searchTerm", searchTerm);
        parameters.addValue("PageSize", PageSize);
        return getCourses(sql, parameters, getCourseRowMapper());
    }

    public Optional<Integer> saveNewCourse(@NonNull Course course) {
        log.info("Repository saveNewCourse called");

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                INSERT INTO main.course (
                                        name,
                                        professor_id,
                                        degree_id) 
                VALUES (
                        :name,
                        :professor_id,
                        :degree_id
                        ) 
                RETURNING id
                """;

        log.debug("Query {}", sql);

        parameters.addValue("name", course.getName());
        parameters.addValue("professor_id", course.getProfessor_id());
        parameters.addValue("degree_id", course.getDegree_id());

        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        return Optional.of(namedJdbcTemplateObject.query(sql, parameters, GenericRowMappers.getIDRowMapper()).get(0));
    }

    public Optional<Course> updateCourseMember(Course course) {
        log.info("Repository updateCourseMember called");

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                UPDATE main.course
                SET name=:name,
                    professor_id=:professor_id,
                    degree_id=:degree_id
                WHERE id=:id
                RETURNING *
                """;

        log.debug("Query {}", sql);

        parameters.addValue("id", course.getId());
        parameters.addValue("name", course.getName());
        parameters.addValue("professor_id", course.getProfessor_id());
        parameters.addValue("degree_id", course.getDegree_id());

        return Optional.of(getCourses(sql, parameters, getCourseRowMapper()).get(0));
    }

    public Integer getSizeOfCourseTable() {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT COUNT(*) FROM main.course
                """;

        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        return namedJdbcTemplateObject.query(sql, parameters, getTableCountRowMapper()).get(0);
    }

    public Boolean deleteCourse(Integer id) {
        log.info("Repository deleteCourse called");
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                DELETE FROM main.course
                WHERE id = :id
                RETURNING *
                """;

        parameters.addValue("id", id);

        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        try {
            namedJdbcTemplateObject.query(sql, parameters, getCourseRowMapper());
            return true;
        } catch (Exception e) {
            log.info("Error when deleting course");
            return false;
        }
    }


    public List<Course> getCoursesRequiredForDegree(@NonNull String degree) {
        
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                    SELECT id,
                        professor_id,
                        name,
                        degree_id
                    FROM main.course
                    WHERE degree_id in (
                        SELECT id
                        FROM main.degree
                        WHERE name=(:degree_name)
                    );
                """;
                String sql2 = """
                    SELECT c.id,
                           c.professor_id,
                           c.name,
                           c.degree_id
                    FROM main.course c
                    INNER JOIN main.degree d
                    ON c.degree_id = d.id
                    WHERE d.name = (:degree_name);
                    """;

        log.debug("Query {}", sql);

        parameters.addValue("degree_name", degree);

        return getCourses(sql, parameters, getCourseRowMapper());            
    }

    public List<Course> getCoursesRequiredForDegree(@NonNull Integer degree) {
        
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT id,
                      professor_id,
                      name,
                      degree_id
                FROM main.course
                WHERE degree_id=:degree_id;
                """;

        log.debug("Query {}", sql);

        parameters.addValue("degree_id", degree);

        return getCourses(sql, parameters, getCourseRowMapper());            
    }

    private List<Course> getCourses(String sql, MapSqlParameterSource parameters, RowMapper<Course> rowMapper) {
        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        return namedJdbcTemplateObject.query(
                sql,
                parameters,
                rowMapper
        );
    }

    private RowMapper<Course> getCourseRowMapper() {
        return (ResultSet rs, int row) ->
                Course.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .degree_id(rs.getInt("degree_id"))
                        .professor_id(rs.getInt("professor_id"))
                        .build();
    }
}
