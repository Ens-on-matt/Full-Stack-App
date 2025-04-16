package com.example.sample_project.repository;

import com.example.sample_project.SampleProjectApplication;
import com.example.sample_project.model.Enrollment;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = {SampleProjectApplication.class})
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class EnrollmentRepositoryIT {

    private final Logger LOGGER = LoggerFactory.getLogger(EnrollmentRepositoryIT.class);
    private static Integer newEnrollmentStudentID = -1;
    private static Integer newEnrollmentCourseID = -1;

    @Autowired
    private DataSource dataSource;
    private EnrollmentRepository enrollmentRepository;

    @BeforeEach
    public void setup() throws SQLException {
        LOGGER.info("setting up DB");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        enrollmentRepository = new EnrollmentRepository(jdbcTemplate);
    }

    private List<Enrollment> createExampleEnrollmentList() {
        Integer[] exampleStudentIds = {3,7,8};
        String[] exampleStatus = {"Completed", "In progress", "Completed"};
        List<Enrollment> enrollmentList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            enrollmentList.add(Enrollment.builder()
                    .student(exampleStudentIds[i])
                    .course(i + 1)
                    .status(exampleStatus[i])
                    .build());
        }
        return enrollmentList;
    }


    @Test
    @Order(1)
    public void testSaveNewEnrollment () {
        LOGGER.info("Should be 1");

        // Given
        Enrollment newEnrollment = Enrollment.builder().student(100).course(50).status("In progress").build();
        List<Enrollment> enrollments = createExampleEnrollmentList();

        // When
        Optional<Enrollment> optNewEnrollment = enrollmentRepository.saveNewEnrollment(newEnrollment);

        // Then
        assertTrue(optNewEnrollment.isPresent());
        newEnrollmentStudentID = optNewEnrollment.get().getStudent();
        newEnrollmentCourseID = optNewEnrollment.get().getCourse();
        assertTrue(newEnrollmentStudentID > 0);
        assertTrue(newEnrollmentCourseID > 0);

        for (Enrollment e : enrollments) {
            enrollmentRepository.saveNewEnrollment(e);
        }
    }

    @Test
    @Order(2)
    public void testUpdateEnrollmentStatus() {
        LOGGER.info("Should be 2");
        assertTrue(newEnrollmentStudentID > 0);
        assertTrue(newEnrollmentCourseID > 0);

        // Given
        Enrollment newEnrollment = Enrollment.builder().student(newEnrollmentStudentID).course(newEnrollmentCourseID)
                .status("Completed").build();

        // When
        Optional<Enrollment> optNewEnrollment = enrollmentRepository.updateEnrollmentStatus(newEnrollment);

        // Then
        assertTrue(optNewEnrollment.isPresent());

        assertEquals(optNewEnrollment.get().getStudent(), newEnrollment.getStudent());
        assertEquals(optNewEnrollment.get().getCourse(), newEnrollment.getCourse());
        assertEquals(optNewEnrollment.get().getStatus(), newEnrollment.getStatus());
    }

    @Test
    @Order(3)
    public void testDeleteEnrollment() {
        LOGGER.info("Should be 3");
        assertTrue(newEnrollmentStudentID > 0);
        assertTrue(newEnrollmentCourseID > 0);

        // Given
        Optional<Enrollment> enrollmentToDelete = enrollmentRepository.getEnrollmentById(newEnrollmentStudentID, newEnrollmentCourseID);

        // When
        assertTrue(enrollmentToDelete.isPresent());

        // Then
        assertTrue(enrollmentRepository.deleteEnrollment(enrollmentToDelete.get().getStudent(), enrollmentToDelete.get().getCourse()));
    }

    @Test
    @Order(11)
    public void cleanupTestEnrollments() {
        for (Enrollment e : createExampleEnrollmentList()) {
            assertTrue(enrollmentRepository.deleteEnrollment(e.getStudent(), e.getCourse()));
        }
    }


    @Test
    @Order(4)
    public void listAllEnrollments() {
        //Given

        LOGGER.info ("In listEnrollments");
         // When
        List<Enrollment> records = enrollmentRepository.listAllEnrollments();

        // Then
        assertTrue(records.size() > 0);
    }

    @Test
    @Order(5)
    public void getEnrollmentById_Valid() {
        //Given
        Integer student_id = 3, course_id = 1;

        LOGGER.info ("In getEnrollmentById_Valid student={} course={}", student_id, course_id);
         // When
        Optional<Enrollment> enrollmentOpt= enrollmentRepository.getEnrollmentById(student_id, course_id);
        
        // Then
        //assertEquals(50, records.size());
        assertTrue(enrollmentOpt.isPresent());

        Enrollment enrollment = enrollmentOpt.get();
        
        assertThat(enrollment.getStatus()).isEqualTo("Completed");
    }

    @Test
    @SneakyThrows
    @Order(6)
    public void testGetEnrollmentById_Invalid() {
        // Given
        Integer student_id = -50, course_id = 10;

        // When
        Optional<Enrollment> enrollmentOpt= enrollmentRepository.getEnrollmentById(student_id, course_id);

        // Then
        assertTrue(enrollmentOpt.isEmpty());
    }

    @Test
    @Order(7)
    public void testSizeOfTable() {
        // Given

        // When
        Integer enrollmentTableSize = enrollmentRepository.getSizeOfEnrollmentTable();

        // Then
        assertTrue(enrollmentTableSize > 0);
    }
}