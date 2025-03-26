import React, {FC, useEffect, useState} from 'react'
import {
    degreePage,
    deleteStudent,
    getDataEntry,
    getPageOfData,
    searchAndGetPageOfData
} from "../../api/UniService.tsx";
import Student from "../../assets/Student.tsx"
import {toastError, toastSuccess} from "../../api/ToastService.tsx";
import {saveStudent} from "../../api/UniService.tsx";
import DatabaseTypes from "../../assets/DatabaseTypes.tsx";
import {useOutsideClick} from "../form_entry/FormHelperFunctions.tsx";
import AsyncSelect from "react-select/async";
import {SingleValue} from "react-select";
import Degree from "../../assets/Degree.tsx";
import {HttpStatusCode} from "axios";

interface props {
    id: number;
    toggleModal: (show: boolean) => void;
    setRefresh: (value : boolean) => void;
}

const StudentDetail:FC<props> = ({ id, toggleModal, setRefresh }) => {
    const [student, setStudent] = useState(new Student(-1, '','','',''));
    const [selectedDegree, setSelectedDegree] = useState<Degree>();

    const updateContact = async (student: Student) => {
        try {
            await saveStudent(student);
            toastSuccess('Updated student');
        } catch (error: unknown) {
            console.log(error);
            toastError("Encountered an unexpected error while updating student");
        }
    }

    const fetchContact = async (id: number) => {
        try {
            const data : Student = await getDataEntry(id, DatabaseTypes.STUDENT);
            if (data?.name && data?.degree_id) {
                setStudent(data);
                const studentDegree: Degree = await getDataEntry(parseInt(data.degree_id), DatabaseTypes.DEGREE);
                studentDegree.label = studentDegree.name;
                studentDegree.value = `${studentDegree.id}`
                setSelectedDegree(studentDegree);
                toastSuccess('Got student');
            } else {
                toastError("Student not found");
                toggleModal(false);
            }
        } catch (error: unknown) {
            console.log(error);
            toastError("Encountered an unexpected error while fetching student");
        }
    };

    const onChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setStudent({...student, [event.target.name]: event.target.value});
    }

    const submitStudent = (event: React.ChangeEvent<HTMLFormElement>) => {
        if (selectedDegree?.id) student.degree_id = `${selectedDegree.id}`

        event.preventDefault();
        updateContact(student);
        setRefresh(true);
    }

    const onDelete = async () => {
        const userConfirmed = window.confirm("Are you sure you want to delete?");
        if (userConfirmed) {
            const responseStatus = await deleteStudent(id, student);
            if (responseStatus == HttpStatusCode.Ok) {
                toastSuccess("Student deleted");
                toggleModal(false);
                setRefresh(true);
            } else {
                toastError("Server error while deleting student")
            }
        } else {
            toastError("Delete canceled.");
        }
        return (<></>);
    };

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
            student.degree_id = `${selectedDegree.id}`
        }
    }


    useEffect(() => {
        fetchContact(id);
    }, [id]);


    return (
        <div className='profile__absolute profile__details' ref={useOutsideClick(() => toggleModal(false))}>
            <form onSubmit={submitStudent} className="form">
                <div className='user-details'>
                    <input type="hidden" name="id" defaultValue={student.id} required />
                    <div className='input-box'>
                        <span className="details">Name</span>
                        <input type="text" value={student.name} onChange={onChange} name="name" required/>
                    </div>
                    <div className='input-box'>
                        <span className="details">Email</span>
                        <input type="text" value={student.email} onChange={onChange} name="email" required/>
                    </div>
                    <div className='input-box'>
                        <span className="details">Phone</span>
                        <input type="text" value={student.phone_number} onChange={onChange} name="phone_number" required/>
                    </div>
                    <div className='input-box'>
                        <span className="details">Degree ID</span>
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

export default StudentDetail;