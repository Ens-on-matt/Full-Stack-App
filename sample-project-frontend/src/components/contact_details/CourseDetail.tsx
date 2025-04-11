import React, {FC, useEffect, useState} from 'react'
import {
    getDataEntry, deleteCourse,
    fetchOptionsForAsyncSelect,
    fetchProfessorOptionsForAsyncSelect
} from "../../api/UniService.tsx";
import Course from "../../assets/Course.tsx"
import {toastError, toastSuccess} from "../../api/ToastService.tsx";
import {saveCourse} from "../../api/UniService.tsx";
import DatabaseTypes from "../../assets/DatabaseTypes.tsx";
import {useOutsideClick} from "../form_entry/FormHelperFunctions.tsx";
import {HttpStatusCode} from "axios";
import AsyncSelect from "react-select/async";
import {SingleValue} from "react-select";
import Staff from "../../assets/Staff.tsx";
import Degree from "../../assets/Degree.tsx";

interface props {
    id: number;
    toggleModal: (show: boolean) => void;
    setRefresh: (value : boolean) => void;
}

// Creates element and logic to handle displaying and editing the details of a course given its id
const CourseDetail:FC<props> = ({ id, toggleModal, setRefresh }) => {
    const [course, setCourse] = useState(new Course());
    const [selectedDegree, setSelectedDegree] = useState<Degree>();
    const [selectedProfessor, setSelectedProfessor] = useState<Staff>();

    const updateCourse = async (course: Course) => {
        try {
            await saveCourse(course);
            toastSuccess('Updated course');
        } catch (error: unknown) {
            console.log(error);
            toastError("Encountered an unexpected error while updating course");
        }
    }

    const fetchCourse = async (id: number) => {
        try {
            const data : Course = await getDataEntry(id, DatabaseTypes.COURSE);
            if (data?.name && data?.degree_id) {
                setCourse(data);
                const courseDegree: Degree = await getDataEntry(parseInt(data.degree_id), DatabaseTypes.DEGREE);
                courseDegree.label = courseDegree.name;
                courseDegree.value = `${courseDegree.id}`
                setSelectedDegree(courseDegree);
                const courseProfessor: Staff = await getDataEntry(parseInt(data.professor_id), DatabaseTypes.STAFF);
                courseProfessor.label = courseProfessor.name;
                courseProfessor.value = `${courseProfessor.id}`
                setSelectedProfessor(courseProfessor);
                toastSuccess('Got course');
            } else {
                toastError("Course not found");
                toggleModal(false);
            }

        } catch (error: unknown) {
            console.log(error);
            toastError("Encountered an unexpected error while updating course");
        }
    };

    const confirmAndDeleteCourse = async () => {
        const userConfirmed = window.confirm("Are you sure you want to delete?");
        if (userConfirmed) {
            const responseStatus = await deleteCourse(id, course);
            if (responseStatus == HttpStatusCode.Ok) {
                toastSuccess("Course deleted");
                toggleModal(false);
                setRefresh(true);
            } else {
                toastError("Server error while deleting course")
            }
        } else {
            toastError("Delete canceled.");
        }
        return (<></>);
    };

    const degreeOptions = async (query: string) => {
        const response: Degree[] = await fetchOptionsForAsyncSelect(query, DatabaseTypes.DEGREE);
        return response;
    }

    const updateStaffID = (selectedProfessor : SingleValue<Staff>) => {
        if (selectedProfessor?.id) {
            setSelectedProfessor(selectedProfessor);
            course.professor_id = `${selectedProfessor.id}`
        }
    }

    const updateDegreeID = (selectedDegree : SingleValue<Degree>) => {
        if (selectedDegree?.id) {
            setSelectedDegree(selectedDegree);
            course.degree_id = `${selectedDegree.id}`
        }
    }

    // Updates course state variable each time the user updates a property of the course
    const onCourseFormChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setCourse({...course, [event.target.name]: event.target.value});
    }

    // Updates course when user presses 'Save'.
    const submitCourse = (event: React.ChangeEvent<HTMLFormElement>) => {
        event.preventDefault();
        updateCourse(course);
        setRefresh(true);
    }

    // React Hook to retrieve the course when the id changes.
    useEffect(() => {
        fetchCourse(id);
    }, [id]);

    return (
        <div className='profile__absolute profile__details' ref={useOutsideClick(() => toggleModal(false))}>
            <form onSubmit={submitCourse} className="form">
                <div className='user-details'>
                    <input type="hidden" name="id" defaultValue={course.id} required />
                    <div className='input-box'>
                        <span className="details">Name</span>
                        <input type="text" value={course.name} onChange={onCourseFormChange} name="name" required/>
                    </div>
                    <div className='input-box'>
                        <span className="details">Lecturer</span>
                        <AsyncSelect defaultOptions cacheOptions value={selectedProfessor} loadOptions={fetchProfessorOptionsForAsyncSelect} onChange={updateStaffID} required/>
                    </div>
                    <div className='input-box'>
                        <span className="details">Degree</span>
                        <AsyncSelect defaultOptions cacheOptions value={selectedDegree} loadOptions={degreeOptions} onChange={updateDegreeID} required/>
                    </div>
                </div>
                <div className='form_footer'>
                    <button type="submit" className="btn">Save</button>
                    <button onClick={() => confirmAndDeleteCourse()} className="btn btn-danger">Delete</button>
                </div>
            </form>
        </div>
    )
}

export default CourseDetail;