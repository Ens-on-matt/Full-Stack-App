import {toast, ToastOptions} from "react-toastify";

const toastConfig: ToastOptions = {
    position: "top-right",
    autoClose: 3000,
    hideProgressBar: false,
    closeOnClick: true,
    pauseOnHover: true,
    draggable: false,
    progress: undefined,
    theme: "light"
}

export function toastInfo(message: string) {
    toast.info(message, toastConfig);
}

export function toastSuccess(message: string) {
    toast.success(message, toastConfig);
}

export function toastWarning(message: string) {
    toast.warning(message, toastConfig);
}

export function toastError(message: string) {
    toast.error(message, toastConfig);
}