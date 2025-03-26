import React, {FC, useState} from "react";
import Student from "../../assets/Student.tsx";
import {phoneNumTextOnKeydown, useOutsideClick} from "./FormHelperFunctions.tsx";
import {degreePage, getPageOfData, saveStudent, searchAndGetPageOfData} from "../../api/UniService.tsx";
import {toastError, toastSuccess} from "../../api/ToastService.tsx";
import AsyncSelect from "react-select/async";
import DatabaseTypes from "../../assets/DatabaseTypes.tsx";
import Degree from "../../assets/Degree.tsx";
import {SingleValue} from "react-select";

interface props {
    toggleModal: (show: boolean) => void;
    setRefresh: (refresh: boolean) => void;
}

const NewStudent:FC<props> = ({toggleModal, setRefresh}) => {
    const [newStudent, setNewStudent] = useState(new Student());

    const handleNewStudent = async (event: React.ChangeEvent<HTMLFormElement>) => {
        event.preventDefault();
        try {
            const { id } = await saveStudent(newStudent);
            toggleModal(false);
            newStudent.resetArgs();
            toastSuccess(`New student made with an ID of ${id}`);
            setRefresh(true);
        } catch (error: unknown) {
            console.log(error)
            toastError("Unexpected error while saving new student");
        }
    }

    const onChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setNewStudent({...newStudent, [event.target.name]: event.target.value});
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

    return (
        <div ref={useOutsideClick(() => toggleModal(false))}>
            <div className="modal__header">
                <h3>New Contact</h3>
                <i onClick={() => toggleModal(false)} className="bi-x-lg"></i>
            </div>
            <div className="divider"></div>
            <form onSubmit={handleNewStudent}>
                <div className="input-box">
                    <span className="details">Name</span>
                    <input type="text" value={newStudent.name} onChange={onChange} name='name' required/>
                </div>
                <div className="input-box">
                    <span className="details">Email</span>
                    <input type="text" value={newStudent.email} onChange={onChange} name='email' required/>
                </div>
                <div className="input-box">
                    <span className="details">Phone Number</span>
                    <input type="text" onKeyDown={phoneNumTextOnKeydown} value={newStudent.phone_number} onChange={onChange} name='phone_number' required/>
                </div>
                <div className="input-box">
                    <span className="details">Degree</span>
                    <AsyncSelect defaultOptions cacheOptions loadOptions={degreeOptions} onChange={(selectedDegree : SingleValue<Degree>) => {if (selectedDegree?.id) newStudent.degree_id = `${selectedDegree.id}`}} required/>
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

export default NewStudent;
