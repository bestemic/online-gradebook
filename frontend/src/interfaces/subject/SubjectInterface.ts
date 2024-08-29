import {IUserBasic} from "../user/UserBasicInterface.ts";
import {ISchoolClassBasic} from "../school_class/SchoolClassBasicInterface.ts";

export interface ISubject {
    id: number;
    name: string;
    schoolClass: ISchoolClassBasic;
    teacher: IUserBasic;
}