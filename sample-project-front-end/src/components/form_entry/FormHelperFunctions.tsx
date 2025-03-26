import {useEffect, useRef} from "react";

export const numberTextOnKeydown = (event : React.KeyboardEvent<HTMLInputElement>) => {
    // Regex expression adapted from https://stackoverflow.com/questions/43687964/only-numbers-input-number-in-react
    // Regex checks if number not: a digit OR 'Backspace' OR 'Delete' OR ...
    if (!/[0-9]|Backspace|Delete|Control|ArrowLeft|ArrowRight|Tab/.test(event.key)) {
        event.preventDefault();
    }
}

export const phoneNumTextOnKeydown = (event : React.KeyboardEvent<HTMLInputElement>) => {
    // Regex expression adapted from https://stackoverflow.com/questions/43687964/only-numbers-input-number-in-react
    // Like numberTextOnKeydown above, but also allows round brackets and whitespace ('(', ')', ' ')
    if (!/[0-9() ]|Backspace|Delete|Control|ArrowLeft|ArrowRight|Tab/.test(event.key)) {
        event.preventDefault();
    }
}

export const useOutsideClick = (callback: () => void) => {
    const ref = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent | TouchEvent) => {
            if (ref.current && !ref.current.contains(event.target as Node)) {
                callback();
            }
        };

        document.addEventListener('mouseup', handleClickOutside);
        document.addEventListener('touchend', handleClickOutside);


        return () => {
            document.removeEventListener('mouseup', handleClickOutside);
            document.removeEventListener('touchend', handleClickOutside);
        };
    }, [callback]);

    return ref;
};
