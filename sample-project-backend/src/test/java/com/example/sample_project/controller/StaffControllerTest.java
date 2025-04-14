package com.example.sample_project.controller;

import com.example.sample_project.matchers.StaffMatcher;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.sample_project.SampleProjectApplication;
import com.example.sample_project.model.Staff;
import com.example.sample_project.repository.StaffRepository;

import io.micrometer.common.lang.NonNull;
import lombok.SneakyThrows;

@SpringBootTest(classes = SampleProjectApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    StaffRepository staffRepository;

    @Test
    @SneakyThrows
    void testListAllStaff() {

        // Given
        List<Staff> testStaffList = createExampleStaffList();

        String staffListPayload = """
                [
                    {
                        "id": 1,
                        "name": "Adam Berry",
                        "email": "adam.b@xyz.com",
                        "phone_number": "04854333619",
                        "salary": 85102,
                        "job": "Professor"
                    },
                    {
                        "id": 2,
                        "name": "Abel Cohen",
                        "email": "abel.c@xyz.com",
                        "phone_number": "04668685844",
                        "salary": 98823,
                        "job": "Student Support"
                    },
                    {
                        "id": 3,
                        "name": "Lila Hubbard",
                        "email": "lila.h@xyz.com",
                        "phone_number": "04718216314",
                        "salary": 101622,
                        "job": "Admin"
                    },
                    {
                        "id": 4,
                        "name": "Wesley Gentry",
                        "email": "wesley.g@xyz.com",
                        "phone_number": "04021648061",
                        "salary": 90055,
                        "job": "Hospitality"
                    },
                    {
                        "id": 5,
                        "name": "Bennett Hood",
                        "email": "bennett.h@xyz.com",
                        "phone_number": "04471504946",
                        "salary": 61583,
                        "job": "Professor"
                    },
                    {
                        "id": 6,
                        "name": "Christopher Corona",
                        "email": "christopher.c@xyz.com",
                        "phone_number": "04268319791",
                        "salary": 113239,
                        "job": "Hospitality"
                    },
                    {
                        "id": 7,
                        "name": "Madelyn Valdez",
                        "email": "madelyn.v@xyz.com",
                        "phone_number": "04666112313",
                        "salary": 88947,
                        "job": "Cleaner"
                    },
                    {
                        "id": 8,
                        "name": "Avery Herman",
                        "email": "avery.h@xyz.com",
                        "phone_number": "04812536942",
                        "salary": 79649,
                        "job": "Cleaner"
                    },
                    {
                        "id": 9,
                        "name": "Aubrey Lawson",
                        "email": "aubrey.l@xyz.com",
                        "phone_number": "04273218702",
                        "salary": 117566,
                        "job": "Hospitality"
                    },
                    {
                        "id": 10,
                        "name": "Adrian Schmidt",
                        "email": "adrian.s@xyz.com",
                        "phone_number": "04062146984",
                        "salary": 79997,
                        "job": "Hospitality"
                    }
                ]
                 """; 

        // When
        when(staffRepository.listAllStaff())
               .thenReturn(testStaffList);

        // Then
        this.mockMvc.perform(get("/staff/list-all"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(staffListPayload));
    }

    @Test
    @SneakyThrows
    void testGetStaffMemberById_Valid() {

        // Given
        Integer id = 1;
        Optional<Staff> staffOpt1 = Optional.of(createExampleStaffList().get(0));

        String staffPayload = """
                {
                    "id": 1,
                    "name": "Adam Berry",
                    "email": "adam.b@xyz.com",
                    "phone_number": "04854333619",
                    "salary": 85102,
                    "job": "Professor"
                }
                 """; 

        // When
        when(staffRepository.getStaffById(id))
               .thenReturn(staffOpt1);

        // Then
        this.mockMvc.perform(get("/staff/get/{id}",id))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(staffPayload));
    }


    @Test
    @SneakyThrows
    void testGetStaffMemberById_Invalid() {

        // Given
        Integer id = 500;

        // When
        when(staffRepository.getStaffById(id))
                .thenReturn(Optional.empty());

        // Then
        this.mockMvc.perform(get("/staff/get/{id}",id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @SneakyThrows
    void testGetPageOfStaff() {

        // Given
        Integer pageNumber = 0, pageSize = 8, offset = 0;
        List<Staff> staffList = createExampleStaffList();
        staffList.remove(9);
        staffList.remove(8);

        String staffListPayload = """
                { "list": [
                        {
                            "id": 1,
                            "name": "Adam Berry",
                            "email": "adam.b@xyz.com",
                            "phone_number": "04854333619",
                            "salary": 85102,
                            "job": "Professor"
                        },
                        {
                            "id": 2,
                            "name": "Abel Cohen",
                            "email": "abel.c@xyz.com",
                            "phone_number": "04668685844",
                            "salary": 98823,
                            "job": "Student Support"
                        },
                        {
                            "id": 3,
                            "name": "Lila Hubbard",
                            "email": "lila.h@xyz.com",
                            "phone_number": "04718216314",
                            "salary": 101622,
                            "job": "Admin"
                        },
                        {
                            "id": 4,
                            "name": "Wesley Gentry",
                            "email": "wesley.g@xyz.com",
                            "phone_number": "04021648061",
                            "salary": 90055,
                            "job": "Hospitality"
                        },
                        {
                            "id": 5,
                            "name": "Bennett Hood",
                            "email": "bennett.h@xyz.com",
                            "phone_number": "04471504946",
                            "salary": 61583,
                            "job": "Professor"
                        },
                        {
                            "id": 6,
                            "name": "Christopher Corona",
                            "email": "christopher.c@xyz.com",
                            "phone_number": "04268319791",
                            "salary": 113239,
                            "job": "Hospitality"
                        },
                        {
                            "id": 7,
                            "name": "Madelyn Valdez",
                            "email": "madelyn.v@xyz.com",
                            "phone_number": "04666112313",
                            "salary": 88947,
                            "job": "Cleaner"
                        },
                        {
                            "id": 8,
                            "name": "Avery Herman",
                            "email": "avery.h@xyz.com",
                            "phone_number": "04812536942",
                            "salary": 79649,
                            "job": "Cleaner"
                        }
                    ],
                    "totalElements" : 8
                }
                 """;

        // When
        when(staffRepository.getPageOfStaff(pageNumber, pageSize, offset))
                .thenReturn(staffList);

        when(staffRepository.getSizeOfStaffTable())
                .thenReturn(staffList.size());

        // Then
        this.mockMvc.perform(get("/staff/get/page/{pageNo}/{pageSize}", pageNumber, pageSize))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(staffListPayload));
    }

    @Test
    @SneakyThrows
    void testSearchStaffMembers() {

        // Given
        String searchTerm = "Lily";
        Integer pageSize = 10;

        List<Staff> staffList = createExampleStaffList();
        List<Staff> searchFilteredStaffList = new ArrayList<>();
        searchFilteredStaffList.add(staffList.get(2));
        searchFilteredStaffList.add(staffList.get(8));

        String staffListPayload = """
                { "list": [
                        {
                            "id": 3,
                            "name": "Lila Hubbard",
                            "email": "lila.h@xyz.com",
                            "phone_number": "04718216314",
                            "salary": 101622,
                            "job": "Admin"
                        },
                        {
                            "id": 9,
                            "name": "Aubrey Lawson",
                            "email": "aubrey.l@xyz.com",
                            "phone_number": "04273218702",
                            "salary": 117566,
                            "job": "Hospitality"
                        }
                    ],
                    "totalElements" : 1
                }
                 """;

        // When
        when(staffRepository.searchStaffMembers(searchTerm, pageSize))
                .thenReturn(searchFilteredStaffList);

        // Then
        this.mockMvc.perform(get("/staff/search/{searchTerm}/{pageSize}",searchTerm, pageSize))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(staffListPayload));
    }

    @Test
    @SneakyThrows
    void testGetAllProfessors() {

        // Given
        List<Staff> staffList = createExampleStaffList();
        List<Staff> jobFilteredStaffList = new ArrayList<>();
        jobFilteredStaffList.add(staffList.get(0));
        jobFilteredStaffList.add(staffList.get(4));

        String staffListPayload = """
                [
                    {
                        "id": 1,
                        "name": "Adam Berry",
                        "email": "adam.b@xyz.com",
                        "phone_number": "04854333619",
                        "salary": 85102,
                        "job": "Professor"
                    },
                    {
                        "id": 5,
                        "name": "Bennett Hood",
                        "email": "bennett.h@xyz.com",
                        "phone_number": "04471504946",
                        "salary": 61583,
                        "job": "Professor"
                    }
                ]
                 """;

        // When
        when(staffRepository.listAllProfessors())
                .thenReturn(jobFilteredStaffList);

        // Then
        this.mockMvc.perform(get("/staff/list-all-professors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(staffListPayload));
    }

    @Test
    @SneakyThrows
    void testSearchProfessors() {
        // Given
        String searchTerm = "Ben";
        Integer pageSize = 2;

        List<Staff> staffList = createExampleStaffList();
        List<Staff> jobFilteredStaffList = new ArrayList<>();
        jobFilteredStaffList.add(staffList.get(0));
        jobFilteredStaffList.add(staffList.get(4));

        String staffListPayload = """
                { "list": [
                    {
                        "id": 5,
                        "name": "Bennett Hood",
                        "email": "bennett.h@xyz.com",
                        "phone_number": "04471504946",
                        "salary": 61583,
                        "job": "Professor"
                    },
                    {
                        "id": 1,
                        "name": "Adam Berry",
                        "email": "adam.b@xyz.com",
                        "phone_number": "04854333619",
                        "salary": 85102,
                        "job": "Professor"
                    }
                    ],
                    "totalElements" : 1
                }
                 """;

        // When
        when(staffRepository.searchProfessors(searchTerm, pageSize))
                .thenReturn(jobFilteredStaffList);

        // Then
        this.mockMvc.perform(get("/staff/searchProfessor/{pageSize}?searchTerm={searchTerm}", pageSize, searchTerm))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(staffListPayload));

    }

    @Test
    @SneakyThrows
    void testSaveStaffMember_ValidUpdate() {
        // Given
        Staff newStaff = Staff.builder()
                .id(20)
                .name("James Sutton")
                .email("j.sutton@xyz.xyz")
                .phone_number("292901232")
                .salary(200000)
                .job("Admin")
                .build();

        String newStaffPayload = """
                {
                    "id": 20,
                    "name": "James Sutton",
                    "email": "j.sutton@xyz.xyz",
                    "phone_number": "292901232",
                    "salary": 200000,
                    "job": "Admin"
                }
                """;

        StaffMatcher staffPropertyMatcher = StaffMatcher.builder().left(newStaff).build();

        // When
        when(staffRepository.updateStaffMember(argThat(staffPropertyMatcher)))
                .thenReturn(Optional.of(newStaff));

        when(staffRepository.saveNewStaffMember(argThat(staffPropertyMatcher)))
                .thenReturn(Optional.empty());

        // Then
        this.mockMvc.perform(put("/staff/put").contentType(MediaType.APPLICATION_JSON)
                        .content(newStaffPayload))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(newStaffPayload));
    }

    @Test
    @SneakyThrows
    void testSaveStaffMember_InvalidUpdateNonExistent() {
        // Given
        Staff newStaff = Staff.builder()
                .id(20)
                .name("James Sutton")
                .email("j.sutton@xyz.xyz")
                .phone_number("292901232")
                .salary(200000)
                .job("Admin")
                .build();

        String newStaffPayload = """
                {
                    "id": 100,
                    "name": "Jeremy Button",
                    "email": "j.e.button@xyz.xyz",
                    "phone_number": "111111111",
                    "salary": 50000,
                    "job": "Admin"
                }
                """;

        StaffMatcher staffPropertyMatcher = StaffMatcher.builder().left(newStaff).build();

        // When
        when(staffRepository.updateStaffMember(argThat(staffPropertyMatcher)))
                .thenReturn(Optional.empty());

        when(staffRepository.saveNewStaffMember(any()))
                .thenReturn(Optional.empty());

        // Then
        this.mockMvc.perform(put("/staff/put").contentType(MediaType.APPLICATION_JSON)
                        .content(newStaffPayload))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void testSaveStaffMember_InvalidProperties() {
        // Given
        Staff newStaff = Staff.builder()
                .id(20)
                .name("James Sutton")
                .email("j.sutton@xyz.xyz")
                .phone_number("292901232")
                .salary(200000)
                .job("Admin")
                .build();

        String newStaffPayload = """
                {
                    "id": 20,
                    "name": "James Sutton",
                    "email": "j.sutton@xyz.xyz",
                    "phone_number": "292901232",
                    "salary": "Twelve",
                    "job": "Admin"
                }
                """;

        StaffMatcher staffPropertyMatcher = StaffMatcher.builder().left(newStaff).build();

        // When
        when(staffRepository.updateStaffMember(argThat(staffPropertyMatcher)))
                .thenReturn(Optional.empty());

        when(staffRepository.saveNewStaffMember(argThat(staffPropertyMatcher)))
                .thenReturn(Optional.of(newStaff.getId()));

        // Then
        this.mockMvc.perform(put("/staff/put").contentType(MediaType.APPLICATION_JSON)
                        .content(newStaffPayload))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @SneakyThrows
    void testDeleteStaffMember_Valid() {
        // Given
        Integer id = 20;

        Staff exampleStaff = Staff.builder()
                .id(20)
                .name("James Sutton")
                .email("j.sutton@xyz.xyz")
                .phone_number("292901232")
                .salary(200000)
                .job("Admin")
                .build();

        // When
        when(staffRepository.getStaffById(id))
                .thenReturn(Optional.of(exampleStaff));
        when(staffRepository.deleteStaffMember(id))
                .thenReturn(true);

        // Then
        this.mockMvc.perform(delete("/staff/delete/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @SneakyThrows
    void testDeleteStaffMember_InvalidNonExistent() {
        // Given
        Integer id = 5;

        // When
        when(staffRepository.getStaffById(id))
                .thenReturn(Optional.empty());
        when(staffRepository.deleteStaffMember(id))
                .thenReturn(false);

        // Then
        this.mockMvc.perform(delete("/staff/delete/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }



    /// HELPER FUNCTIONS

    private List<Staff> createExampleStaffList() {
        String[] exampleNames = {"Adam Berry", "Abel Cohen", "Lila Hubbard", "Wesley Gentry", "Bennett Hood",
                "Christopher Corona","Madelyn Valdez","Avery Herman", "Aubrey Lawson", "Adrian Schmidt"};
        String[] exampleEmails = {"adam.b@xyz.com", "abel.c@xyz.com", "lila.h@xyz.com", "wesley.g@xyz.com",
                "bennett.h@xyz.com", "christopher.c@xyz.com", "madelyn.v@xyz.com", "avery.h@xyz.com",
                "aubrey.l@xyz.com", "adrian.s@xyz.com"};
        String[] examplePhoneNumber = {"04854333619", "04668685844", "04718216314", "04021648061", "04471504946",
                "04268319791", "04666112313", "04812536942", "04273218702", "04062146984"};
        Integer[] exampleSalaries = {85102, 98823, 101622, 90055, 61583, 113239, 88947, 79649, 117566, 79997};
        String[] exampleJobs = {"Professor", "Student Support", "Admin", "Hospitality", "Professor", "Hospitality",
                "Cleaner", "Cleaner", "Hospitality", "Hospitality"};

        List<Staff> staffList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            staffList.add(Staff.builder()
                    .id(i + 1)
                    .name(exampleNames[i])
                    .email(exampleEmails[i])
                    .phone_number(examplePhoneNumber[i])
                    .salary(exampleSalaries[i])
                    .job(exampleJobs[i])
                    .build());
        }
        return staffList;
    }
}