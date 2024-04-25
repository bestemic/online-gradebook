import React, {createContext, useState} from "react";
import {authContextDefaults, IAuthContext} from "../interfaces/AuthContextInterface.ts";

type Props = {
    children: React.ReactNode;
};

const AuthContext = createContext<IAuthContext>(authContextDefaults);

export const AuthProvider = ({children}: Props) => {
    const [auth, setAuth] = useState(authContextDefaults.auth);

    return (
        <AuthContext.Provider value={{auth, setAuth}}>
            {children}
        </AuthContext.Provider>
    );
};

export default AuthContext;