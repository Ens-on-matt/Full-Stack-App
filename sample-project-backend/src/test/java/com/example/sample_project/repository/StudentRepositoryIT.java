package com.example.sample_project.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.example.sample_project.SampleProjectApplication;
import com.example.sample_project.model.Student;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = {SampleProjectApplication.class})
@ActiveProfiles("test")
@SpringBootTest
public class StudentRepositoryIT {

    private final Logger LOGGER = LoggerFactory.getLogger(StudentRepositoryIT.class);

    @Autowired
    private DataSource dataSource;
    private StudentRepository studentRepository;

    @BeforeEach
    public void setup() throws SQLException {
        LOGGER.info("setting up DB");
        //ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("sql/alloc-data.sql"));
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        studentRepository = new StudentRepository(jdbcTemplate);
    }

    @Test
    public void listStudents_success() {
        //Given

        LOGGER.info ("In listStudents_success");
         // When
        List<Student> records = studentRepository.listAllStudents();

        // Then
        //assertEquals(50, records.size());
        assertTrue(records.size() > 0);
    }

    @Test
    public void getStaffById_success() {
        //Given
        Integer id = 1;

        LOGGER.info ("In getStudentById_success id={}",id);
         // When
        Optional<Student> studentOpt= studentRepository.getStudentById(id);
        
        // Then
        //assertEquals(50, records.size());
        assertTrue(studentOpt.isPresent());

        Student student = studentOpt.get();
        
        assertThat(student.getName()).isEqualTo("Mason Stanton");
    }
}