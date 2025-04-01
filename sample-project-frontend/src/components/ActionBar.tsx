import {useState} from "react";
import {Link} from "react-router-dom";

const ActionBar = () => {
    const [isOpen, setIsOpen] = useState(false);
    const toggleActionBar = () => setIsOpen(!isOpen);

    return (
    <div className='action-bar'>

        <nav className={isOpen ? 'nav-menu active' : 'nav-menu'} aria-label="breadcrumb">
            <i color="ffffff" className='toggle-bar menu-bars bi bi-justify' onClick={toggleActionBar}></i>
            <ul className='nav-menu-items'>
                <li className='nav-text'>
                    <Link to="/" className="menu-bars">
                        <i className='bi bi bi-house'> Home</i>
                    </Link>
                </li>
                <li className='nav-text'>
                    <Link to="/course-report" className="menu-bars">
                        <i className='bi bi bi-book'> Course Report</i>
                    </Link>
                </li>
                <li className='nav-text'>
                    <Link to="/student-enrollment" className="menu-bars">
                        <i className='bi bi-people'> Student Enrollment</i>
                    </Link>
                </li>
            </ul>
        </nav>
    </div>
    )
}


export default ActionBar;