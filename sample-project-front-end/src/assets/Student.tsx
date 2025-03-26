interface StudentInterface {
    id: number;
    name: string;
    email: string;
    phone_number: string;
    degree_id: string;
}


class Student implements StudentInterface {
    label: string;
    value: string;

    constructor(public id : number = -1,
                public name: string = '',
                public email: string = '',
                public phone_number: string = '',
                public degree_id: string = '') {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
        this.degree_id = degree_id;
        this.label = name;
        this.value = `${id}`;
    }

    resetArgs = () => {
        this.id = -1;
        this.name = '';
        this.email = '';
        this.phone_number = '';
        this.degree_id = '';
    }
}

export default Student;