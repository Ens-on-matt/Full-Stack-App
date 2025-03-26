import React, {FC, useState} from "react";
import Staff from "../../assets/Staff.tsx";
import {phoneNumTextOnKeydown, useOutsideClick} from "./FormHelperFunctions.tsx";
import {saveStaff} from "../../api/UniService.tsx";
import {toastError, toastSuccess} from "../../api/ToastService.tsx";
import {JobOptions} from "../../assets/ValidJobs.tsx";
import Select from "react-select";

interface props {
    toggleModal: (show: boolean) => void;
    setRefresh: (refresh: boolean) => void;
}

const NewStaff:FC<props> = ({toggleModal, setRefresh}) => {
    const [newStaff, setNewStaff] = useState(new Staff());

    const handleNewStaff = async (event: React.ChangeEvent<HTMLFormElement>) => {
        event.preventDefault();
        try {
            const { id } = await saveStaff(newStaff);
            toggleModal(false);
            newStaff.resetArgs();
            toastSuccess(`New Staff Member made with an ID of ${id}`);
            setRefresh(true);
        } catch (error: unknown) {
            console.log(error)
            toastError("Unexpected error while saving new staff");
        }
    }

    const onChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setNewStaff({...newStaff, [event.target.name]: event.target.value});
    }

    return (
        <div ref={useOutsideClick(() => toggleModal(false))}>
        <div className="modal__header">
            <h3>New Contact</h3>
            <i onClick={() => toggleModal(false)} className="bi-x-lg"></i>
        </div>
        <div className="divider"></div>
        <form onSubmit={handleNewStaff}>
            <div className="input-box">
                <span className="details">Name</span>
                <input type="text" value={newStaff.name} onChange={onChange} name='name' required/>
            </div>
            <div className="input-box">
                <span className="details">Email</span>
                <input type="text" value={newStaff.email} onChange={onChange} name='email' required/>
            </div>
            <div className="input-box">
                <span className="details">Job</span>
                <Select options={JobOptions} onChange={(job) => {if (job) newStaff.job = job.value}} required />
            </div>
            <div className="input-box">
                <span className="details">Phone Number</span>
                <input type="text" value={newStaff.phone_number} onChange={onChange} name='phone_number' required/>
            </div>
            <div className="input-box">
                <span className="details">Salary</span>
                <input type="text" onKeyDown={phoneNumTextOnKeydown} value={newStaff.salary} onChange={onChange} name='salary' required/>
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

export default NewStaff;
