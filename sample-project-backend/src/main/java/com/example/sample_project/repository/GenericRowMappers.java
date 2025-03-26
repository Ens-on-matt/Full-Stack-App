package com.example.sample_project.repository;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;

public class GenericRowMappers {
    static RowMapper<Integer> getIDRowMapper() {
        return (ResultSet rs, int row) ->
                rs.getInt("id");
    }

    static RowMapper<Integer> getTableCountRowMapper() {
        return (ResultSet rs, int row) ->
                rs.getInt("count");
    }
}
