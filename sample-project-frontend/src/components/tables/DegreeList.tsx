import {FC, useEffect, useRef, useState} from 'react'
import {getPageOfData, degreePage, searchAndGetPageOfData} from "../../api/UniService.tsx";
import {toastError, toastSuccess} from "../../api/ToastService.tsx";
import DatabaseTypes from "../../assets/DatabaseTypes.tsx";
import Degree from "../../assets/Degree.tsx";
import DegreeBox from "../box/DegreeBox.tsx";
import {paginationTabs} from "./ListHelperFunctions.tsx";
import DegreeDetail from "../contact_details/DegreeDetail.tsx";

interface props {
    pageSize: number,
    refresh: boolean,
    searchTerm: string,
    setRefresh: (value : boolean) => void,
}

// Creates element and logic to handle retrieving, displaying and updating a list of degrees
const DegreeList:FC<props> = ({ pageSize, searchTerm, refresh, setRefresh }) => {
    const [totalPages, setTotalPages] = useState(0);
    const [degreeList, setDegreeList] = useState<Degree[]>();
    const [pageDisplayed, setPageDisplayed] = useState(0);
    const [degreeDetails, setDegreeDetails] = useState(0);
    const degreeDetailsModal = useRef<HTMLDialogElement>(null);
    const [useSearchResults, setUseSearchResults] = useState(false);

    const getPage = async (page: number, size: number) => {
        try {
            if (!useSearchResults) {
                const data : degreePage = await getPageOfData(page, size, DatabaseTypes.DEGREE);
                setTotalPages(Math.ceil(data.totalElements/size))
                setDegreeList(data.list)
                toastSuccess(`Degree page retrieved`);
            }
        } catch (error: unknown) {
            console.log(error)
            toastError("Unexpected error while retrieving degree page");
        }
    }

    const searchDegree = async (searchTerm: string) => {
        if (searchTerm == "") {
            setUseSearchResults(false);
            return;
        }

        try {
            const response : degreePage = await searchAndGetPageOfData(searchTerm, pageSize, DatabaseTypes.DEGREE);
            if (response != null) {
                toastSuccess(`Search Completed`);
                setUseSearchResults(true);
                setTotalPages(response.totalElements)
                setDegreeList(response.list)
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

    const toggleModal = (show: boolean): void => show ? degreeDetailsModal.current?.showModal() : degreeDetailsModal.current?.close();

    useEffect(() => {
        searchDegree(searchTerm);
    }, [searchTerm]);

    useEffect(() => {
        getPage(pageDisplayed, pageSize)
    }, [pageDisplayed, pageSize]);

    useEffect(() => {
        if (refresh) getPage(pageDisplayed, pageSize);
    }, [refresh]);

    if (degreeList == null) {
        return;
    }

    return (
        <main className='main'>
            <dialog ref={degreeDetailsModal} className="modal" id="degreeDetailModal">
                {degreeDetails != 0 && <DegreeDetail id={degreeDetails} toggleModal={toggleModal} setRefresh={setRefresh}/>}
            </dialog>

            {degreeList?.length === 0 && <div>No Contacts</div>}

            <ul className='contact__list'>
                {degreeList?.length > 0 && degreeList.map((degree: Degree) => <DegreeBox degree={degree} showDetails={setDegreeDetails} toggleModal={toggleModal} key={degree.id}/>)}
            </ul>

            {degreeList.length > 0 && totalPages > 1 &&
                <div className='pagination'>
                    <a onClick={() => setPageDisplayed(0)} className={0 === pageDisplayed ? 'disabled' : ''}> &laquo; </a>
                    { degreeList && paginationTabs(pageDisplayed, totalPages).map((page : number) =>
                        <a onClick={() => setPageDisplayed(page)} className={pageDisplayed === page ? 'active' : ''} key={page}>{page + 1}</a>)}
                    <a onClick={() => setPageDisplayed(totalPages-1)} className={totalPages === pageDisplayed + 1 ? 'disabled' : ''}> &raquo; </a>
                </div>}
        </main>
    )
}

export default DegreeList