import { FC } from 'react'
import Degree from "../../assets/Degree.tsx";

interface props {
    degree: Degree;
    showDetails: (id: number) => void;
    toggleModal: (show: boolean) => void;
}

const DegreeBox:FC<props> = ({ degree, showDetails, toggleModal }  ) => {

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