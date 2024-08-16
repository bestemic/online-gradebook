import {ISubject} from "./SubjectInterface.ts";
import {IUserBasic} from "./UserBasicInterface.ts";
import {IClassGroupBasic} from "./ClassGroupBasicInterface.ts";

export interface IClassSubject {
    id: number;
    classGroup: IClassGroupBasic;
    subject: ISubject;
    teacher: IUserBasic;
}