interface EnrollmentInterface {
    id: number;
    student: string;
    course: string;
    status: string;
}


export default class Enrollment implements EnrollmentInterface {
    constructor(public id : number = -1,
                public student: string = '',
                public course: string = '',
                public status: string = '') {
        this.id = id;
        this.student = student;
        this.course = course;
        this.status = status;
    }

    resetArgs = () => {
        this.id = -1;
        this.student = '';
        this.course = '';
        this.status = '';
    }
};