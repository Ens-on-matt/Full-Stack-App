import React, {FC, useState} from "react";
import Course from "../../assets/Course.tsx";
import {useOutsideClick} from "./FormHelperFunctions.tsx";
import {
    saveCourse, fetchOptionsForAsyncSelect,
    fetchProfessorOptionsForAsyncSelect,
} from "../../api/UniService.tsx";
import {toastError, toastSuccess} from "../../api/ToastService.tsx";
import DatabaseTypes from "../../assets/DatabaseTypes.tsx";
import AsyncSelect from "react-select/async";
import {SingleValue} from "react-select";
import Degree from "../../assets/Degree.tsx";
import Staff from "../../assets/Staff.tsx";

interface props {
    toggleModal: (show: boolean) => void;
    setRefresh: (refresh: boolean) => void;
}

// Creates element and logic to handle creating a new course
const NewCourse:FC<props> = ({toggleModal, setRefresh}) => {
    const [newCourse, setNewCourse] = useState(new Course());

    const saveNewCourse = async (event: React.ChangeEvent<HTMLFormElement>) => {
        event.preventDefault();
        try {
            const { id } = await saveCourse(newCourse);
            toggleModal(false);
            newCourse.resetArgs();
            toastSuccess(`New course made with an ID of ${id}`);
            setRefresh(true);
        } catch (error: unknown) {
            console.log(error)
            toastError("Unexpected error while saving new course");
        }
    }

    const degreeOptions = async (query: string) => {
        const response: Degree[] = await fetchOptionsForAsyncSelect(query, DatabaseTypes.DEGREE);
        return response;
    }

    const onCourseFormChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setNewCourse({...newCourse, [event.target.name]: event.target.value});
    }

    return (
        <div ref={useOutsideClick(() => toggleModal(false))}>
        <div className="modal__header">
            <h3>New Contact</h3>
            <i onClick={() => toggleModal(false)} className="bi-x-lg"></i>
        </div>
        <div className="divider"></div>
        <form onSubmit={saveNewCourse}>
            <div className="input-box">
                <span className="details">Name</span>
                <input type="text" value={newCourse.name} onChange={onCourseFormChange} name='name' required/>
            </div>
            <div className="input-box">
                <span className="details">Lecturer</span>
                <AsyncSelect defaultOptions cacheOptions loadOptions={fetchProfessorOptionsForAsyncSelect} onChange={(selectedProfessor : SingleValue<Staff>) => {if (selectedProfessor?.id) newCourse.professor_id = `${selectedProfessor.id}`}} required/>
            </div>
            <div className="input-box">
                <span className="details">Degree</span>
                <AsyncSelect defaultOptions cacheOptions loadOptions={degreeOptions} onChange={(selectedDegree : SingleValue<Degree>) => {if (selectedDegree?.id) newCourse.degree_id = `${selectedDegree.id}`}} required/>
            </div>
            <div className="form_footer">
                <button onClick={() => toggleModal(false)} type='button' className="btn btn-danger">Cancel</button>
                <i> </i>
                <button type='submit' className="btn">Submit</button>
            </div>
        </form>
        </div>
    );
};

export default NewCourse;
