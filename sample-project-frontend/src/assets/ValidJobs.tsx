const ValidJobs = Object.freeze({
    STUDENT_SUPPORT: 'Student Support',
    HOSPITALITY: 'Hospitality',
    CLEANING: 'Cleaner',
    PROFESSOR: 'Professor',
    ADMINISTRATION: 'Admin',
    IT: 'IT'
});

export default ValidJobs;

export class option {
    label: string;
    value: string;
    constructor(public job: string) {
        this.label = job;
        this.value = job;
    }
}

export const JobOptions = [
    new option(ValidJobs.STUDENT_SUPPORT),
    new option(ValidJobs.HOSPITALITY),
    new option(ValidJobs.CLEANING),
    new option(ValidJobs.PROFESSOR),
    new option(ValidJobs.ADMINISTRATION),
    new option(ValidJobs.IT)
]

