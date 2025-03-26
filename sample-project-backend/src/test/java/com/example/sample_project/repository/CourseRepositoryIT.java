package com.example.sample_project.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.example.sample_project.SampleProjectApplication;
import com.example.sample_project.model.Course;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = {SampleProjectApplication.class})
@ActiveProfiles("test")
@SpringBootTest
public class CourseRepositoryIT {

    private final Logger LOGGER = LoggerFactory.getLogger(CourseRepositoryIT.class);

    @Autowired
    private DataSource dataSource;
    private CourseRepository courseRepository;

    @BeforeEach
    public void setup() throws SQLException {
        LOGGER.info("setting up DB");
        //ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("sql/alloc-data.sql"));
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        courseRepository = new CourseRepository(jdbcTemplate);
    }

    @Test
    public void listCourses_success() {
        //Given

        LOGGER.info ("In listCourses_success");
         // When
        List<Course> records = courseRepository.listAllCourses();

        // Then
        assertTrue(records.size() > 0);
    }

    @Test
    public void getCourseById_success() {
        //Given
        Integer id = 1;

        LOGGER.info ("In getCourseById_success id={}",id);
         // When
        Optional<Course> courseOpt= courseRepository.getCourseById(id);
        
        // Then
        //assertEquals(50, records.size());
        assertTrue(courseOpt.isPresent());

        Course course = courseOpt.get();
        
        assertThat(course.getName()).isEqualTo("Introduction to Biology");
    }

    @Test
    public void getCoursesRequiredGivenDegreeName_success() {
        //Given
        String degree = "Computer Science";

        LOGGER.info("In getCoursesRequiredGivenDegreeName_success degree={}", degree);

        // When
        List<Course> courses = courseRepository.getCoursesRequiredForDegree("Computer Science");

        courses.forEach(course -> {System.out.println ("course="+course);});

        // Then
        assertTrue(courses.size() > 0);
    }

    @Test
    public void getCoursesRequiredGivenDegreeID_success() {
        //Given
        Integer degree_id = 3;

        LOGGER.info("In getCoursesRequiredGivenDegreeID_success degree={}", degree_id);

        // When
        List<Course> courses = courseRepository.getCoursesRequiredForDegree(degree_id);

        courses.forEach(course -> {System.out.println ("course="+course);});

        // Then
        assertTrue(courses.size() > 0);
    }
}