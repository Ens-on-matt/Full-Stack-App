import {FC, useEffect} from "react";

interface props {
    toggleModal: (show: boolean) => void;
}

const BlankForm:FC<props> = ({toggleModal}) => {
    useEffect(() => {
        toggleModal(false);
    }, [toggleModal]);
    return (<></>)
}

export default BlankForm;