import React, {FC, useState} from "react";
import Degree from "../../assets/Degree.tsx";
import {useOutsideClick} from "./FormHelperFunctions.tsx";
import {saveDegree} from "../../api/UniService.tsx";
import {toastError, toastSuccess} from "../../api/ToastService.tsx";

interface props {
    toggleModal: (show: boolean) => void;
    setRefresh: (refresh: boolean) => void;
}

const NewDegree:FC<props> = ({toggleModal, setRefresh}) => {
    const [newDegree, setNewDegree] = useState(new Degree());

    const handleNewDegree = async (event: React.ChangeEvent<HTMLFormElement>) => {
        event.preventDefault();
        try {
            const { id } = await saveDegree(newDegree);
            toggleModal(false);
            newDegree.resetArgs();
            toastSuccess(`New degree made with an ID of ${id}`);
            setRefresh(true);
        } catch (error: unknown) {
            console.log(error)
            toastError("Unexpected error while saving new degree");
        }
    }

    const onChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setNewDegree({...newDegree, [event.target.name]: event.target.value});
    }

    return (
        <div ref={useOutsideClick(() => toggleModal(false))}>
        <div className="modal__header">
            <h3>New Contact</h3>
            <i onClick={() => toggleModal(false)} className="bi-x-lg"></i>
        </div>
        <div className="divider"></div>
        <form onSubmit={handleNewDegree}>
            <div className="input-box">
                <span className="details">Name</span>
                <input type="text" value={newDegree.name} onChange={onChange} name='name' required/>
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

export default NewDegree;
