import { FC } from 'react'
import Course from "../../assets/Course.tsx";
import Staff from "../../assets/Staff.tsx";
import Degree from "../../assets/Degree.tsx";

interface props {
    course: Course;
    professors: Staff[] | undefined;
    degrees: Degree[] | undefined;
    showDetails: (id: number) => void;
    toggleModal: (show: boolean) => void;
}

const CourseBox:FC<props> = ({ course, professors, degrees, showDetails, toggleModal }  ) => {

    // Activates CourseDetail modal and provides the course's ID when this box when pressed.
    const handleOnClick = () => {
        showDetails(course.id);
        toggleModal(true);
    }

    return (
        <div className="contact__item" onClick={handleOnClick}>
            <div className="contact__header">
                <div className="contact__details">
                    <p className="contact_name">{course.name}</p>
                    <p className="contact_title">ID: {course.id}</p>
                </div>
            </div>
            <div className="contact__body">
                <p><i className="bi bi-envelope"></i>Lecturer: {professors && professors[parseInt(course.professor_id)]?.name}</p>
                <p><i className="bi bi-telephone"></i>Bachelor of {degrees && degrees[parseInt(course.degree_id)-1]?.name}</p>
            </div>
        </div>
    )
}

export default CourseBox