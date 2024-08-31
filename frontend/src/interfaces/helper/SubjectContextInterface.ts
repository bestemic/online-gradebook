import {ISubject} from "../subject/SubjectInterface.ts";
import {ISchoolClass} from "../school_class/SchoolClassInterface.ts";

export interface ISubjectContext {
    subject: ISubject | null;
    setSubject: (subject: ISubject) => void;
    schoolClass: ISchoolClass | null;
    setSchoolClass: (schoolClass: ISchoolClass) => void;
}

export const subjectContextDefaults: ISubjectContext = {
    subject: null,
    setSubject: () => {},
    schoolClass: null,
    setSchoolClass: () => {}
};