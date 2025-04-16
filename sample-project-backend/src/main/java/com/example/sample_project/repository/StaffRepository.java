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

import com.example.sample_project.model.Staff;

import static com.example.sample_project.repository.GenericRowMappers.getTableCountRowMapper;

@Slf4j
@Repository
public class StaffRepository {
    private final JdbcOperations jdbcOperations;

    @Autowired public StaffRepository(final JdbcOperations jdbcOperations) {
        log.info ("In Staff Repository");
        this.jdbcOperations = jdbcOperations;
    }

    public List<Staff> listAllStaff() {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT 
                      id,
                      name,
                      email,
                      phone_number,
                      salary,
                      job 
                FROM main.staff
                WHERE 1=1
                """;

        log.debug("Query {}", sql);

        return getStaffMembers(sql, parameters, getStaffRowMapper());
    }

    public Optional<Staff> getStaffById(@NonNull Integer id) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT 
                      id,
                      name,
                      email,
                      phone_number,
                      salary,
                      job 
                FROM main.staff
                WHERE id=:staff_id
                """;

        log.debug("Query {}", sql);

        parameters.addValue("staff_id", id);

        List<Staff> staff = getStaffMembers(sql, parameters, getStaffRowMapper());
        if (staff.isEmpty()) { return Optional.empty(); }

        return Optional.of(staff.get(0));
    }

    public List<Staff> getPageOfStaff(@NonNull Integer PageNo, @NonNull Integer PageSize, @NonNull Integer Offset) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
            SELECT 
                  id,
                  name,
                  email,
                  phone_number,
                  salary,
                  job
            FROM main.staff
            ORDER BY name ASC 
            OFFSET :EntryOffset ROWS
            FETCH FIRST :PageSize ROWS ONLY
                    """;
            
            log.debug("Query {}", sql);
            parameters.addValue("EntryOffset", PageNo*PageSize + Offset);
            parameters.addValue("PageSize", PageSize);

            return getStaffMembers(sql, parameters, getStaffRowMapper());
    }

    public List<Staff> searchStaffMembers (@NonNull String searchTerm, @NonNull Integer PageSize) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """                
            SELECT
                  id,
                  name,
                  email,
                  phone_number,
                  salary,
                  job
            FROM main.staff
            WHERE similarity(name, :searchTerm) > 0.05
            ORDER BY similarity(name, :searchTerm) DESC
            FETCH FIRST :PageSize ROWS ONLY
                """;

        parameters.addValue("searchTerm", searchTerm);
        parameters.addValue("PageSize", PageSize);
        return getStaffMembers(sql, parameters, getStaffRowMapper());
    }

    public List<Staff> listAllProfessors() {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
            SELECT 
                  id,
                  name,
                  email,
                  phone_number,
                  salary,
                  job
            FROM main.staff
            WHERE job='Professor'
            ORDER BY name ASC 
                    """;

        return getStaffMembers(sql, parameters, getStaffRowMapper());
    }

    public List<Staff> getPageOfProfessors(@NonNull Integer PageNo, @NonNull Integer PageSize, @NonNull Integer Offset) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
            SELECT 
                  id,
                  name,
                  email,
                  phone_number,
                  salary,
                  job
            FROM main.staff
            WHERE job='Professor'
            ORDER BY name ASC 
            OFFSET :EntryOffset ROWS
            FETCH FIRST :PageSize ROWS ONLY
                    """;

        parameters.addValue("EntryOffset", PageNo*PageSize + Offset);
        parameters.addValue("PageSize", PageSize);

        return getStaffMembers(sql, parameters, getStaffRowMapper());
    }


    public List<Staff> searchProfessors (@NonNull String searchTerm, @NonNull Integer PageSize) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """                
            SELECT
                  id,
                  name,
                  email,
                  phone_number,
                  salary,
                  job
            FROM main.staff
            WHERE job='Professor' AND similarity(name, :searchTerm) > 0.05
            ORDER BY similarity(name, :searchTerm) DESC
            FETCH FIRST :PageSize ROWS ONLY
                """;

        parameters.addValue("searchTerm", searchTerm);
        parameters.addValue("PageSize", PageSize);
        return getStaffMembers(sql, parameters, getStaffRowMapper());
    }

    public Integer getSizeOfStaffTable() {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                SELECT COUNT(*) FROM main.staff
                """;

        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        return namedJdbcTemplateObject.query(sql, parameters, getTableCountRowMapper()).get(0);
    }

    public Optional<Integer> saveNewStaffMember(@NonNull Staff staff) {
        log.info("Repository saveNewStaffMember called");

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                INSERT INTO main.staff (
                                        name, 
                                        email,
                                        phone_number, 
                                        salary, 
                                        job) 
                VALUES (
                        :name,
                        :email,
                        :phone_number,
                        :salary,
                        :job
                        ) 
                RETURNING id
                """;

        log.debug("Query {}", sql);

        parameters.addValue("name", staff.getName());
        parameters.addValue("email", staff.getEmail());
        parameters.addValue("phone_number", staff.getPhone_number());
        parameters.addValue("salary", staff.getSalary());
        parameters.addValue("job", staff.getJob());

        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        return Optional.of(namedJdbcTemplateObject.query(sql, parameters, GenericRowMappers.getIDRowMapper()).get(0));
    }

    public Optional<Staff> updateStaffMember(Staff staff) {
        log.info("Repository updateStaffMember called");

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                UPDATE main.staff
                SET name=:name,
                    email=:email,
                    phone_number=:phone_number,
                    salary=:salary,
                    job=:job
                WHERE id=:id
                RETURNING *
                """;

        log.debug("Query {}", sql);

        parameters.addValue("id", staff.getId());
        parameters.addValue("name", staff.getName());
        parameters.addValue("email", staff.getEmail());
        parameters.addValue("phone_number", staff.getPhone_number());
        parameters.addValue("salary", staff.getSalary());
        parameters.addValue("job", staff.getJob());

        return Optional.of(getStaffMembers(sql, parameters, getStaffRowMapper()).get(0));
    }

    public Boolean deleteStaffMember(Integer id) {
        log.info("Repository deleteStaffMember called");
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String sql = """
                DELETE FROM main.staff
                WHERE id = :id
                RETURNING *
                """;

        parameters.addValue("id", id);

        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        try {
            namedJdbcTemplateObject.query(sql, parameters, getStaffRowMapper());
            return true;
        } catch (Exception e) {
            log.info("Error when deleting staff member");
            return false;
        }
    }

    private List<Staff> getStaffMembers(String sql, MapSqlParameterSource parameters, RowMapper<Staff> rowMapper) {
        NamedParameterJdbcTemplate namedJdbcTemplateObject = new NamedParameterJdbcTemplate(jdbcOperations);
        return namedJdbcTemplateObject.query(
                sql,
                parameters,
                rowMapper
        );
    }


    private RowMapper<Staff> getStaffRowMapper() {
        return (ResultSet rs, int row) ->
                Staff.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .email(rs.getString("email"))
                        .phone_number(rs.getString("phone_number"))
                        .salary(rs.getInt("salary"))
                        .job(rs.getString("job"))
                        .build();
    }
}
