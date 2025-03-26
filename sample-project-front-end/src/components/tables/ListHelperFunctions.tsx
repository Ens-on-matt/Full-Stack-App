export const paginationTabs = (currentPage: number, totalPages: number) => {
    const lengthOfPagination = 4;
    const minPageNumber = Math.max(currentPage - lengthOfPagination, 0);
    const maxPageNumber = Math.min(totalPages, minPageNumber + 2*lengthOfPagination+1);
    return [...Array(maxPageNumber - minPageNumber).keys()].map((page : number)=> page+minPageNumber);
}