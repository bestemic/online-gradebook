import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import {useEffect, useState} from "react";
import lessonsService from "../../services/lessons.ts";
import {ILesson} from "../../interfaces/lesson/LessonInterface.ts";
import {useNavigate, useParams} from "react-router-dom";
import useAuth from "../../hooks/useAuth.ts";
import JwtInterface from "../../interfaces/helper/JwtInterface.ts";
import {jwtDecode} from "jwt-decode";
import useSubject from "../../hooks/useSubject.ts";
import RequireRole from "../wrapper/RequireRole.tsx";
import {ROLES} from "../../constants/roles.ts";
import AddLesson from "./AddLesson.tsx";

const LessonsTab = () => {
    const {id} = useParams();
    const {auth} = useAuth();
    const {subject} = useSubject();
    const axiosPrivate = useAxiosPrivate();
    const [lessons, setLessons] = useState<ILesson[]>([]);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    const decoded: JwtInterface | undefined = auth?.token ? jwtDecode(auth.token) : undefined;
    const currentUserId: number = decoded?.id || 0;

    useEffect(() => {
        if (id) {
            lessonsService.getAll(axiosPrivate, parseInt(id))
                .then(data => {
                    setLessons(data);
                    setError(null);
                })
                .catch(error => {
                    setError(error.message);
                });
        }
    }, [axiosPrivate, id]);

    const handleLessonClick = (lessonId: number) => {
        navigate(`/lessons/${lessonId}`);
    }

    return (
        <>
            {error ? (
                <div className="h-full flex items-center justify-center">
                    <div className="text-center">
                        <h2 className="text-3xl font-semibold text-red-500">{error}</h2>
                    </div>
                </div>
            ) : (
                <div className="h-full flex-col flex items-center justify-center">

                    {currentUserId === subject?.teacher.id && (
                        <RequireRole allowedRoles={[ROLES.Teacher]}>
                            <div className="">
                                <AddLesson/>
                            </div>
                        </RequireRole>
                    )}

                    {lessons.length > 0 ? (
                        <ul className="space-y-4 mt-6 mb-6 max-w-3xl w-full">
                            <li className="border-b-2">
                                <div className="flex items-center justify-between p-2 font-bold">
                                    <span className="w-2/3 text-center">Lesson Title</span>
                                    <span className="w-1/3 text-center">Date</span>
                                </div>
                            </li>
                            {lessons.map(lesson => (
                                <li key={lesson.id} className="border rounded shadow cursor-pointer"
                                    onClick={() => handleLessonClick(lesson.id)}>
                                    <div className="flex items-center justify-between p-2">
                                        <span className="w-2/3 text-center">{lesson.title}</span>
                                        <span
                                            className="w-1/3 text-center">{new Date(lesson.date).toLocaleDateString()}
                                        </span>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <h1 className="text-xl font-bold text-center">No lessons available</h1>
                    )}
                </div>
            )}
        </>
    );
};

export default LessonsTab;