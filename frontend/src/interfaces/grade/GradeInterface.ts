import {IUserBasic} from "../user/UserBasicInterface.ts";

export interface IGrade {
    id: number;
    student: IUserBasic;
    grade: string;
}