import {useNavigate, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {IUser} from "../../interfaces/UserInterface.ts";
import userService from "../../services/users.ts";
import subjectService from "../../services/subjects.ts";
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import useAuth from "../../hooks/useAuth.ts";
import {jwtDecode} from "jwt-decode";
import JwtInterface from "../../interfaces/JwtInterface.ts";
import {ROLES} from "../../constants/roles.ts";
import {ISubject} from "../../interfaces/SubjectInterface.ts";
import {IRole} from "../../interfaces/RoleInterface.ts";
import RequireRole from "../wrapper/RequireRole.tsx";
import {Select} from "@mantine/core";

const UserProfile = () => {
    const {auth} = useAuth();
    const {id} = useParams();
    const axiosPrivate = useAxiosPrivate();
    const navigate = useNavigate();
    const [user, setUser] = useState<IUser | null>(null);
    const [userSubjects, setUserSubjects] = useState<ISubject[]>([]);
    const [isEditingSubjects, setIsEditingSubjects] = useState(false);
    const [availableSubjects, setAvailableSubjects] = useState<ISubject[]>([]);
    const [selectedSubjectId, setSelectedSubjectId] = useState<number | null>(null);
    const [userError, setUserError] = useState<string | null>(null);
    const [subjectError, setSubjectError] = useState<string | null>(null);

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

            userService.get(axiosPrivate, id)
                .then(data => {
                    setUser(data);
                    setUserError(null);
                    if (data.roles.flatMap((role: IRole) => role.name).includes(ROLES.Teacher)) {
                        userService.getUserSubjects(axiosPrivate, id)
                            .then(subjects => {
                                setUserSubjects(subjects)
                                setSubjectError(null)
                            })
                            .catch(error => setSubjectError(error.message));
                    }
                })
                .catch(error => {
                    setUserError(error.message);
                });
        } else {
            setUserError('Invalid user ID');
        }
    }, [auth.token, axiosPrivate, currentUserId, id, navigate]);

    useEffect(() => {
        if (isEditingSubjects && availableSubjects.length == 0) {
            subjectService.getAll(axiosPrivate)
                .then(subjects => {
                    setAvailableSubjects(subjects);
                    setSubjectError(null);
                })
                .catch(error => {
                    setSubjectError(error.message);
                });
        }
    }, [isEditingSubjects, axiosPrivate, availableSubjects]);

    const handleEditSubjects = () => {
        setSelectedSubjectId(null);
        setIsEditingSubjects(!isEditingSubjects);
    };

    const handleSaveSubject = () => {
        if (selectedSubjectId && user) {
            userService.assignSubjectToTeacher(axiosPrivate, String(user.id), selectedSubjectId)
                .then(() => {
                    const selectedSubject = availableSubjects.find(subject => subject.id === selectedSubjectId);
                    if (selectedSubject && !userSubjects.find(subject => subject.id === selectedSubject.id)) {
                        setUserSubjects([...userSubjects, selectedSubject]);
                    }
                    setIsEditingSubjects(false);
                    setSelectedSubjectId(null);
                })
                .catch(error => {
                    setSubjectError(error.message);
                });
        }
    };

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

                        {user.roles.flatMap(role => role.name).includes(ROLES.Teacher) && (
                            <div className="mt-8">
                                <h2 className="block font-bold">Subjects taught</h2>
                                {userSubjects.length > 0 ? (
                                    <ul className="space-y-2 mt-2">
                                        {userSubjects.map(subject => (
                                            <li key={subject.id} className="border rounded p-2">
                                                {subject.name}
                                            </li>
                                        ))}
                                    </ul>
                                ) : subjectError ? (
                                    <div className="text-red-500">{subjectError}</div>
                                ) : (
                                    <p>No subjects assigned.</p>
                                )}

                                {isEditingSubjects ? (
                                    <div className="mt-4">
                                        <Select
                                            data={availableSubjects.map(subject => ({
                                                value: String(subject.id),
                                                label: subject.name
                                            }))}
                                            placeholder="Select a subject"
                                            value={String(selectedSubjectId)}
                                            onChange={(_value, option) => setSelectedSubjectId(Number(option.value))}
                                        />
                                        <div className="mt-4 flex space-x-4">
                                            <button
                                                onClick={handleSaveSubject}
                                                className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600 transition duration-300"
                                            >
                                                Save
                                            </button>
                                            <button
                                                onClick={handleEditSubjects}
                                                className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 transition duration-300"
                                            >
                                                Cancel
                                            </button>
                                        </div>
                                    </div>
                                ) : (
                                    <RequireRole allowedRoles={[ROLES.Admin]}>
                                        <button
                                            onClick={handleEditSubjects}
                                            className="mt-4 bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition duration-300"
                                        >
                                            Add Subject
                                        </button>
                                    </RequireRole>
                                )}
                            </div>
                        )}

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