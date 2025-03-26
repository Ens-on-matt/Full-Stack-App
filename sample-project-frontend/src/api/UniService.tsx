import axios, {AxiosError, HttpStatusCode} from "axios";
import Staff from "../assets/Staff.tsx"
import Student from "../assets/Student.tsx"
import Course from "../assets/Course.tsx"
import Degree from "../assets/Degree.tsx"
import DatabaseTypes from "../assets/DatabaseTypes.tsx";
import Enrollment from "../assets/Enrollment.tsx";

const API_URL = 'http://localhost:8080'

export interface returningPageFormat {
    list: Array<Staff|Student|Course|Degree>;
    totalElements : number;
    databaseType : string | undefined;
}

export interface staffPage extends returningPageFormat {
    list: Array<Staff>
}

export interface studentPage extends returningPageFormat {
    list: Array<Student>
}

export interface coursePage extends returningPageFormat {
    list: Array<Course>
}

export interface degreePage extends returningPageFormat {
    list: Array<Degree>
}

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

export async function getAllData(dataType : string) {
    try {
        const response = await axios.get(`${API_URL}/${dataType}/list-all`);
        return response.data;
    } catch (error) {
        console.log ('Get axios error ', JSON.stringify(error));
    }
    return null;
}

export async function getAllProfessors() {
    try {
        const response = await axios.get(`${API_URL}/${DatabaseTypes.STAFF}/list-all-professors`);
        return response.data;
    } catch (error) {
        console.log ('Get axios error ', JSON.stringify(error));
    }
    return null;
}

export async function getCoursesForDegree(degree_id: number) {
    try {
        const response = await axios.get(`${API_URL}/${DatabaseTypes.COURSE}/courses-for-degree/${degree_id}`);
        return response.data;
    } catch (error) {
        console.log ('Get axios error ', JSON.stringify(error));
    }
    return [];
}

export async function getEnrollmentsForStudent(student_id: number) {
    try {
        const response = await axios.get(`${API_URL}/${DatabaseTypes.ENROLLMENT}/get-from-student/${student_id}`);
        return response.data;
    } catch (error) {
        console.log ('Get axios error ', JSON.stringify(error));
    }
    return [];
}

export async function getEnrollmentsForCourse(course_id: number) {
    try {
        const response = await axios.get(`${API_URL}/${DatabaseTypes.ENROLLMENT}/get-from-course/${course_id}`);
        return response.data;
    } catch (error) {
        console.log ('Get axios error ', JSON.stringify(error));
    }
    return [];
}


export async function getPageOfData(page : number, size: number, dataType : string) {
    try {
        const response = await axios.get(`${API_URL}/${dataType}/get/page/${page}/${size}`);
        return response.data;
    } catch (error) {
        console.log ('Get axios error ', JSON.stringify(error));
    }
    return null;
}

export async function getDataEntry(id : number, dataType : string) {
    try {
        const response = await axios.get(`${API_URL}/${dataType}/get/${id}`);
        return response.data;
    } catch (error) {
        console.log ('Get axios error ', JSON.stringify(error));
        return null;
    }
}

export async function searchAndGetPageOfData(searchTerm: string, size: number, dataType : string) {
    try {
        const response = await axios.get(`${API_URL}/${dataType}/search/${searchTerm}/${size}`);
        return response.data;
    } catch (error) {
        console.log ('Get axios error ', JSON.stringify(error));
    }
    return null;
}

export async function searchProfessors(searchTerm: string, size: number) {
    try {
        const URL = `${API_URL}/${DatabaseTypes.STAFF}/searchProfessor/${size}`
        const params = searchTerm ? { searchTerm } : { };
        const response = await axios.get(URL, { params });
        return response.data;
    } catch (error) {
        console.log ('Get axios error ', JSON.stringify(error));
    }
    return null;
}


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

export async function deleteEnrollment(id : number, enrollment : Enrollment) {
    try {
        if (id == enrollment.id) {
            const response = await axios.delete(`${API_URL}/${DatabaseTypes.ENROLLMENT}/delete/${id}`);
            return response.status == HttpStatusCode.Ok;
        }
    } catch (error) {
        console.log ('Put axios error ', JSON.stringify(error));
    }
    return false;
}
