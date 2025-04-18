import {FC} from 'react'
import Student from "../../assets/Student.tsx";
import Degree from "../../assets/Degree.tsx";

interface props {
    student: Student;
    degrees: Degree[] | undefined;
    showDetails: (id: number) => void;
    toggleModal: (show: boolean) => void;
}

// Creates an box description of the students and its details, replacing numerical database ids for their corresponding names for clarity.
// Lists for degrees is used to find the name corresponding to the degree_id of the student provided
// toggleModal is used to toggle when the details should be displayed, where showDetails is used to pass up which student is to be shown
const StudentBox:FC<props> = ({ student, degrees, showDetails, toggleModal }  ) => {
    
    // Activates StudentDetail modal and provides the student's ID when this box when pressed.
    const handleOnClick = () => {
        showDetails(student.id)
        toggleModal(true);
    }

    return (
        <div className="contact__item" onClick={handleOnClick}>
            <div className="contact__header">
                <div className="contact__details">
                    <p className="contact_name"> {student.name.substring(0,15)}</p>
                    <p className="contact_title">Bachelor of {degrees && degrees[parseInt(student.degree_id)-1]?.name}</p>
                </div>
            </div>
            <div className="contact__body">
                <p><i className="bi bi-envelope"></i>{student.email.substring(0,15)}</p>
                <p><i className="bi bi-telephone"></i>{student.phone_number.substring(0,15)}</p>
            </div>
        </div>
    )
}

export default StudentBox