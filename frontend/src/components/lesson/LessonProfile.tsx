import {useParams} from "react-router-dom";
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import {useEffect, useState} from "react";
import {ILesson} from "../../interfaces/lesson/LessonInterface.ts";
import lessonsService from "../../services/lessons.ts";
import AttendancesTab from "../attendance/AttendancesTab.tsx";

const LessonProfile = () => {
    const {id} = useParams();
    const axiosPrivate = useAxiosPrivate();
    const [lesson, setLesson] = useState<ILesson | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (id) {
            lessonsService.getById(axiosPrivate, id)
                .then(data => {
                    setLesson(data);
                    setError(null);
                })
                .catch(error => {
                    setError(error.message);
                });
        }
    }, [id, axiosPrivate]);

    return (
        <div className="h-full flex items-center justify-center">
            <div className="bg-white p-8 rounded-b shadow-2xl max-w-4xl w-full">
                {lesson ? (
                    <>
                        <div>
                            <h1 className="text-3xl font-bold mb-2 text-center">{lesson.title}</h1>
                            <p className="text-xl text-center">
                                {lesson.description}
                            </p>
                            <p className="text-xl text-center">
                                {new Date(lesson.date).toLocaleDateString()}
                            </p>
                        </div>

                        <AttendancesTab/>
                    </>
                ) : !error && (
                    <div>Loading...</div>
                )}

                {error && (<div className="text-red-500">{error}</div>)}
            </div>
        </div>
    );
};

export default LessonProfile;