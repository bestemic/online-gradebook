import {IUserBasic} from "./UserBasicInterface.ts";

export interface ISubject {
    id: number;
    name: string;
    teachers: IUserBasic[];
}