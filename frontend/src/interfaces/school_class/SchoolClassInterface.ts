import {IUserBasic} from "../user/UserBasicInterface.ts";

export interface ISchoolClass {
    id: number;
    name: string;
    classroom: string;
    students: IUserBasic[];
}