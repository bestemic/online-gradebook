import {useNavigate, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {IUser} from "../../interfaces/user/UserInterface.ts";
import userService from "../../services/users.ts";
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import useAuth from "../../hooks/useAuth.ts";
import {jwtDecode} from "jwt-decode";
import JwtInterface from "../../interfaces/helper/JwtInterface.ts";
import {ROLES} from "../../constants/roles.ts";

const UserProfile = () => {
    const {auth} = useAuth();
    const {id} = useParams();
    const axiosPrivate = useAxiosPrivate();
    const navigate = useNavigate();
    const [user, setUser] = useState<IUser | null>(null);
    const [userError, setUserError] = useState<string | null>(null);

    const decoded: JwtInterface | undefined = auth?.token ? jwtDecode(auth.token) : undefined;
    const currentUserId = decoded?.id;

    useEffect(() => {
        if (id) {
            const decoded: JwtInterface | undefined = auth?.token ? jwtDecode(auth.token) : undefined;
            const currentUserId = decoded?.id;
            const roles: string[] = decoded?.roles.split(',') || [];
            const isAdmin = roles.includes(ROLES.Admin);

            if (!isAdmin) {
                if (Number(id) !== currentUserId) {
                    navigate("/unauthorized");
                    return;
                }
            }

            userService.getById(axiosPrivate, id)
                .then(data => {
                    setUser(data);
                    setUserError(null);
                })
                .catch(error => {
                    setUserError(error.message);
                });
        } else {
            setUserError('Invalid user ID');
        }
    }, [auth.token, axiosPrivate, currentUserId, id, navigate]);

    const formatRoleName = (roleName: string) => {
        const parts = roleName.split('_');
        const formatted = parts.length > 1 ? parts[1].toLowerCase() : roleName.toLowerCase();
        return formatted.charAt(0).toUpperCase() + formatted.slice(1);
    };

    const handlePasswordChange = () => {
        navigate("/change-password", {state: {from: `/users/${id}`}});
    };

    return (
        <div className="h-full flex items-center justify-center">
            <div className="bg-white p-8 rounded-b shadow-2xl max-w-4xl w-full">
                {user ? (
                    <>
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

                            {Number(id) === currentUserId && (
                                <div className="mt-8">
                                    <button
                                        onClick={handlePasswordChange}
                                        className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition duration-300"
                                    >
                                        Change Password
                                    </button>
                                </div>
                            )}
                        </div>
                    </>
                ) : !userError && (
                    <div>Loading...</div>
                )}

                {userError && (<div className="text-red-500">{userError}</div>)}
            </div>
        </div>
    );
};

export default UserProfile;