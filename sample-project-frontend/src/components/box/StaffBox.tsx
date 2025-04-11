import { FC } from "react";
import Staff from "../../assets/Staff.tsx";

interface props {
    staff: Staff;
    showDetails: (id: number) => void;
    toggleModal: (show: boolean) => void;
}

// Creates an box description of the staff member and its details.
// toggleModal is used to toggle when the details should be displayed, where showDetails is used to pass up which staff member is to be shown
const StaffBox:FC<props> = ({ staff, showDetails, toggleModal }  ) => {

    // Activates StaffDetail modal and provides the staff member's ID when this box when pressed.
    const handleOnClick = () => {
        showDetails(staff.id);
        toggleModal(true);
    }

    return (
        <div className="contact__item" onClick={handleOnClick}>
            <div className="contact__header">
                <div className="contact__details">
                    <p className="contact_name">{staff.name.substring(0, 15)}</p>
                    <p className="contact_title">{staff.job}</p>
                </div>
            </div>
            <div className="contact__body">
                <p><i className="bi bi-envelope"></i>{staff.email.substring(0, 15)}</p>
                <p><i className="bi bi-geo"></i>{staff.salary}</p>
                <p><i className="bi bi-telephone"></i>{staff.phone_number.substring(0, 15)}</p>
            </div>
        </div>
    )
}

export default StaffBox