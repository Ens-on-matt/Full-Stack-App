package com.example.sample_project.repository;

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

import com.example.sample_project.model.Degree;

import static com.example.sample_project.repository.GenericRowMappers.getIDRowMapper;
import static com.example.sample_project.repository.GenericRowMappers.getTableCountRowMapper;

@Slf4j
@Repository
public class DegreeRepository {
    private final JdbcOperations jdbcOperations;

    @Autowired public DegreeRepository(final JdbcOperations jdbcOperations) {
        log.info ("In Degree Repository");
        this.jdbcOperations = jdbcOperations;
    }

    public List<Degree> listAllDegrees() {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT 
                      id,
                      name
                FROM main.degree
                WHERE 1=1
                ORDER BY id ASC;
                """;

        log.debug("Query {}", sql);

        return getDegrees(sql, parameters, getDegreeRowMapper());
    }

    public Optional<Degree> getDegreeById(@NonNull Integer id) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT 
                      id,
                      name
                FROM main.degree
                WHERE id=:degree_id
                """;

        log.debug("Query {}", sql);

        parameters.addValue("degree_id", id);

        List<Degree> degree = getDegrees(sql, parameters, getDegreeRowMapper());
        if (degree.isEmpty()) { return Optional.empty(); }

        return Optional.of(degree.get(0));
    }

    public List<Degree> getPageOfDegrees(@NonNull Integer PageNo, @NonNull Integer PageSize, @NonNull Integer Offset) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
            SELECT 
                  id,
                  name
            FROM main.degree
            ORDER BY name ASC 
            OFFSET :EntryOffset ROWS
            FETCH FIRST :PageSize ROWS ONLY
                    """;

        log.debug("Query {}", sql);
        parameters.addValue("EntryOffset", PageNo*PageSize + Offset);
        parameters.addValue("PageSize", PageSize);

        return getDegrees(sql, parameters, getDegreeRowMapper());
    }

    public List<Degree> searchDegrees (@NonNull String searchTerm, @NonNull Integer PageSize) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """                
            SELECT 
                  id,
                  name
            FROM main.degree
            WHERE similarity(name, :searchTerm) > 0.05
            ORDER BY similarity(name, :searchTerm) DESC
            FETCH FIRST :PageSize ROWS ONLY
                """;

        parameters.addValue("searchTerm", searchTerm);
        parameters.addValue("PageSize", PageSize);
        return getDegrees(sql, parameters, getDegreeRowMapper());
    }

    public Integer getSizeOfDegreeTable() {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT COUNT(*) FROM main.degree
                """;

        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        return namedJdbcTemplateObject.query(sql, parameters, getTableCountRowMapper()).get(0);
    }

    public Optional<Integer> saveNewDegree(@NonNull Degree degree) {
        log.info("Repository saveNewDegree called");

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                INSERT INTO main.degree (
                                        name) 
                VALUES (
                        :name
                        ) 
                RETURNING id
                """;

        log.debug("Query {}", sql);

        parameters.addValue("name", degree.getName());

        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        return Optional.of(namedJdbcTemplateObject.query(sql, parameters, getIDRowMapper()).get(0));
    }

    public Optional<Degree> updateDegree(Degree degree) {
        log.info("Repository updateDegree called");

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                UPDATE main.degree
                SET name=:name
                WHERE id=:id
                RETURNING *
                """;

        log.debug("Query {}", sql);

        parameters.addValue("id", degree.getId());
        parameters.addValue("name", degree.getName());

        return Optional.of(getDegrees(sql, parameters, getDegreeRowMapper()).get(0));
    }

    public Boolean deleteDegree(Integer id) {
        log.info("Repository deleteDegree called");
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                DELETE FROM main.degree
                WHERE id = :id
                RETURNING *
                """;

        parameters.addValue("id", id);

        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        try {
            namedJdbcTemplateObject.query(sql, parameters, getDegreeRowMapper());
            return true;
        } catch (Exception e) {
            log.info("Error when deleting degree");
            return false;
        }
    }


    private List<Degree> getDegrees(String sql, MapSqlParameterSource parameters, RowMapper<Degree> rowMapper) {
        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        return namedJdbcTemplateObject.query(
                sql,
                parameters,
                rowMapper
        );
    }

    private RowMapper<Degree> getDegreeRowMapper() {
        return (ResultSet rs, int row) ->
                Degree.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .build();
    }
}
