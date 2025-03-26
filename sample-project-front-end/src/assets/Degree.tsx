interface DegreeInterface {
    id: number;
    name: string;
}


export default class Degree implements DegreeInterface {
    label: string;
    value: string;

    constructor(public id : number = -1,
                public name: string = '') {
        this.id = id;
        this.name = name;
        this.label = name;
        this.value = `${id}`;
    }

    resetArgs = () => {
        this.id = -1;
        this.name = '';
    }
};