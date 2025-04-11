import {FC, useEffect} from "react";

interface props {
    toggleModal: (show: boolean) => void;
}

// Blank form to use as wildcard. Should not be accessible (header should prevent users from openning the form).
// Closes itself when opened in case.
const BlankForm:FC<props> = ({toggleModal}) => {
    useEffect(() => {
        toggleModal(false);
    }, [toggleModal]);
    return (<></>)
}

export default BlankForm;