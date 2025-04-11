import {useEffect, useRef, useState} from 'react'
import 'react-toastify/dist/ReactToastify.css'
import Header from './components/Header';
import {Navigate, Route, Routes} from 'react-router-dom';
import {ToastContainer} from "react-toastify";
import DatabaseTypes from "./assets/DatabaseTypes.tsx";
import LandingPage from "./components/LandingPage.tsx";
import StaffList from "./components/tables/StaffList.tsx";
import StudentList from "./components/tables/StudentList.tsx";
import CourseList from "./components/tables/CourseList.tsx";
import DegreeList from "./components/tables/DegreeList.tsx";
import BlankForm from "./components/form_entry/BlankForm.tsx";
import NewStaff from "./components/form_entry/NewStaff.tsx";
import NewStudent from "./components/form_entry/NewStudent.tsx";
import NewCourse from "./components/form_entry/NewCourse.tsx";
import NewDegree from "./components/form_entry/NewDegree.tsx";
import ActionBar from "./components/ActionBar.tsx";
import StudentEnrollment from "./components/StudentEnrollment.tsx";
import CourseReport from "./components/CourseReport.tsx";

function App() {
  const newEntryModal = useRef<HTMLDialogElement>(null);
  const pageSize = 12; // number of items in a page
  const [refresh, setRefresh] = useState(false); // used by New____ element to inform ____List element to reload list
  const [searchTerm, setSearchTerm] = useState("");

  // toggles form element on or off
  const toggleFormModal = (show: boolean): void => show ? newEntryModal.current?.showModal() : newEntryModal.current?.close();

  useEffect(() => {
    setRefresh(false);
  }, [refresh]);

  return (
    <div>
      <Routes>
        <Route path='*' element={<Header toggleModal={toggleFormModal} submitSearchTerm={setSearchTerm} database={DatabaseTypes.HOME}/>}/>
        <Route path='/staff' element={<Header toggleModal={toggleFormModal}  submitSearchTerm={setSearchTerm} database={DatabaseTypes.STAFF}/>} />
        <Route path='/student' element={<Header toggleModal={toggleFormModal} submitSearchTerm={setSearchTerm} database={DatabaseTypes.STUDENT}/>} />
        <Route path='/course' element={<Header toggleModal={toggleFormModal} submitSearchTerm={setSearchTerm} database={DatabaseTypes.COURSE}/>} />
        <Route path='/degree' element={<Header toggleModal={toggleFormModal} submitSearchTerm={setSearchTerm} database={DatabaseTypes.DEGREE}/>} />
      </Routes>

      <ActionBar/>
      <ToastContainer/>

      <main className='main'>
      <div className='container'>
        <Routes>
          <Route path='*' element={<Navigate to='/' />} />
          <Route path='/' element={<LandingPage />}/>
          <Route path='/staff' element={<StaffList pageSize={pageSize} searchTerm={searchTerm} refresh={refresh} setRefresh={setRefresh}/>} />
          <Route path='/student' element={<StudentList pageSize={pageSize} searchTerm={searchTerm} refresh={refresh} setRefresh={setRefresh}/>} />
          <Route path='/course' element={<CourseList pageSize={pageSize} searchTerm={searchTerm} refresh={refresh} setRefresh={setRefresh}/>} />
          <Route path='/degree' element={<DegreeList pageSize={pageSize} searchTerm={searchTerm} refresh={refresh} setRefresh={setRefresh}/>} />
          <Route path='/student-enrollment' element={<StudentEnrollment />} />
          <Route path='/course-report' element={<CourseReport />} />
        </Routes>
      </div>
      </main>

      <dialog ref={newEntryModal} className="modal" id="formModal">
        <Routes>
          <Route path='*' element={<BlankForm toggleModal={toggleFormModal}/>} />
          <Route path='/staff' element={<NewStaff toggleModal={toggleFormModal} setRefresh={setRefresh}/>} />
          <Route path='/student' element={<NewStudent toggleModal={toggleFormModal} setRefresh={setRefresh}/>} />
          <Route path='/course' element={<NewCourse toggleModal={toggleFormModal} setRefresh={setRefresh}/>} />
          <Route path='/degree' element={<NewDegree toggleModal={toggleFormModal} setRefresh={setRefresh}/>} />
        </Routes>
      </dialog>
    </div>
  )
}

export default App
