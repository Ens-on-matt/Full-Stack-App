import {FC, useEffect, useState} from 'react'
import {
    deleteEnrollment, getCoursesForDegree,
    getDataEntry, getEnrollmentsForStudent,
    getPageOfData, saveEnrollment,
    searchAndGetPageOfData, studentPage
} from "../api/UniService.tsx";
import Student from "../assets/Student.tsx"
import {toastError, toastSuccess} from "../api/ToastService.tsx";
import DatabaseTypes from "../assets/DatabaseTypes.tsx";
import AsyncSelect from "react-select/async";
import {SingleValue} from "react-select";
import Degree from "../assets/Degree.tsx";
import Course from "../assets/Course.tsx";
import Enrollment from "../assets/Enrollment.tsx";
import EnrollmentProgress from "../assets/EnrollmentProgress.tsx";
import {Button} from "react-bootstrap";
import INVALID_ID from "../assets/INVALID_ID.tsx";

// Creates a element that displays and lets users edit the courses that a student is enrolled in and their progress.
const StudentEnrollment:FC = () => {
    const [student, setStudent] = useState(new Student(INVALID_ID, 'Select Student','','',''));
    const [coursesForDegree, setCoursesForDegree] = useState<Course[]>();
    const [selectedDegree, setSelectedDegree] = useState<Degree>();
    const [enrollments, setEnrollments] = useState<Enrollment[]>([]);

    // Local constants used to split courses into the relevant column
    const COURSE_NOT_ENROLLED = 0;
    const COURSE_PARTIAL_COMPLETED = 1;
    const COURSE_FULLY_COMPLETED = 2;

    /* FETCH FUNCTIONS THAT GET INFORMATION FROM THE DATABASE */
    const fetchDegree = async (degree_id: number) => {
        try {
            const studentDegree: Degree = await getDataEntry(degree_id, DatabaseTypes.DEGREE);
            setSelectedDegree(studentDegree);
        } catch (error) {
            console.log(error)
            toastError("Error when fetching degree")
        }
    }

    const fetchCourses = async (degree_id : number) => {
        try {
            const coursesForDegree: Course[] = await getCoursesForDegree(degree_id);
            setCoursesForDegree(coursesForDegree);
        } catch (error) {
            console.log(error);
            toastError("Error when fetching courses required for degree");
        }
    }

    const fetchEnrollments = async(student_id: number) => {
        try {
            const enrollmentsForCourse: Enrollment[] = await getEnrollmentsForStudent(student_id);
            setEnrollments(enrollmentsForCourse);
        } catch (error) {
            console.log(error)
            toastError("Error when fetching enrollments")
        }
    }

    // Wrapper async method that handles updates to information relating to the student.
    // (What courses the student is enrolled in, and what courses are required for the degree the student is studying)
    const fetchStudentInfo = async () => {
        try {
            if (student && student.id != INVALID_ID) {
                fetchEnrollments(student.id)
                const degree_id_num = parseInt(student.degree_id)
                fetchDegree(degree_id_num)
                fetchCourses(degree_id_num)
            }
        } catch (error: unknown) {
            console.log(error);
            toastError("Encountered an unexpected error while fetching student");
        }
    };

    // Finds what stage the student has made in the course given a course_id and all enrollments.
    const courseInEnrollments = (course_id: number, enrollments: Enrollment[]) => {
        for (const e of enrollments) {
            if (course_id ==  parseInt(e.course)) {
                if (e.status == EnrollmentProgress.PARTIAL) return COURSE_PARTIAL_COMPLETED;
                if (e.status == EnrollmentProgress.COMPLETED) return COURSE_FULLY_COMPLETED;
            }
        }
        return COURSE_NOT_ENROLLED;
    }

    // Gets first 10 students if no query, else searches for the 10 closest students (by name).
    // The list of students returned have added properties to allow AsyncSelect to interpret the list (set properties .label and .value).
    const studentOptions = async (inputValue: string) => {
        let response: studentPage;
        if (inputValue == '') {
            response = await getPageOfData(0, 10, DatabaseTypes.STUDENT);
        } else {
            response = await searchAndGetPageOfData(inputValue, 10, DatabaseTypes.STUDENT);
        }
        for (const student of response.list) {
            student.label = student.name;
            student.value = `${student.id}`;
        }
        return response.list;
    }

    // Wraps the student state variable with a check that the student returned by the AsyncSelect is valid.
    const updateStudentID = (selectedStudent : SingleValue<Student>) => {
        if (selectedStudent) {
            setStudent(selectedStudent);
        }
    }

    /* FUNCTIONS THAT SEND REQUESTS TO UPDATE OR REMOVE ENTRIES TO THE DATABASE */
    // Update database to enroll student (id) in course (id)
    const enrollInCourse = async (student_id: number, course_id: number) => {
        const newEnrollment = new Enrollment(`${student_id}`, `${course_id}`, EnrollmentProgress.PARTIAL);
        const response : Enrollment = await saveEnrollment(newEnrollment)
        if (response?.status == EnrollmentProgress.PARTIAL) {
            toastSuccess("Enrolled Course");
            fetchStudentInfo();
        } else {
            toastError("Error while enrolling course");
        }
    }

    // Update database to drop student (id) from course (id) (even if student has been stated to complete the course).
    const dropEnrolledCourse = async (student_id: number, course_id: number) => {
        const enrollment = new Enrollment(`${student_id}`, `${course_id}`, EnrollmentProgress.PARTIAL);
        const deletedSuccess = await deleteEnrollment(enrollment);

        if (deletedSuccess) {
            toastSuccess("Dropped Course");
            fetchStudentInfo();
        } else {
            toastError("Error while dropping course");
        }
    }

    // Update database to indicate that student (id) has completed course (id).
    const completeEnrolledCourse = async (student_id: number, course_id: number) => {
        const newEnrollment = new Enrollment(`${student_id}`, `${course_id}`, EnrollmentProgress.COMPLETED);
        const response : Enrollment = await saveEnrollment(newEnrollment)
        if (response?.status == EnrollmentProgress.COMPLETED) {
            toastSuccess("Completed Course");
            fetchStudentInfo();
        } else {
            toastError("Error while completing course");
        }
    }

    // React Hook to update enrollments and course info when student changes
    useEffect(() => {
        fetchStudentInfo();
    }, [student]);

    return (
        <main className='main student-enrollment'>
            <div className='form_footer'>
                <AsyncSelect className='student-enrollment-select' value={student} defaultOptions cacheOptions loadOptions={studentOptions} onChange={updateStudentID} required/>
                {student.id != INVALID_ID && <div className='student-enrollment-ID'>ID<span>{student.id}</span></div>}
            </div>

            <div className="enrollment-degree">{selectedDegree && `Bachelor of ${selectedDegree.name}`}</div>

            <span className='enrollment-header'>Courses</span>
            <div className='enrollment-table'>
                <ul className='sub-table'>
                    <span>TODO</span>
                    { coursesForDegree && coursesForDegree.map((course : Course) => {
                        if (courseInEnrollments(course.id, enrollments) == COURSE_NOT_ENROLLED) {
                           return (<li key={course.id}>{course.name}<div className='enrollment-spacing'/><Button className='bi bi-plus-lg enrollment-btn' onClick={() => enrollInCourse(student.id, course.id)}/></li>);
                        }
                    }
                    )}
                </ul>
                <ul className='sub-table'>
                    <span>IN PROGRESS</span>
                    { coursesForDegree && coursesForDegree.map((course : Course) => {
                            if (courseInEnrollments(course.id, enrollments) == COURSE_PARTIAL_COMPLETED) {
                                return (<li key={course.id}>{course.name}<div className='enrollment-spacing'/><Button className='bi bi-check-lg enrollment-btn' onClick={() => completeEnrolledCourse(student.id, course.id)}/><Button className='btn-danger bi bi-x-lg enrollment-btn' onClick={() => dropEnrolledCourse(student.id, course.id)}/></li>);
                            }
                        }
                    )}
                </ul>
                <ul className='sub-table'>
                    <span>COMPLETED</span>
                    { coursesForDegree && coursesForDegree.map((course : Course) => {
                            if (courseInEnrollments(course.id, enrollments) == COURSE_FULLY_COMPLETED) {
                                return (<li key={course.id}>{course.name}<div className='enrollment-spacing'/><Button className='btn-danger bi bi-x-lg enrollment-btn' onClick={() => dropEnrolledCourse(student.id, course.id)}/></li>);
                            }
                        }
                    )}
                </ul>
            </div>
        </main>
    )
}

export default StudentEnrollment;