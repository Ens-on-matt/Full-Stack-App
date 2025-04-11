import {useEffect, useRef} from "react";

// Only lets keyboard event occur if the character pressed is either: 0 - 9, backspace, delete, control, tab, arrow left or arrow right
export const numberTextOnKeydown = (event : React.KeyboardEvent<HTMLInputElement>) => {
    // Regex expression adapted from https://stackoverflow.com/questions/43687964/only-numbers-input-number-in-react
    // Regex checks if number not: a digit OR 'Backspace' OR 'Delete' OR ...
    if (!/[0-9]|Backspace|Delete|Control|ArrowLeft|ArrowRight|Tab/.test(event.key)) {
        event.preventDefault();
    }
}

// Only lets keyboard event occur if the character pressed is either: 0 - 9, round brackets, whitespace, backspace, delete, control, tab, arrow left or arrow right
export const phoneNumTextOnKeydown = (event : React.KeyboardEvent<HTMLInputElement>) => {
    // Regex expression adapted from https://stackoverflow.com/questions/43687964/only-numbers-input-number-in-react
    // Like numberTextOnKeydown above, but also allows round brackets and whitespace ('(', ')', ' ')
    if (!/[0-9() ]|Backspace|Delete|Control|ArrowLeft|ArrowRight|Tab/.test(event.key)) {
        event.preventDefault();
    }
}

// Allows for a function "callback" to be called when the mouse clicks outside of the referenced element.
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
