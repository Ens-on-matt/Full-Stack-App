import {FC} from "react";
import {Link} from "react-router-dom";

// Simple landing page to introduce users to the application
const LandingPage:FC = () => {
    return (
        <main className='main'>
            <h1>Welcome to the university database</h1>
            <p>Click one of the lives below to learn more about the university's staff, students, degrees and courses</p>
            <br/><br/>
            <h1>The database contains the following data: </h1>

            <ul>
                <li>
                    <h2><Link to="/staff" className='landing-page-link'>Staff</Link></h2>
                    <h4>&#8226; Name, Email, Phone Number, Salary, Job</h4>
                    <br/>
                </li>
                <li>
                    <h2><Link to="/student" className='landing-page-link'>Student</Link></h2>
                    <h4>&#8226; Name, Email, Phone Number, Degree</h4>
                    <br/>
                </li>
                <li>
                    <h2><Link to="/course" className='landing-page-link'>Course</Link></h2>
                    <h4>&#8226; Name, Degree</h4>
                    <br/>
                </li>
                <li>
                    <h2><Link to="/degree" className='landing-page-link'>Degree</Link></h2>
                    <h4>&#8226; Name</h4>
                    <br/>
                </li>
            </ul>
            <p>Click on the top tabs or on one of the links to go visit that specific database</p>
            <br/><br/><br/><br/><br/>
        </main>
    )
}

export default LandingPage