import {useNavigate, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {IUser} from "../interfaces/UserInterface.ts";
import userService from "../services/users.ts";
import useAxiosPrivate from "../hooks/useAxiosPrivate.ts";
import useAuth from "../hooks/useAuth.ts";
import {jwtDecode} from "jwt-decode";
import JwtInterface from "../interfaces/JwtInterface.ts";
import {ROLES} from "../constants/roles.ts";

const UserProfile = () => {
    const {auth} = useAuth();
    const {id} = useParams();
    const axiosPrivate = useAxiosPrivate();
    const navigate = useNavigate();
    const [user, setUser] = useState<IUser | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const decoded: JwtInterface | undefined = auth?.token ? jwtDecode(auth.token) : undefined;
        const currentUserId = decoded?.id;
        const roles: string[] = decoded?.roles.split(',') || [];
        const isAdmin = roles.includes(ROLES.Admin);

        if (id) {
            if (!isAdmin) {
                if (Number(id) !== currentUserId) {
                    navigate("/unauthorized");
                    return;
                }
            }

            userService.get(axiosPrivate, id)
                .then(data => {
                    setUser(data);
                    setError(null);
                })
                .catch(error => {
                    setError(error.message);
                });
        } else {
            setError('Invalid user ID');
        }
    }, [auth.token, axiosPrivate, id, navigate]);

    const formatRoleName = (roleName: string) => {
        const parts = roleName.split('_');
        const formatted = parts.length > 1 ? parts[1].toLowerCase() : roleName.toLowerCase();
        return formatted.charAt(0).toUpperCase() + formatted.slice(1);
    };

    return (
        <div className="h-full flex items-center justify-center">
            <div className="bg-white p-8 rounded-b shadow-2xl max-w-4xl w-full">
                {user ? (
                    <div className="mt-4">
                        <div className="flex mb-4 space-x-6">
                            <div className="w-1/2">
                                <label className="block font-bold mb-1">First Name:</label>
                                <div className="border border-gray-300 rounded px-3 py-2">
                                    {user.firstName}
                                </div>
                            </div>
                            <div className="w-1/2">
                                <label className="block font-bold mb-1">Last Name:</label>
                                <div className="border border-gray-300 rounded px-3 py-2">
                                    {user.lastName}
                                </div>
                            </div>
                        </div>
                        <div className="mb-4">
                            <label className="block font-bold mb-1">Email:</label>
                            <div className="border border-gray-300 rounded px-3 py-2">
                                {user.email}
                            </div>
                        </div>
                        <div className="flex mb-4 space-x-6">
                            <div className="w-1/2">
                                <label className="block font-bold mb-1">Birth Date:</label>
                                <div className="border border-gray-300 rounded px-3 py-2">
                                    {user.birth || "not provided"}
                                </div>
                            </div>
                            <div className="w-1/2">
                                <label className="block font-bold mb-1">Phone Number:</label>
                                <div className="border border-gray-300 rounded px-3 py-2">
                                    {user.phoneNumber || "not provided"}
                                </div>
                            </div>
                        </div>
                        <div className="mb-4">
                            <label className="block font-bold mb-1">Roles:</label>
                            <div className="border border-gray-300 rounded px-3 py-2">
                                {user.roles.flatMap(role => formatRoleName(role.name)).join(", ")}
                            </div>
                        </div>
                    </div>
                ) : error ? (
                    <div className="text-red-500">{error}</div>
                ) : (
                    <div>Loading...</div>
                )}
            </div>
        </div>
    );
};

export default UserProfile;