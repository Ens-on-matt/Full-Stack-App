import {FC, useEffect, useRef, useState} from 'react'
import Staff from "../../assets/Staff.tsx";
import StaffBox from "../box/StaffBox.tsx";
import {getPageOfData, searchAndGetPageOfData, staffPage} from "../../api/UniService.tsx";
import {toastError, toastSuccess} from "../../api/ToastService.tsx";
import DatabaseTypes from "../../assets/DatabaseTypes.tsx";
import {paginationTabs} from "./ListHelperFunctions.tsx";
import StaffDetail from "../contact_details/StaffDetail.tsx";

interface props {
    pageSize: number,
    searchTerm: string,
    refresh: boolean,
    setRefresh: (value : boolean) => void,
}

// Creates element and logic to handle retrieving, displaying and updating a list of staff
const StaffList:FC<props> = ({ pageSize, searchTerm, refresh, setRefresh }) => {
    const [totalPages, setTotalPages] = useState(0);
    const [staffList, setStaffList] = useState<Staff[]>();
    const [pageDisplayed, setPageDisplayed] = useState(0);
    const [staffDetails, setStaffDetails] = useState(0);
    const staffDetailsModal = useRef<HTMLDialogElement>(null);
    const [useSearchResults, setUseSearchResults] = useState(false);

    const getPage = async (page: number, size: number) => {
        try {
            if (!useSearchResults) {
                const data : staffPage = await getPageOfData(page, size, DatabaseTypes.STAFF);
                setTotalPages(Math.ceil(data.totalElements/size))
                setStaffList(data.list)
                toastSuccess(`Staff page retrieved`);
            }
        } catch (error: unknown) {
            console.log(error)
            toastError("Unexpected error while retrieving staff page");
        }
    }

    const searchStaff = async (searchTerm: string) => {
        if (searchTerm == "") {
            setUseSearchResults(false);
            return;
        }

        try {
            const response : staffPage = await searchAndGetPageOfData(searchTerm, pageSize, DatabaseTypes.STAFF);
            if (response != null) {
                toastSuccess(`Search Completed`);
                setUseSearchResults(true);
                setTotalPages(response.totalElements)
                setStaffList(response.list)
            }
        } catch (error: unknown) {
            console.log(error)
            toastError("Unexpected error while searching staff");
        }
    };

    const toggleModal = (show: boolean): void => show ? staffDetailsModal.current?.showModal() : staffDetailsModal.current?.close();

    useEffect(() => {
        searchStaff(searchTerm);
    }, [searchTerm]);

    useEffect(() => {
        getPage(pageDisplayed, pageSize);
    }, [pageDisplayed, pageSize]);

    useEffect(() => {
        if (refresh) getPage(pageDisplayed, pageSize);
    }, [refresh]);

    if (staffList == null) {
        return;
    }

    return (
        <main className='main'>
            <dialog ref={staffDetailsModal} className="modal" id="staffDetailModal">
                {staffDetails != 0 && <StaffDetail id={staffDetails} toggleModal={toggleModal} setRefresh={setRefresh}/>}
            </dialog>
            {staffList?.length === 0 && <div>No Contacts</div>}

            <ul className='contact__list'>
                {staffList?.length > 0 && staffList.map((staff: Staff) => <StaffBox staff={staff} showDetails={setStaffDetails} toggleModal={toggleModal} key={staff.id}/>)}
            </ul>

            {staffList.length > 0 && totalPages > 1 &&
                <div className='pagination'>
                    <a onClick={() => setPageDisplayed(0)} className={0 === pageDisplayed ? 'disabled' : ''}> &laquo; </a>
                    { staffList && paginationTabs(pageDisplayed, totalPages).map((page : number) =>
                        <a onClick={() => setPageDisplayed(page)} className={pageDisplayed === page ? 'active' : ''} key={page}>{page + 1}</a>)}
                    <a onClick={() => setPageDisplayed(totalPages-1)} className={totalPages === pageDisplayed + 1 ? 'disabled' : ''}> &raquo; </a>
                </div>}
        </main>
    )
}

export default StaffList