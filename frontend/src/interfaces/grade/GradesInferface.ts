import {IGrade} from "./GradeInterface.ts";
import {ISubject} from "../subject/SubjectInterface.ts";

export interface IGrades {
    subject: ISubject;
    name: string;
    assignedTime: string;
    grades: IGrade[];
}