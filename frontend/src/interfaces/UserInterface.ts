import {IRole} from "./RoleInterface.ts";

export interface IUser {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    phoneNumber: string;
    birth: string;
    roles: IRole[];
}