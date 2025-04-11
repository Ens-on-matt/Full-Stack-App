import {FC, useEffect, useState} from "react";
import {
    coursePage, getPageOfData,
    getDataEntry, getEnrollmentsForCourse,
    searchAndGetPageOfData
} from "../api/UniService.tsx";
import Course from "../assets/Course.tsx";
import DatabaseTypes from "../assets/DatabaseTypes.tsx";
import AsyncSelect from "react-select/async";
import {SingleValue} from "react-select";
import Student from "../assets/Student.tsx";
import Enrollment from "../assets/Enrollment.tsx";
import Staff from "../assets/Staff.tsx";
import Degree from "../assets/Degree.tsx";
import EnrollmentProgress from "../assets/EnrollmentProgress.tsx";
import INVALID_ID from "../assets/INVALID_ID.tsx";

// Creates an element that displays the course report of a given course (course, degree, professors and all enrolled students)
const CourseReport:FC = () => {
    const [course, setCourse] = useState<Course>(new Course(-1, 'Select Course'));
    const [students, setStudents] = useState<Student[]>([]);
    const [professor, setProfessor] = useState<Staff>(new Staff());
    const [degree, setDegree] = useState<Degree>(new Degree());

    /* FETCH FUNCTIONS THAT GET INFORMATION FROM THE DATABASE */
    const fetchProfessorTeachingCourse = async () => {
        const professor : Staff = await getDataEntry(parseInt(course.professor_id), DatabaseTypes.STAFF);
        if (professor?.id) setProfessor(professor);
    }
    
    const fetchDegree = async () => {
        const degree : Degree = await getDataEntry(parseInt(course.degree_id), DatabaseTypes.DEGREE);
        if (degree?.id) setDegree(degree);
    }
        
    const fetchStudentsInCourse = async () => {
        const courseEnrollments: Enrollment[] = await getEnrollmentsForCourse(course.id);
        if (courseEnrollments.length > 0) {
            const studentsInCourse: Student[] = [];
            for (const enrollment of courseEnrollments) {
                if (enrollment.status == EnrollmentProgress.PARTIAL) {
                    const student: Student = await getDataEntry(parseInt(enrollment.student), DatabaseTypes.STUDENT);
                    if (student?.id) studentsInCourse.push(student);
                }
            }
            setStudents(studentsInCourse)
        }
    }

    // Gets first 10 courses if no query, else searches for the 10 closest courses (by name).
    // The list of courses returned have added properties to allow AsyncSelect to interpret the list (set properties .label and .value).
    const courseOptions = async (inputValue: string) => {
        let response: coursePage;
        if (inputValue == '') {
            response = await getPageOfData(0, 10, DatabaseTypes.COURSE);
        } else {
            response = await searchAndGetPageOfData(inputValue, 10, DatabaseTypes.COURSE);
        }
        for (const course of response.list) {
            course.label = course.name;
            course.value = `${course.id}`;
        }
        return response.list;
    }

    // Wraps the course state variable with a check that the student returned by the AsyncSelect is valid.
    const updateCourseID = (selectedCourse : SingleValue<Course>) => {
        if (selectedCourse) setCourse(selectedCourse);
    }

    // React Hook to update degree, students and professor when course changes.
    useEffect(() => {
        if (course.id != INVALID_ID) {
            fetchDegree();
            fetchStudentsInCourse();
            fetchProfessorTeachingCourse();
        }
    }, [course]);

    return (
        <main className='main course-report'>
            <AsyncSelect className='course-report-select' value={course} defaultOptions cacheOptions loadOptions={courseOptions} onChange={updateCourseID} required/>
            <div className='report-table'>
                <div className='report-description'>
                    <div className='report-entity'>LECTURER <p className='report-entity-description'>{professor.name}</p></div>
                    <div className='report-entity'>DEGREE <p className='report-entity-description'>{degree.name}</p></div>
                </div>
                <div className='report-sub-table'>
                    <div>STUDENTS</div>
                    {students.map((student) => <ul key={student.id}>{student.name}</ul>)}
                </div>
            </div>
        </main>
    )
}


export default CourseReport;