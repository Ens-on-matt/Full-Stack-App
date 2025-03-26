interface CourseInterface {
    id: number;
    name: string;
    professor_id: string;
    degree_id: string;
}


export default class Course implements CourseInterface {
    label: string;
    value: string;

    constructor(public id : number = -1,
                public name: string = '',
                public professor_id: string = '',
                public degree_id: string = '') {
        this.id = id;
        this.name = name;
        this.professor_id = professor_id;
        this.degree_id = degree_id;
        this.label = name;
        this.value = `${id}`;
    }

    resetArgs = () => {
        this.id = -1;
        this.name = '';
        this.professor_id = '';
        this.degree_id = '';
    }
};