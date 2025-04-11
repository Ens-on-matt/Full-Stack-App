import axios, {AxiosError, HttpStatusCode} from "axios";
import Staff from "../assets/Staff.tsx"
import Student from "../assets/Student.tsx"
import Course from "../assets/Course.tsx"
import Degree from "../assets/Degree.tsx"
import DatabaseTypes from "../assets/DatabaseTypes.tsx";
import Enrollment from "../assets/Enrollment.tsx";

// URL pointing to where the location of the Spring Boot Application is located
const API_URL = 'http://localhost:8080'

// Generic Interface for the returning data format for the function getPageOfData.
export interface returningPageFormat {
    list: Array<Staff|Student|Course|Degree>;
    totalElements : number;
    databaseType : string | undefined;
}

// Data format that getPageOfData(dataType = DatabaseTypes.STAFF) returns.
export interface staffPage extends returningPageFormat {
    list: Array<Staff>
}

// Data format that getPageOfData(dataType = DatabaseTypes.STUDENT) returns.
export interface studentPage extends returningPageFormat {
    list: Array<Student>
}

// Data format that getPageOfData(dataType = DatabaseTypes.COURSE) returns.
export interface coursePage extends returningPageFormat {
    list: Array<Course>
}

// Data format that getPageOfData(dataType = DatabaseTypes.DEGREE) returns.
export interface degreePage extends returningPageFormat {
    list: Array<Degree>
}

// Save staff member (set staff.id=INVALID_ID to indicate that a new staff member should be made, else it will update the staff member with the same id).
export async function saveStaff(staff : Staff) {
    try {
        const requestEndPoint = `${API_URL}/${DatabaseTypes.STAFF}/put`;
        const response = await axios.put(requestEndPoint, staff);
        return response.data;
    } catch (error) {
        console.log ('Put axios error ', JSON.stringify(error));
    }
    return null;
}

// Save student (set student.id=INVALID_ID to indicate that a new student should be made, else it will update the student with the same id).
export async function saveStudent(student : Student) {
    try {
        const requestEndPoint = `${API_URL}/${DatabaseTypes.STUDENT}/put`;
        const response = await axios.put(requestEndPoint, student);
        return response.data;
    } catch (error) {
        console.log ('Put axios error ', JSON.stringify(error));
    }
    return null;
}

// Save course (set course.id=INVALID_ID to indicate that a new course should be made, else it will update the course with the same id).
export async function saveCourse(course : Course) {
    try {
        const requestEndPoint = `${API_URL}/${DatabaseTypes.COURSE}/put`;
        const response = await axios.put(requestEndPoint, course);
        return response.data;
    } catch (error) {
        console.log ('Put axios error ', JSON.stringify(error));
    }
    return null;
}

// Save degree (set degree.id=INVALID_ID to indicate that a new degree should be made, else it will update the degree with the same id).
export async function saveDegree(degree : Degree) {
    try {
        const requestEndPoint = `${API_URL}/${DatabaseTypes.DEGREE}/put`;
        const response = await axios.put(requestEndPoint, degree);
        return response.data;
    } catch (error) {
        console.log ('Put axios error ', JSON.stringify(error));
    }
    return null;
}

// Save enrollment (if enrollment exists it will be updated, else a new enrollment will be made)
export async function saveEnrollment(enrollment : Enrollment) {
    try {
        const requestEndPoint = `${API_URL}/${DatabaseTypes.ENROLLMENT}/put`;
        const response = await axios.put(requestEndPoint, enrollment);
        return response.data;
    } catch (error) {
        console.log ('Put axios error ', JSON.stringify(error));
    }
    return null;
}

// Get all items of a given dataType.
export async function getAllData(dataType : string) {
    try {
        const response = await axios.get(`${API_URL}/${dataType}/list-all`);
        return response.data;
    } catch (error) {
        console.log ('Get axios error ', JSON.stringify(error));
    }
    return null;
}

// Get all professors (staff members with job of 'professor')
export async function getAllProfessors() {
    try {
        const response = await axios.get(`${API_URL}/${DatabaseTypes.STAFF}/list-all-professors`);
        return response.data;
    } catch (error) {
        console.log ('Get axios error ', JSON.stringify(error));
    }
    return null;
}

// Get all courses required for the degree.
export async function getCoursesForDegree(degree_id: number) {
    try {
        const response = await axios.get(`${API_URL}/${DatabaseTypes.COURSE}/courses-for-degree/${degree_id}`);
        return response.data;
    } catch (error) {
        console.log ('Get axios error ', JSON.stringify(error));
    }
    return [];
}

// Get all course enrollments the student has made.
export async function getEnrollmentsForStudent(student_id: number) {
    try {
        const response = await axios.get(`${API_URL}/${DatabaseTypes.ENROLLMENT}/get-from-student/${student_id}`);
        return response.data;
    } catch (error) {
        console.log ('Get axios error ', JSON.stringify(error));
    }
    return [];
}

// Get all course enrollments that have been done for this course.
export async function getEnrollmentsForCourse(course_id: number) {
    try {
        const response = await axios.get(`${API_URL}/${DatabaseTypes.ENROLLMENT}/get-from-course/${course_id}`);
        return response.data;
    } catch (error) {
        console.log ('Get axios error ', JSON.stringify(error));
    }
    return [];
}

