import {IUserBasic} from "../user/UserBasicInterface.ts";

export interface IAttendance {
    id: number;
    student: IUserBasic;
    present: boolean;
}