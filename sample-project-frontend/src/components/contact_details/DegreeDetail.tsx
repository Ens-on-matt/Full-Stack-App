import React, {FC, useEffect, useState} from 'react'
import {getDataEntry, deleteDegree} from "../../api/UniService.tsx";
import Degree from "../../assets/Degree.tsx"
import {toastError, toastSuccess} from "../../api/ToastService.tsx";
import {saveDegree} from "../../api/UniService.tsx";
import DatabaseTypes from "../../assets/DatabaseTypes.tsx";
import {useOutsideClick} from "../form_entry/FormHelperFunctions.tsx";
import {HttpStatusCode} from "axios";

interface props {
    id: number;
    toggleModal: (show: boolean) => void;
    setRefresh: (value : boolean) => void;
}

// Creates element and logic to handle displaying and editing the details of a degree given its id
const DegreeDetail:FC<props> = ({ id, toggleModal, setRefresh }) => {
    const [degree, setDegree] = useState(new Degree());

    const updateDegree = async (degree: Degree) => {
        try {
            await saveDegree(degree);
            toastSuccess('Updated degree');
        } catch (error: unknown) {
            console.log(error);
            toastError("Encountered an unexpected error while updating degree");
        }
    }

    const fetchDegree = async (id: number) => {
        try {
            const data : Degree = await getDataEntry(id, DatabaseTypes.DEGREE);
            if (data?.name) {
                setDegree(data);
                console.log(degree);
                toastSuccess('Got degree');
            } else {
                toastError("Degree not found");
                toggleModal(false);
            }
        } catch (error: unknown) {
            console.log(error);
            toastError("Encountered an unexpected error while fetching degree");
        }
    };

    const confirmAndDeleteDegree = async () => {
        const userConfirmed = window.confirm("Are you sure you want to delete?");
        if (userConfirmed) {
            const responseStatus = await deleteDegree(id, degree);
            if (responseStatus == HttpStatusCode.Ok) {
                toastSuccess("Degree deleted");
                toggleModal(false);
                setRefresh(true);
            } else if (responseStatus == HttpStatusCode.BadRequest) {
                toastError("Cannot delete degree - course(s) reference it")
            } else {
                toastError("Unexpected error while deleting degree")
            }
        } else {
            toastError("Delete canceled.");
        }
        return (<></>);
    };

    // Updates degree state variable each time the user updates a property of the degree
    const onDegreeFormChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setDegree({...degree, [event.target.name]: event.target.value});
    }

    // Updates degree when user presses 'Save'.
    const submitDegree = (event: React.ChangeEvent<HTMLFormElement>) => {
        event.preventDefault();
        updateDegree(degree);
        setRefresh(true);
    }
    

    useEffect(() => {
        fetchDegree(id);
    }, [id]);

    return (
        <div className='profile__absolute profile__details' ref={useOutsideClick(() => toggleModal(false))}>
            <form onSubmit={submitDegree} className="form">
                <div className='user-details'>
                    <input type="hidden" name="id" defaultValue={degree.id} required />
                    <div className='input-box'>
                        <span className="details">Name</span>
                        <input type="text" value={degree.name} onChange={onDegreeFormChange} name="name" required/>
                    </div>
                </div>
                <div className='form_footer'>
                    <button type="submit" className="btn">Save</button>
                    <button onClick={() => confirmAndDeleteDegree()} className="btn btn-danger">Delete</button>
                </div>
            </form>
        </div>
    )
}

export default DegreeDetail;