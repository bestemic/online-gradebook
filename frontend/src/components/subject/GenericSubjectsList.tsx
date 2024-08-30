import React, {useEffect, useState} from "react";
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import {NavigateFunction, useNavigate} from "react-router-dom";
import {jwtDecode} from "jwt-decode";
import useAuth from "../../hooks/useAuth.ts";
import {ISubject} from "../../interfaces/subject/SubjectInterface.ts";
import JwtInterface from "../../interfaces/helper/JwtInterface.ts";
import {AxiosInstance} from "axios";

interface GenericSubjectsProps {
    fetchSubjects: (axiosPrivate: AxiosInstance, userId: number, classId?: number) => Promise<ISubject[]>;
    handleItemClick: (navigate: NavigateFunction, id: number) => void;
    renderHeaders: () => React.ReactNode;
    renderRow: (subject: ISubject) => React.ReactNode;
}

const GenericSubjects: React.FC<GenericSubjectsProps> = ({
                                                             fetchSubjects,
                                                             handleItemClick,
                                                             renderHeaders,
                                                             renderRow
                                                         }) => {
    const axiosPrivate = useAxiosPrivate();
    const {auth} = useAuth();
    const [subjects, setSubjects] = useState<ISubject[]>([]);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const decoded: JwtInterface | undefined = auth?.token ? jwtDecode(auth.token) : undefined;
        const userId: number = decoded?.id || 0;

        fetchSubjects(axiosPrivate, userId)
            .then((data: ISubject[]) => {
                const sortedSubjects = data.sort((a, b) => a.name.localeCompare(b.name));
                setSubjects(sortedSubjects);
                setError(null);
            })
            .catch((error) => {
                setError(error.message);
            });
    }, [auth.token, axiosPrivate, fetchSubjects]);

    return (
        <>
            {error ? (
                <div className="h-full flex items-center justify-center">
                    <div className="text-center">
                        <h2 className="text-3xl font-semibold text-red-500">{error}</h2>
                    </div>
                </div>
            ) : (
                <div className="h-full flex items-center justify-center">
                    {subjects.length > 0 ? (
                        <ul className="space-y-4 mt-6 mb-6 max-w-4xl w-full">
                            <li className="border-b-2">{renderHeaders()}</li>
                            {subjects.map(subject => (
                                <li key={subject.id} className="border rounded shadow cursor-pointer"
                                    onClick={() => handleItemClick(navigate, subject.id)}>
                                    {renderRow(subject)}
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <h1 className="text-xl font-bold text-center">No subjects available</h1>
                    )}
                </div>
            )}
        </>
    );
};

export default GenericSubjects;