import INVALID_ID from "./INVALID_ID.tsx";

interface StaffInterface {
    id: number;
    name: string;
    email: string;
    phone_number: string;
    salary: string;
    job: string;
}


class Staff implements StaffInterface {
    label: string;
    value: string;

    constructor(public id : number = INVALID_ID,
                public name: string = '',
                public email: string = '',
                public phone_number: string = '',
                public salary: string = '',
                public job: string = '') {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
        this.salary = salary;
        this.job = job;
        this.label = name;
        this.value = `${id}`;
    }

    resetArgs = () => {
        this.id = INVALID_ID;
        this.name = '';
        this.email = '';
        this.phone_number = '';
        this.salary = '';
        this.job = '';
    }
};

export default Staff;