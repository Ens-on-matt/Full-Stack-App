import { FC } from 'react'
import Degree from "../../assets/Degree.tsx";

interface props {
    degree: Degree;
    showDetails: (id: number) => void;
    toggleModal: (show: boolean) => void;
}

// Creates an box description of the degrees and its details.
// toggleModal is used to toggle when the details should be displayed, where showDetails is used to pass up which degree is to be shown
const DegreeBox:FC<props> = ({ degree, showDetails, toggleModal }  ) => {

    // Activates DegreeDetail modal and provides the degree's ID when this box when pressed.
    const handleOnClick = () => {
        showDetails(degree.id);
        toggleModal(true);
    }

    return (
        <div className="contact__item" onClick={handleOnClick}>
            <div className="contact__header">
                <div className="contact__details">
                    <p className="contact_name">{degree.name.substring(0,15)}</p>
                    <p className="contact_title">ID: {degree.id}</p>
                </div>
            </div>
        </div>
    )
}

export default DegreeBox