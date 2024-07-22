import {Navigate, Outlet, useLocation} from "react-router-dom";
import useAuth from "../../hooks/useAuth.ts";
import {jwtDecode} from "jwt-decode";
import JwtInterface from "../../interfaces/JwtInterface.ts";

type Props = {
    allowedRoles: string[];
};

const RequireAuth = ({allowedRoles}: Props) => {
    const {auth} = useAuth();
    const location = useLocation();

    const decoded: JwtInterface | undefined = auth?.token ? jwtDecode(auth.token) : undefined;
    const roles: string[] = decoded?.roles.split(',') || [];

    return (
        roles.find(role => allowedRoles?.includes(role))
            ? <Outlet/>
            : auth?.token
                ? <Navigate to="/unauthorized" state={{from: location}} replace/>
                : <Navigate to="/login" state={{from: location}} replace/>
    );
};

export default RequireAuth;