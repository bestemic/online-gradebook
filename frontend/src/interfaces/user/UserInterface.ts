import {IRole} from "../role/RoleInterface.ts";

export interface IUser {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    phoneNumber: string;
    birth: string;
    classId: number;
    roles: IRole[];
}