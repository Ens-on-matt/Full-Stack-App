package com.example.sample_project.repository;

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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class StudentRepositoryIT {

    private final Logger LOGGER = LoggerFactory.getLogger(StudentRepositoryIT.class);
    private static Integer newStudentID;

    @Autowired
    private DataSource dataSource;
    private StudentRepository studentRepository;

    @BeforeEach
    public void setup() throws SQLException {
        LOGGER.info("setting up DB");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        studentRepository = new StudentRepository(jdbcTemplate);
    }

    @Test
    public void listAllStudents() {
        //Given

        LOGGER.info ("In listStudents_success");
         // When
        List<Student> records = studentRepository.listAllStudents();

        // Then
        //assertEquals(50, records.size());
        assertTrue(records.size() > 0);
    }

    @Test
    public void getStaffById_Valid() {
        //Given
        Integer id = 1;

        LOGGER.info ("In getStudentById_success id={}",id);
         // When
        Optional<Student> studentOpt= studentRepository.getStudentById(id);
        
        // Then
        //assertEquals(50, records.size());
        assertTrue(studentOpt.isPresent());

        Student student = studentOpt.get();
        
        assertThat(student.getName()).isEqualTo("Lily Stuart");
    }

    @Test
    @SneakyThrows
    public void testGetStudentById_Invalid() {
        // Given
        Integer id = -50;

        // When
        Optional<Student> studentOpt= studentRepository.getStudentById(id);

        // Then
        assertTrue(studentOpt.isEmpty());
    }

    @Test
    public void testGetPageOfStudent_Valid() {
        // Given
        Integer pageNum = 0, pageSize = 15, offset = 0;

        // When
        List<Student> studentList = studentRepository.getPageOfStudents(pageNum, pageSize, offset);

        // Then
        assertEquals(pageSize, studentList.size());
    }

    @Test
    public void testGetPageOfStudent_HugeSize() {
        // Given
        Integer pageNum = 0, pageSize = 50000, offset = 0;

        // When
        List<Student> studentList = studentRepository.getPageOfStudents(pageNum, pageSize, offset);

        // Then
        assertTrue(studentList.size() < pageSize);
    }


    @Test
    public void testGetPageOfStudent_NoResults() {
        // Given
        Integer pageNum = 50000, pageSize = 15, offset = 0;

        // When
        List<Student> studentList = studentRepository.getPageOfStudents(pageNum, pageSize, offset);

        // Then
        assertEquals(0, studentList.size());
    }


    @Test
    public void testSearchStudents_ReasonableSearch() {
        // Given
        Integer pageSize = 15;
        String searchTerm = "Larry";

        // When
        List<Student> studentList = studentRepository.searchStudents(searchTerm, pageSize);

        // Then
        assertTrue(studentList.size() > 0);
    }

    @Test
    public void testSearchStudents_BadSearch() {
        // Given
        Integer pageSize = 15;
        String searchTerm = "Jsafhdksfahieajfskdnfjaebfeajbfkdsnkfjwbaekjf";

        // When
        List<Student> studentList = studentRepository.searchStudents(searchTerm, pageSize);

        // Then
        assertEquals(0, studentList.size());
    }

    @Test
    public void testSizeOfTable() {
        // Given

        // When
        Integer studentTableSize = studentRepository.getSizeOfStudentTable();

        // Then
        assertTrue(studentTableSize > 0);
    }

    @Order(1)
    @Test
    public void testSaveNewStudent () {
        LOGGER.info("Should be 1");

        // Given
        Student newStudent = Student.builder().id(-1).name("Larry Jenkins").email("larryster@xyz.com")
                .phone_number("1111111111").degree_id(5).build();

        // When
        Optional<Integer> optNewId = studentRepository.saveNewStudent(newStudent);

        // Then
        assertTrue(optNewId.isPresent());
        newStudentID = optNewId.get();
        assertTrue(newStudentID > 0);
    }

    @Order(2)
    @Test
    public void testUpdateStudent() {
        LOGGER.info("Should be 2");
        assertTrue(newStudentID > 0);

        // Given
        Student newStudent = Student.builder().id(newStudentID).name("Harry Wilson").email("harry123@web.xyz")
                .phone_number("321321321").degree_id(8).build();

        // When
        Optional<Student> optNewStudent = studentRepository.updateStudent(newStudent);

        // Then
        assertTrue(optNewStudent.isPresent());

        assertEquals(optNewStudent.get().getId(), newStudent.getId());
        assertEquals(optNewStudent.get().getName(), newStudent.getName());
        assertEquals(optNewStudent.get().getEmail(), newStudent.getEmail());
        assertEquals(optNewStudent.get().getPhone_number(), newStudent.getPhone_number());
        assertEquals(optNewStudent.get().getDegree_id(), newStudent.getDegree_id());
    }

    @Order(3)
    @Test
    public void testDeleteStudent() {
        LOGGER.info("Should be 3");
        assertTrue(newStudentID > 0);

        // Given
        Optional<Student> studentToDelete = studentRepository.getStudentById(newStudentID);

        // When
        assertTrue(studentToDelete.isPresent());

        // Then
        assertTrue(studentRepository.deleteStudent(studentToDelete.get().getId()));
    }
}