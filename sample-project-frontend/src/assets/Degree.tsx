import INVALID_ID from "./INVALID_ID.tsx";

interface DegreeInterface {
    id: number;
    name: string;
}


export default class Degree implements DegreeInterface {
    label: string;
    value: string;

    constructor(public id : number = INVALID_ID,
                public name: string = '') {
        this.id = id;
        this.name = name;
        this.label = name;
        this.value = `${id}`;
    }

    resetArgs = () => {
        this.id = INVALID_ID;
        this.name = '';
    }
};