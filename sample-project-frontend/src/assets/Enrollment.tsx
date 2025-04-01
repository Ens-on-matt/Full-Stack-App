interface EnrollmentInterface {
    student: string;
    course: string;
    status: string;
}


export default class Enrollment implements EnrollmentInterface {
    constructor(public student: string = '',
                public course: string = '',
                public status: string = '') {
        this.student = student;
        this.course = course;
        this.status = status;
    }

    resetArgs = () => {
        this.student = '';
        this.course = '';
        this.status = '';
    }
};