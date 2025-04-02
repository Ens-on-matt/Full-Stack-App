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


const CourseReport:FC = () => {
    const [course, setCourse] = useState<Course>(new Course(-1, 'Select Course'));
    const [students, setStudents] = useState<Student[]>([]);
    const [professor, setProfessor] = useState<Staff>(new Staff());
    const [degree, setDegree] = useState<Degree>(new Degree());

    const fetchProfessor = async () => {
        const professor : Staff = await getDataEntry(parseInt(course.professor_id), DatabaseTypes.STAFF);
        if (professor?.id) setProfessor(professor);
    }
    
    const fetchDegree = async () => {
        const degree : Degree = await getDataEntry(parseInt(course.degree_id), DatabaseTypes.DEGREE);
        if (degree?.id) setDegree(degree);
    }
        
    const fetchStudents = async () => {
        const courseEnrollments: Enrollment[] = await getEnrollmentsForCourse(course.id);
        if (courseEnrollments.length > 0) {
            const studentsInCourse: Student[] = [];
            for (const enrollment of courseEnrollments) {
                const student: Student = await getDataEntry(parseInt(enrollment.student), DatabaseTypes.STUDENT);
                if (student?.id) studentsInCourse.push(student);
            }
            setStudents(studentsInCourse)
        }
    }

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

    const updateCourseID = (selectedCourse : SingleValue<Course>) => {
        if (selectedCourse) setCourse(selectedCourse);
    }

    useEffect(() => {
        if (course.id != -1) {
            fetchStudents();
            fetchProfessor();
            fetchDegree();
        }
    }, [course]);

    return (
        <main className='main course-report'>
            <AsyncSelect className='course-report-select' value={course} defaultOptions cacheOptions loadOptions={courseOptions} onChange={updateCourseID} required/>
            <div className='report-table'>
                <div className='report-description'>
                    <p className='report-entity'>LECTURER: {professor.name}</p>
                    <p className='report-entity'>DEGREE: {degree.name}</p>
                </div>
                <div className='report-sub-table'>
                    <div className=''><span>STUDENTS</span></div>
                    {students.map((student) => <ul key={student.id}>{student.name}</ul>)}
                </div>
            </div>
        </main>
    )
}


export default CourseReport;