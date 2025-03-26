import {FC, useEffect, useRef, useState} from 'react'
import {getAllData, getPageOfData, searchAndGetPageOfData, studentPage} from "../../api/UniService.tsx";
import {toastError, toastSuccess} from "../../api/ToastService.tsx";
import DatabaseTypes from "../../assets/DatabaseTypes.tsx";
import Student from "../../assets/Student.tsx";
import StudentBox from "../box/StudentBox.tsx";
import {paginationTabs} from "./ListHelperFunctions.tsx";
import StudentDetail from "../contact_details/StudentDetail.tsx";
import Degree from "../../assets/Degree.tsx";

interface props {
    pageSize: number,
    refresh: boolean,
    searchTerm: string,
    setRefresh: (value : boolean) => void,
}

const StudentList:FC<props> = ({ pageSize, searchTerm, refresh, setRefresh }) => {
    const [totalPages, setTotalPages] = useState(0);
    const [studentList, setStudentList] = useState<Student[]>();
    const [pageDisplayed, setPageDisplayed] = useState(0);
    const [studentDetails, setStudentDetails] = useState(0);
    const studentDetailsModal = useRef<HTMLDialogElement>(null);
    const [useSearchResults, setUseSearchResults] = useState(false);
    const [degrees, setDegrees] = useState<Degree[]>();

    const getPage = async (page: number, size: number) => {
        try {
            if (!useSearchResults) {
                const data : studentPage = await getPageOfData(page, size, DatabaseTypes.STUDENT);
                setTotalPages(Math.ceil(data.totalElements/size))
                setStudentList(data.list)
                toastSuccess(`Student page retrieved`);
            }
        } catch (error: unknown) {
            console.log(error)
            toastError("Unexpected error while retrieving student page");
        }
    }

    const searchStudent = async (searchTerm: string) => {
        if (searchTerm == "") {
            setUseSearchResults(false);
            return;
        }

        try {
            const response : studentPage = await searchAndGetPageOfData(searchTerm, pageSize, DatabaseTypes.STUDENT);
            if (response != null) {
                toastSuccess(`Search Completed`);
                setUseSearchResults(true);
                setTotalPages(response.totalElements)
                setStudentList(response.list)
            }

        } catch (error: unknown) {
            console.log(error)
            if (error instanceof Error) {
                toastError(error.message);
            } else {
                toastError("Unexpected Error");
            }
        }
    };

    const getAllDegrees = async () => {
        const response:Degree[] = await getAllData(DatabaseTypes.DEGREE);
        setDegrees(response);
    }

    const toggleModal = (show: boolean): void => show ? studentDetailsModal.current?.showModal() : studentDetailsModal.current?.close();

    useEffect(() => {
        getAllDegrees();
    }, []);

    useEffect(() => {
        searchStudent(searchTerm);
    }, [searchTerm]);

    useEffect(() => {
        getPage(pageDisplayed, pageSize)
    }, [pageDisplayed, pageSize]);

    useEffect(() => {
        if (refresh) getPage(pageDisplayed, pageSize);
    }, [refresh]);

    if (studentList == null) {
        return;
    }
    return (
        <main className='main'>
            <dialog ref={studentDetailsModal} className="modal" id="studentDetailModal">
                {studentDetails != 0 && <StudentDetail id={studentDetails} toggleModal={toggleModal} setRefresh={setRefresh}/>}
            </dialog>

            {studentList?.length === 0 && <div>No Contacts</div>}

            <ul className='contact__list'>
                {studentList?.length > 0 && studentList.map((student: Student) => <StudentBox student={student} showDetails={setStudentDetails} degrees={degrees} toggleModal={toggleModal} key={student.id}/>)}
            </ul>

            {studentList.length > 0 && totalPages > 1 &&
                <div className='pagination'>
                    <a onClick={() => setPageDisplayed(0)} className={0 === pageDisplayed ? 'disabled' : ''}> &laquo; </a>
                    { studentList && paginationTabs(pageDisplayed, totalPages).map((page : number) =>
                        <a onClick={() => setPageDisplayed(page)} className={pageDisplayed === page ? 'active' : ''} key={page}>{page + 1}</a>)}
                    <a onClick={() => setPageDisplayed(totalPages-1)} className={totalPages === pageDisplayed + 1 ? 'disabled' : ''}> &raquo; </a>
                </div>}
        </main>
    )
}

export default StudentList