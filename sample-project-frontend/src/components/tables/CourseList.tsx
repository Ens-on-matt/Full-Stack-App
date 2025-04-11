import {FC, useEffect, useRef, useState} from 'react'
import {
    coursePage,
    getAllData,
    getAllProfessors,
    getPageOfData,
    searchAndGetPageOfData,
} from "../../api/UniService.tsx";
import {toastError, toastSuccess} from "../../api/ToastService.tsx";
import DatabaseTypes from "../../assets/DatabaseTypes.tsx";
import Course from "../../assets/Course.tsx";
import CourseBox from "../box/CourseBox.tsx";
import {paginationTabs} from "./ListHelperFunctions.tsx";
import CourseDetail from "../contact_details/CourseDetail.tsx";
import Degree from "../../assets/Degree.tsx";
import Staff from "../../assets/Staff.tsx";

interface props {
    pageSize: number,
    refresh: boolean,
    searchTerm: string,
    setRefresh: (value : boolean) => void,
}

// Creates element and logic to handle retrieving, displaying and updating a list of courses
const CourseList:FC<props> = ({ pageSize, searchTerm, refresh, setRefresh }) => {
    const [totalPages, setTotalPages] = useState(0);
    const [courseList, setCourseList] = useState<Course[]>();
    const [pageDisplayed, setPageDisplayed] = useState(0);
    const [courseDetails, setCourseDetails] = useState(0);
    const courseDetailsModal = useRef<HTMLDialogElement>(null);
    const [useSearchResults, setUseSearchResults] = useState(false);
    const [degrees, setDegrees] = useState<Degree[]>();
    const [professors, setProfessors] = useState<Staff[]>();

    const getPage = async (page: number, size: number) => {
        try {
            if (!useSearchResults) {
                const data : coursePage = await getPageOfData(page, size, DatabaseTypes.COURSE);
                setTotalPages(Math.ceil(data.totalElements/size))
                setCourseList(data.list)
                toastSuccess(`Course page retrieved`);
            }
        } catch (error: unknown) {
            console.log(error);
            toastError("Unexpected error while retrieving course page");
        }
    }

    const searchCourse = async (searchTerm: string) => {
        if (searchTerm == "") {
            setUseSearchResults(false);
            return;
        }

        try {
            const response : coursePage = await searchAndGetPageOfData(searchTerm, pageSize, DatabaseTypes.COURSE);
            if (response != null) {
                toastSuccess(`Search Completed`);
                setUseSearchResults(true);
                setTotalPages(response.totalElements)
                setCourseList(response.list)
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

    const createProfessorDictionary = async () => {
        const response:Staff[] = await getAllProfessors();
        const professorDict = {} // Treat as dictionary where professorDict[key] = value
        for (const prof of response) {
            professorDict[prof.id] = prof;
        }
        setProfessors(professorDict);
    }

    const toggleModal = (show: boolean): void => show ? courseDetailsModal.current?.showModal() : courseDetailsModal.current?.close();

    useEffect(() => {
        getAllDegrees();
        createProfessorDictionary();
    }, []);

    useEffect(() => {
        searchCourse(searchTerm);
    }, [searchTerm]);

    useEffect(() => {
        getPage(pageDisplayed, pageSize)
    }, [pageDisplayed, pageSize]);

    useEffect(() => {
        if (refresh) getPage(pageDisplayed, pageSize);
    }, [refresh]);

    if (courseList == null) {
        return;
    }

    return (
        <main className='main'>
            <dialog ref={courseDetailsModal} className="modal" id="studentDetailModal">
                {courseDetails != 0 && <CourseDetail id={courseDetails} toggleModal={toggleModal} setRefresh={setRefresh}/>}
            </dialog>

            {courseList?.length === 0 && <div>No Contacts</div>}

            <ul className='contact__list'>
                {courseList?.length > 0 && courseList.map((course: Course) => <CourseBox course={course} professors={professors} degrees={degrees} showDetails={setCourseDetails} toggleModal={toggleModal} key={course.id}/>)}
            </ul>

            {courseList.length > 0 && totalPages > 1 &&
                <div className='pagination'>
                    <a onClick={() => setPageDisplayed(0)} className={0 === pageDisplayed ? 'disabled' : ''}> &laquo; </a>
                    { courseList && paginationTabs(pageDisplayed, totalPages).map((page : number) =>
                        <a onClick={() => setPageDisplayed(page)} className={pageDisplayed === page ? 'active' : ''} key={page}>{page + 1}</a>)}
                    <a onClick={() => setPageDisplayed(totalPages-1)} className={totalPages === pageDisplayed + 1 ? 'disabled' : ''}> &raquo; </a>
                </div>}
        </main>
    )
}

export default CourseList