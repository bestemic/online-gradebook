import {IUserBasic} from "./UserBasicInterface.ts";

export interface IClassGroup {
    id: number;
    name: string;
    classroom: string;
    students: IUserBasic[];
}