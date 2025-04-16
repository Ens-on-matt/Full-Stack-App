package com.example.sample_project.repository;

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
import com.example.sample_project.model.Degree;

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
public class DegreeRepositoryIT {

    private final Logger LOGGER = LoggerFactory.getLogger(DegreeRepositoryIT.class);
    private static Integer newDegreeID = -1;

    @Autowired
    private DataSource dataSource;
    private DegreeRepository degreeRepository;

    @BeforeEach
    public void setup() throws SQLException {
        LOGGER.info("setting up DB");
        //ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("sql/alloc-data.sql"));
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        degreeRepository = new DegreeRepository(jdbcTemplate);
    }

    @Test
    public void listAllDegrees() {
        //Given

        LOGGER.info ("In listDegrees_success");
         // When
        List<Degree> records = degreeRepository.listAllDegrees();

        // Then
        assertTrue(records.size() > 0);
    }

    @Test
    public void getDegreeById_Valid() {
        //Given
        Integer id = 1;

        LOGGER.info ("In getDegreeById_success id={}",id);
         // When
        Optional<Degree> degreeOpt= degreeRepository.getDegreeById(id);
        
        // Then
        //assertEquals(50, records.size());
        assertTrue(degreeOpt.isPresent());

        Degree degree = degreeOpt.get();
        
        assertThat(degree.getName()).isEqualTo("Science");
    }

    @Test
    @SneakyThrows
    public void testGetDegreeById_Invalid() {
        // Given
        Integer id = -50;

        // When
        Optional<Degree> degreeOpt= degreeRepository.getDegreeById(id);

        // Then
        assertTrue(degreeOpt.isEmpty());
    }

    @Test
    public void testGetPageOfDegrees_Valid() {
        // Given
        Integer pageNum = 0, pageSize = 8, offset = 0;

        // When
        List<Degree> degreeList = degreeRepository.getPageOfDegrees(pageNum, pageSize, offset);

        // Then
        assertEquals(pageSize, degreeList.size());
    }

    @Test
    public void testGetPageOfDegrees_HugeSize() {
        // Given
        Integer pageNum = 0, pageSize = 50000, offset = 0;

        // When
        List<Degree> degreeList = degreeRepository.getPageOfDegrees(pageNum, pageSize, offset);

        // Then
        assertTrue(degreeList.size() < pageSize);
    }


    @Test
    public void testGetPageOfDegrees_NoResults() {
        // Given
        Integer pageNum = 50000, pageSize = 15, offset = 0;

        // When
        List<Degree> degreeList = degreeRepository.getPageOfDegrees(pageNum, pageSize, offset);

        // Then
        assertEquals(0, degreeList.size());
    }


    @Test
    public void testSearchDegrees_ReasonableSearch() {
        // Given
        Integer pageSize = 15;
        String searchTerm = "Larry";

        // When
        List<Degree> degreeList = degreeRepository.searchDegrees(searchTerm, pageSize);

        // Then
        assertTrue(degreeList.size() > 0);
    }

    @Test
    public void testSearchDegrees_BadSearch() {
        // Given
        Integer pageSize = 15;
        String searchTerm = "Jsafhdksfahieajfskdnfjaebfeajbfkdsnkfjwbaekjf";

        // When
        List<Degree> degreeList = degreeRepository.searchDegrees(searchTerm, pageSize);

        // Then
        assertEquals(0, degreeList.size());
    }

    @Test
    public void testSizeOfTable() {
        // Given

        // When
        Integer degreeTableSize = degreeRepository.getSizeOfDegreeTable();

        // Then
        assertTrue(degreeTableSize > 0);
    }

    @Test
    @Order(1)
    public void testSaveNewDegree () {
        LOGGER.info("Should be 1");

        // Given
        Degree newDegree = Degree.builder().id(-1).name("Formal Music Theory").build();

        // When
        Optional<Integer> optNewId = degreeRepository.saveNewDegree(newDegree);

        // Then
        assertTrue(optNewId.isPresent());
        newDegreeID = optNewId.get();
        assertTrue(newDegreeID > 0);
    }

    @Test
    @Order(2)
    public void testUpdateDegree() {
        LOGGER.info("Should be 2");
        assertTrue(newDegreeID > 0);

        // Given
        Degree newDegree = Degree.builder().id(newDegreeID).name("Creative Writing").build();

        // When
        Optional<Degree> optNewDegree = degreeRepository.updateDegree(newDegree);

        // Then
        assertTrue(optNewDegree.isPresent());

        assertEquals(optNewDegree.get().getId(), newDegree.getId());
        assertEquals(optNewDegree.get().getName(), newDegree.getName());
    }

    @Test
    @Order(3)
    public void testDeleteDegree() {
        LOGGER.info("Should be 3");
        assertTrue(newDegreeID > 0);

        // Given
        Optional<Degree> degreeToDelete = degreeRepository.getDegreeById(newDegreeID);

        // When
        assertTrue(degreeToDelete.isPresent());

        // Then
        assertTrue(degreeRepository.deleteDegree(degreeToDelete.get().getId()));
    }
}