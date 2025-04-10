import React, {FC, useEffect, useState} from 'react'
import {deleteStaff, getDataEntry} from "../../api/UniService.tsx";
import Staff from "../../assets/Staff.tsx"
import {toastError, toastSuccess} from "../../api/ToastService.tsx";
import {saveStaff} from "../../api/UniService.tsx";
import DatabaseTypes from "../../assets/DatabaseTypes.tsx";
import {useOutsideClick} from "../form_entry/FormHelperFunctions.tsx";
import {HttpStatusCode} from "axios";
import Select from "react-select";
import {JobOptions, option} from "../../assets/ValidJobs.tsx";

interface props {
    id: number;
    toggleModal: (show: boolean) => void;
    setRefresh: (value : boolean) => void;
}


const StaffDetail:FC<props> = ({ id, toggleModal, setRefresh }) => {
    const [staff, setStaff] = useState(new Staff());
    const [chosenJob, setChosenJob] = useState<option>();

    const updateContact = async (staff: Staff) => {
        try {
            await saveStaff(staff);
            toastSuccess('Updated staff');
        } catch (error: unknown) {
            console.log(error);
            toastError("Encountered an unexpected error while updating staff member");
        }
    }

    const fetchContact = async (id: number) => {
        try {
            const data : Staff = await getDataEntry(id, DatabaseTypes.STAFF);
            if (data?.name && data?.job) {
                setStaff(data);
                setDefaultJob(data.job);
                console.log(staff);
                toastSuccess('Got staff member');
            } else {
                toastError("Staff not found");
                toggleModal(false);
            }
        } catch (error: unknown) {
            console.log(error);
            toastError("Encountered an unexpected error while fetching staff member");
        }
    };

    const onChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setStaff({...staff, [event.target.name]: event.target.value});
    }

    const submitStaff = (event: React.ChangeEvent<HTMLFormElement>) => {
        event.preventDefault();
        updateContact(staff);
        setRefresh(true);
    }

    const onDelete = async () => {
        const userConfirmed = window.confirm("Are you sure you want to delete?");
        if (userConfirmed) {
            const responseStatus = await deleteStaff(id, staff);
            if (responseStatus == HttpStatusCode.Ok) {
                toastSuccess("Staff member deleted");
                toggleModal(false);
                setRefresh(true);
            } else if (responseStatus == HttpStatusCode.BadRequest) {
                toastError("Cannot delete staff - course(s) reference this staff member")
            } else {
                toastError("Unexpected error while deleting degree")
            }
        } else {
            toastError("Delete canceled.");
        }
        return (<></>);
    };

    const setDefaultJob = (job : string) => {
        for (const jobOpt of JobOptions) {
            if (jobOpt.label == job) {
                setChosenJob(jobOpt)
                return;
            }
        }
    }

    useEffect(() => {
        fetchContact(id);
    }, [id]);

    return (
        <div className='profile__absolute profile__details' ref={useOutsideClick(() => toggleModal(false))}>
            <form onSubmit={submitStaff} className="form">
                <div className='user-details'>
                    <input type="hidden" name="id" defaultValue={staff.id} required />
                    <div className='input-box'>
                        <span className="details">Name</span>
                        <input type="text" value={staff.name} onChange={onChange} name="name" required/>
                    </div>
                    <div className='input-box'>
                        <span className="details">Email</span>
                        <input type="text" value={staff.email} onChange={onChange} name="email" required/>
                    </div>
                    <div className='input-box'>
                        <span className="details">Phone</span>
                        <input type="text" value={staff.phone_number} onChange={onChange} name="phone_number" required/>
                    </div>
                    <div className='input-box'>
                        <span className="details">Job</span>
                        <Select options={JobOptions} value={chosenJob} onChange={(job) => {if (job) {staff.job = job.value; setChosenJob(job)}}} required />
                    </div>
                    <div className='input-box'>
                        <span className="details">Salary</span>
                        <input type="text" value={staff.salary} onChange={onChange} name="salary" required/>
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

export default StaffDetail;