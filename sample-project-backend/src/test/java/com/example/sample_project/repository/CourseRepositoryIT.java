package com.example.sample_project.repository;

import com.example.sample_project.model.Student;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class CourseRepositoryIT {

    private final Logger LOGGER = LoggerFactory.getLogger(CourseRepositoryIT.class);
    private static Integer newCourseID = -1;

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
    public void listCourses() {
        //Given

        LOGGER.info ("In listCourses_success");
         // When
        List<Course> records = courseRepository.listAllCourses();

        // Then
        assertTrue(records.size() > 0);
    }

    @Test
    public void getCourseById_Valid() {
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
    @SneakyThrows
    public void testGetCourseById_Invalid() {
        // Given
        Integer id = -50;

        // When
        Optional<Course> courseOpt= courseRepository.getCourseById(id);

        // Then
        assertTrue(courseOpt.isEmpty());
    }

    @Test
    public void testGetPageOfCourses_Valid() {
        // Given
        Integer pageNum = 0, pageSize = 15, offset = 0;

        // When
        List<Course> courseList = courseRepository.getPageOfCourses(pageNum, pageSize, offset);

        // Then
        assertEquals(pageSize, courseList.size());
    }

    @Test
    public void testGetPageOfCourses_HugeSize() {
        // Given
        Integer pageNum = 0, pageSize = 50000, offset = 0;

        // When
        List<Course> courseList = courseRepository.getPageOfCourses(pageNum, pageSize, offset);

        // Then
        assertTrue(courseList.size() < pageSize);
    }


    @Test
    public void testGetPageOfCourses_NoResults() {
        // Given
        Integer pageNum = 50000, pageSize = 15, offset = 0;

        // When
        List<Course> courseList = courseRepository.getPageOfCourses(pageNum, pageSize, offset);

        // Then
        assertEquals(0, courseList.size());
    }


    @Test
    public void testSearchCourses_ReasonableSearch() {
        // Given
        Integer pageSize = 15;
        String searchTerm = "Larry";

        // When
        List<Course> courseList = courseRepository.searchCourses(searchTerm, pageSize);

        // Then
        assertTrue(courseList.size() > 0);
    }

    @Test
    public void testSearchCourses_BadSearch() {
        // Given
        Integer pageSize = 15;
        String searchTerm = "Jsafhdksfahieajfskdnfjaebfeajbfkdsnkfjwbaekjf";

        // When
        List<Course> courseList = courseRepository.searchCourses(searchTerm, pageSize);

        // Then
        assertEquals(0, courseList.size());
    }

    @Test
    public void testSizeOfTable() {
        // Given

        // When
        Integer courseTableSize = courseRepository.getSizeOfCourseTable();

        // Then
        assertTrue(courseTableSize > 0);
    }

    @Test
    @Order(1)
    public void testSaveNewCourse () {
        LOGGER.info("Should be 1");

        // Given
        Course newCourse = Course.builder().id(-1).name("Music").professor_id(29).degree_id(3).build();

        // When
        Optional<Integer> optNewId = courseRepository.saveNewCourse(newCourse);

        // Then
        assertTrue(optNewId.isPresent());
        newCourseID = optNewId.get();
        assertTrue(newCourseID > 0);
    }

    @Test
    @Order(2)
    public void testUpdateCourse() {
        LOGGER.info("Should be 2");
        assertTrue(newCourseID > 0);

        // Given
        Course newCourse = Course.builder().id(newCourseID).name("Business").professor_id(1).degree_id(2).build();

        // When
        Optional<Course> optNewCourse = courseRepository.updateCourse(newCourse);

        // Then
        assertTrue(optNewCourse.isPresent());

        assertEquals(optNewCourse.get().getId(), newCourse.getId());
        assertEquals(optNewCourse.get().getName(), newCourse.getName());
        assertEquals(optNewCourse.get().getProfessor_id(), newCourse.getProfessor_id());
        assertEquals(optNewCourse.get().getDegree_id(), newCourse.getDegree_id());
    }

    @Test
    @Order(3)
    public void testDeleteCourse() {
        LOGGER.info("Should be 3");
        assertTrue(newCourseID > 0);

        // Given
        Optional<Course> courseToDelete = courseRepository.getCourseById(newCourseID);

        // When
        assertTrue(courseToDelete.isPresent());

        // Then
        assertTrue(courseRepository.deleteCourse(courseToDelete.get().getId()));
    }

    @Test
    public void testGetCoursesRequiredForDegreeName() {
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
    public void testGetCoursesRequiredForDegreeID() {
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