// Get a page of items of dataType in alphabetical order. 'size' determines the max size of the list returned.
export async function getPageOfData(page : number, size: number, dataType : string) {
    try {
        const response = await axios.get(`${API_URL}/${dataType}/get/page/${page}/${size}`);
        return response.data;
    } catch (error) {
        console.log ('Get axios error ', JSON.stringify(error));
    }
    return null;
}

// Get the item of dataType with the id provided.
export async function getDataEntry(id : number, dataType : string) {
    try {
        const response = await axios.get(`${API_URL}/${dataType}/get/${id}`);
        return response.data;
    } catch (error) {
        console.log ('Get axios error ', JSON.stringify(error));
        return null;
    }
}

// Get the enrollment with student_id and course_id provided.
// This requires a separate function as the primary key for enrollment is a composite primary key of student_id and course_id.
export async function getEnrollmentById(student_id : number, course_id : number) {
    try {
        const response = await axios.get(`${API_URL}/${DatabaseTypes.ENROLLMENT}/get/${student_id}-${course_id}`);
        return response.data;
    } catch (error) {
        console.log ('Get axios error ', JSON.stringify(error));
        return null;
    }
}


// Search for items of dataType with names closest to 'query'. 'size' determines the max size of the list returned.
export async function searchAndGetPageOfData(query: string, size: number, dataType : string) {
    try {
        const response = await axios.get(`${API_URL}/${dataType}/search/${query}/${size}`);
        return response.data;
    } catch (error) {
        console.log ('Get axios error ', JSON.stringify(error));
    }
    return null;
}

// Search for professors (staff member with job of professor) with names closest to 'query'
export async function searchProfessors(query: string, size: number) {
    try {
        const URL = `${API_URL}/${DatabaseTypes.STAFF}/searchProfessor/${size}`
        const params = query ? { query } : { };
        const response = await axios.get(URL, { params });
        return response.data;
    } catch (error) {
        console.log ('Get axios error ', JSON.stringify(error));
    }
    return null;
}


/* DELETE FUNCTIONS TO DELETE ENTRIES FROM THE DATABASE */

export async function deleteStaff(id : number, staff : Staff) {
    try {
        if (id == staff.id) {
            const response = await axios.delete(`${API_URL}/${DatabaseTypes.STAFF}/delete/${id}`);
            return response.status;
        }
    } catch (error) {
        console.log ('Put axios error ', JSON.stringify(error));
        if (error instanceof AxiosError) {
            return error.status;
        }
    }
    return HttpStatusCode.NoContent;
}

export async function deleteStudent(id : number, student : Student) {
    try {
        if (id == student.id) {
            const response = await axios.delete(`${API_URL}/${DatabaseTypes.STUDENT}/delete/${id}`);
            return response.status;
        }
    } catch (error) {
        console.log ('Put axios error ', JSON.stringify(error));
        if (error instanceof AxiosError) {
            return error.status;
        }
    }
    return HttpStatusCode.NoContent;
}

export async function deleteCourse(id : number, course : Course) {
    try {
        if (id == course.id) {
            const response = await axios.delete(`${API_URL}/${DatabaseTypes.COURSE}/delete/${id}`);
            return response.status;
        }
    } catch (error) {
        console.log ('Put axios error ', JSON.stringify(error));
        if (error instanceof AxiosError) {
            return error.status;
        }
    }
    return HttpStatusCode.NoContent;
}

export async function deleteDegree(id : number, degree : Degree) {
    try {
        if (id == degree.id) {
            const response = await axios.delete(`${API_URL}/${DatabaseTypes.DEGREE}/delete/${id}`);
            return response.status;
        }
    } catch (error) {
        console.log ('Put axios error ', JSON.stringify(error));
        if (error instanceof AxiosError) {
            return error.status;
        }
    }
    return HttpStatusCode.NoContent;
}

export async function deleteEnrollment(enrollment : Enrollment) {
    try {
        const response = await axios.delete(`${API_URL}/${DatabaseTypes.ENROLLMENT}/delete/${enrollment.student}-${enrollment.course}`);
        return response.status == HttpStatusCode.Ok;
    } catch (error) {
        console.log ('Put axios error ', JSON.stringify(error));
    }
    return false;
}

// Gets first 10 items of given type (alphabetical order) if query is an empty string, else searches for the 10 items with the most similar names.
// The list of items returned have added properties to allow AsyncSelect to interpret the list (set properties .label and .value).
// dataType can be either Staff, Student, Course or Degree (use DatabaseTypes.STAFF, etc).
export async function fetchOptionsForAsyncSelect(query: string, dataType: string) {
    let response: returningPageFormat;
    if (query == '') {
        response = await getPageOfData(0, 10, dataType);
    } else {
        response = await searchAndGetPageOfData(query, 10, dataType);
    }
    for (const item of response.list) {
        item.label = item.name;
        item.value = `${item.id}`;
    }
    return response.list;
}

// Gets first 10 professors (staff members with job=="professor") if query is an empty string, else searches for the 10 closest professors (by name).
// The list of professors returned have added properties to allow AsyncSelect to interpret the list (set properties .label and .value).
export async function fetchProfessorOptionsForAsyncSelect(query: string) {
    const response: staffPage = await searchProfessors(query, 10);
    for (const degree of response.list) {
        degree.label = degree.name;
        degree.value = `${degree.id}`;
    }
    return response.list;
}
