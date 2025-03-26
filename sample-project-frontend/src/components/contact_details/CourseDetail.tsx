import React, {FC, useEffect, useState} from 'react'
import {
    degreePage,
    deleteCourse,
    getDataEntry,
    getPageOfData, searchAndGetPageOfData,
    searchProfessors,
    staffPage
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

const CourseDetail:FC<props> = ({ id, toggleModal, setRefresh }) => {
    const [course, setCourse] = useState(new Course(-1, '','',''));
    const [selectedDegree, setSelectedDegree] = useState<Degree>();
    const [selectedProfessor, setSelectedProfessor] = useState<Staff>();


    const updateContact = async (course: Course) => {
        try {
            await saveCourse(course);
            toastSuccess('Updated course');
        } catch (error: unknown) {
            console.log(error);
            toastError("Encountered an unexpected error while updating course");
        }
    }

    const fetchContact = async (id: number) => {
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

    const onChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setCourse({...course, [event.target.name]: event.target.value});
    }

    const submitCourse = (event: React.ChangeEvent<HTMLFormElement>) => {
        event.preventDefault();
        updateContact(course);
        setRefresh(true);
    }

    const onDelete = async () => {
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

    const professorOptions = async (inputValue: string) => {
        const response: staffPage = await searchProfessors(inputValue, 10);
        for (const degree of response.list) {
            degree.label = degree.name;
            degree.value = `${degree.id}`;
        }
        return response.list;
    }

    const updateStaffID = (selectedProfessor : SingleValue<Staff>) => {
        if (selectedProfessor?.id) {
            setSelectedDegree(selectedProfessor);
            course.professor_id = `${selectedProfessor.id}`
        }
    }

    const degreeOptions = async (inputValue: string) => {
        let response: degreePage;
        if (inputValue == '') {
            response = await getPageOfData(0, 10, DatabaseTypes.DEGREE);
        } else {
            response = await searchAndGetPageOfData(inputValue, 10, DatabaseTypes.DEGREE);
        }
        for (const degree of response.list) {
            degree.label = degree.name;
            degree.value = `${degree.id}`;
        }
        return response.list;
    }

    const updateDegreeID = (selectedDegree : SingleValue<Degree>) => {
        if (selectedDegree?.id) {
            setSelectedDegree(selectedDegree);
            course.degree_id = `${selectedDegree.id}`
        }
    }

    useEffect(() => {
        fetchContact(id);
    }, [id]);

    return (
        <div className='profile__absolute profile__details' ref={useOutsideClick(() => toggleModal(false))}>
            <form onSubmit={submitCourse} className="form">
                <div className='user-details'>
                    <input type="hidden" name="id" defaultValue={course.id} required />
                    <div className='input-box'>
                        <span className="details">Name</span>
                        <input type="text" value={course.name} onChange={onChange} name="name" required/>
                    </div>
                    <div className='input-box'>
                        <span className="details">Lecturer</span>
                        <AsyncSelect defaultOptions cacheOptions value={selectedProfessor} loadOptions={professorOptions} onChange={updateStaffID} required/>
                    </div>
                    <div className='input-box'>
                        <span className="details">Degree</span>
                        <AsyncSelect defaultOptions cacheOptions value={selectedDegree} loadOptions={degreeOptions} onChange={updateDegreeID} required/>
                    </div>
                </div>
                <div className='form_footer'>
                    <button type="submit" className="btn">Save</button>
                    <button onClick={() => onDelete()} className="btn btn-danger">Delete</button>
                </div>
            </form>
        </div>
    )
}

export default CourseDetail;