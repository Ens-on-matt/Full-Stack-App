package com.example.sample_project.repository;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.core.io.ClassPathResource;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.example.sample_project.SampleProjectApplication;
import com.example.sample_project.model.Staff;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = {SampleProjectApplication.class})
@ActiveProfiles("test")
@SpringBootTest
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class StaffRepositoryIT {

    private final Logger LOGGER = LoggerFactory.getLogger(StaffRepositoryIT.class);
    private static Integer newStaffID;

    @Autowired
    private DataSource dataSource;
    private StaffRepository staffRepository;

    @BeforeEach
    public void setup() throws SQLException {
        LOGGER.info("setting up DB");
        //ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("sql/alloc-data.sql"));
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        staffRepository = new StaffRepository(jdbcTemplate);
    }

    @Test
    public void testListStaffMembers() {
        // Given

        // When
        List<Staff> records = staffRepository.listAllStaff();

        // Then
        assertTrue(records.size() > 0);
    }

    @Test
    public void testGetStaffById_Valid() {
        // Given
        Integer id = 1;

        // When
        Optional<Staff> staffOpt= staffRepository.getStaffById(id);
        
        // Then
        assertTrue(staffOpt.isPresent());

        Staff staff = staffOpt.get();
        
        assertThat(staff.getName()).isEqualTo("Adam Berry");
    }

    @Test
    @SneakyThrows
    public void testGetStaffById_Invalid() {
        // Given
        Integer id = -50;

        // When
        Optional<Staff> staffOpt= staffRepository.getStaffById(id);

        // Then
        assertTrue(staffOpt.isEmpty());
    }

    @Test
    public void testGetPageOfStaff_Valid() {
        // Given
        Integer pageNum = 0, pageSize = 15, offset = 0;

        // When
        List<Staff> staffList = staffRepository.getPageOfStaff(pageNum, pageSize, offset);

        // Then
        assertEquals(pageSize, staffList.size());
    }

    @Test
    public void testGetPageOfStaff_HugeSize() {
        // Given
        Integer pageNum = 0, pageSize = 50000, offset = 0;

        // When
        List<Staff> staffList = staffRepository.getPageOfStaff(pageNum, pageSize, offset);

        // Then
        assertTrue(staffList.size() < pageSize);
    }


    @Test
    public void testGetPageOfStaff_NoResults() {
        // Given
        Integer pageNum = 50000, pageSize = 15, offset = 0;

        // When
        List<Staff> staffList = staffRepository.getPageOfStaff(pageNum, pageSize, offset);

        // Then
        assertEquals(0, staffList.size());
    }


    @Test
    public void testSearchStaffMembers_ReasonableSearch() {
        // Given
        Integer pageSize = 15;
        String searchTerm = "Larry";

        // When
        List<Staff> staffList = staffRepository.searchStaffMembers(searchTerm, pageSize);

        // Then
        assertTrue(staffList.size() > 0);
    }

    @Test
    public void testSearchStaffMembers_BadSearch() {
        // Given
        Integer pageSize = 15;
        String searchTerm = "Jsafhdksfahieajfskdnfjaebfeajbfkdsnkfjwbaekjf";

        // When
        List<Staff> staffList = staffRepository.searchStaffMembers(searchTerm, pageSize);

        // Then
        assertEquals(0, staffList.size());
    }

    @Test
    public void testListAllProfessors() {
        // Given

        // When
        List<Staff> records = staffRepository.listAllProfessors();

        // Then
        assertTrue(records.size() > 0);
    }

    @Test
    public void testGetPageOfProfessors_Valid() {
        // Given
        Integer pageNum = 0, pageSize = 5, offset = 0;

        // When
        List<Staff> staffList = staffRepository.getPageOfProfessors(pageNum, pageSize, offset);

        // Then
        assertEquals(pageSize, staffList.size());
    }

    @Test
    public void testGetPageOfProfessors_HugeSize() {
        // Given
        Integer pageNum = 0, pageSize = 50000, offset = 0;

        // When
        List<Staff> staffList = staffRepository.getPageOfProfessors(pageNum, pageSize, offset);

        // Then
        assertTrue(staffList.size() < pageSize);
    }

    @Test
    public void testGetPageOfProfessors_NoResults() {
        // Given
        Integer pageNum = 50000, pageSize = 15, offset = 0;

        // When
        List<Staff> staffList = staffRepository.getPageOfProfessors(pageNum, pageSize, offset);

        // Then
        assertEquals(0, staffList.size());
    }



    @Test
    public void testSearchProfessors_ReasonableSearch() {
        // Given
        Integer pageSize = 15;
        String searchTerm = "Larry";

        // When
        List<Staff> staffList = staffRepository.searchProfessors(searchTerm, pageSize);

        // Then
        assertTrue(staffList.size() > 0);
    }

    @Test
    public void testSearchProfessors_BadSearch() {
        // Given
        Integer pageSize = 15;
        String searchTerm = "Jsafhdksfahieajfskdnfjaebfeajbfkdsnkfjwbaekjf";

        // When
        List<Staff> staffList = staffRepository.searchProfessors(searchTerm, pageSize);

        // Then
        assertEquals(0, staffList.size());
    }

    @Test
    public void testSizeOfTable() {
        // Given

        // When
        Integer staffTableSize = staffRepository.getSizeOfStaffTable();

        // Then
        assertTrue(staffTableSize > 0);
    }

    @Test
    @Order(1)
    public void testSaveNewStaffMember () {

        // Given
        Staff newStaff = Staff.builder().id(-1).name("Larry Jenkins").email("larryster@xyz.com")
                .phone_number("1111111111").salary(50000).job("Student Support").build();

        // When
        Optional<Integer> optNewId = staffRepository.saveNewStaffMember(newStaff);

        // Then
        assertTrue(optNewId.isPresent());
        newStaffID = optNewId.get();
        assertTrue(newStaffID > 0);
    }

    @Test
    @Order(2)
    public void testUpdateStaffMember() {
        assertTrue(newStaffID > 0);

        // Given
        Staff newStaff = Staff.builder().id(newStaffID).name("Harry Wilson").email("harry123@web.xyz")
                .phone_number("321321321").salary(50000).job("Admin").build();

        // When
        Optional<Staff> optNewStaff = staffRepository.updateStaffMember(newStaff);

        // Then
        assertTrue(optNewStaff.isPresent());

        //assertEquals(optNewStaff.get(), newStaff);
    }

    @Test
    @Order(3)
    public void testDeleteStaffMember() {
        assertTrue(newStaffID > 0);

        // Given
        Optional<Staff> staffToDelete = staffRepository.getStaffById(newStaffID);

        // When
        assertTrue(staffToDelete.isPresent());

        // Then
        assertTrue(staffRepository.deleteStaffMember(staffToDelete.get().getId()));
    }
}