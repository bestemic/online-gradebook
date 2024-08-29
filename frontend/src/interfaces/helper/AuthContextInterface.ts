import {IAuthData} from "./AuthDataInterface.ts";
import {Dispatch, SetStateAction} from "react";

export interface IAuthContext {
    auth: IAuthData;
    setAuth: Dispatch<SetStateAction<IAuthData>>;
}

export const authContextDefaults: IAuthContext = {
    auth: {
        token: null,
        email: null
    },
    setAuth: () => {}
};
