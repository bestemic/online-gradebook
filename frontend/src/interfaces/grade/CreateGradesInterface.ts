import {ICreateGrade} from "./CreateGradeInterface.ts";

export interface ICreateGrades {
    subjectId: number;
    name: string;
    grades: ICreateGrade[];
}