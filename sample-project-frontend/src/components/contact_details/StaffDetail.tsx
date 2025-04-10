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

    const updateStaffMember = async (staff: Staff) => {
        try {
            await saveStaff(staff);
            toastSuccess('Updated staff');
        } catch (error: unknown) {
            console.log(error);
            toastError("Encountered an unexpected error while updating staff member");
        }
    }

    const fetchStaffMember = async (id: number) => {
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

    const confirmAndDeleteStaffMember = async () => {
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

    // Updates staff state variable each time the user updates a property of the staff member
    const onStaffMemberFormChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setStaff({...staff, [event.target.name]: event.target.value});
    }

    // Updates staff member when user presses 'Save'.
    const submitStaffMember = (event: React.ChangeEvent<HTMLFormElement>) => {
        event.preventDefault();
        updateStaffMember(staff);
        setRefresh(true);
    }


    useEffect(() => {
        fetchStaffMember(id);
    }, [id]);

    return (
        <div className='profile__absolute profile__details' ref={useOutsideClick(() => toggleModal(false))}>
            <form onSubmit={submitStaffMember} className="form">
                <div className='user-details'>
                    <input type="hidden" name="id" defaultValue={staff.id} required />
                    <div className='input-box'>
                        <span className="details">Name</span>
                        <input type="text" value={staff.name} onChange={onStaffMemberFormChange} name="name" required/>
                    </div>
                    <div className='input-box'>
                        <span className="details">Email</span>
                        <input type="text" value={staff.email} onChange={onStaffMemberFormChange} name="email" required/>
                    </div>
                    <div className='input-box'>
                        <span className="details">Phone</span>
                        <input type="text" value={staff.phone_number} onChange={onStaffMemberFormChange} name="phone_number" required/>
                    </div>
                    <div className='input-box'>
                        <span className="details">Job</span>
                        <Select options={JobOptions} value={chosenJob} onChange={(job) => {if (job) {staff.job = job.value; setChosenJob(job)}}} required />
                    </div>
                    <div className='input-box'>
                        <span className="details">Salary</span>
                        <input type="text" value={staff.salary} onChange={onStaffMemberFormChange} name="salary" required/>
                    </div>
                </div>
                <div className='form_footer'>
                    <button type="submit" className="btn">Save</button>
                    <button onClick={() => confirmAndDeleteStaffMember()} className="btn btn-danger">Delete</button>
                </div>
            </form>
        </div>
    )
}

export default StaffDetail;