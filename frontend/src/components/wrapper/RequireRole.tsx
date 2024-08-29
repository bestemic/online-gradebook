import useAuth from "../../hooks/useAuth.ts";
import JwtInterface from "../../interfaces/helper/JwtInterface.ts";
import {jwtDecode} from "jwt-decode";
import React from "react";

type Props = {
    allowedRoles: string[];
    children: React.ReactNode;
};

const RequireRole = ({allowedRoles, children}: Props) => {
    const {auth} = useAuth();

    const decoded: JwtInterface | undefined = auth?.token ? jwtDecode(auth.token) : undefined;
    const roles: string[] = decoded?.roles.split(',') || [];
    const isAllowed = roles.some(role => allowedRoles.includes(role));

    return (
        isAllowed ? <>{children}</> : null
    );
};

export default RequireRole;