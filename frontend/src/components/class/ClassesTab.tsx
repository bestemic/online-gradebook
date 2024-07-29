import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import {useEffect, useState} from "react";
import classesService from "../../services/classes.ts";
import {IClassGroup} from "../../interfaces/ClassGroupInterface.ts";

const ClassesTab = () => {
    const axiosPrivate = useAxiosPrivate();
    const [classes, setClasses] = useState<IClassGroup[]>([]);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        classesService.getAll(axiosPrivate)
            .then(data => {
                setClasses(data);
                setError(null);
            })
            .catch(error => {
                setError(error.message);
            });
    }, [axiosPrivate]);

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
                    {classes.length > 0 ? (
                        <ul className="space-y-4 mt-6 mb-6 max-w-3xl w-full">
                            <li className="border-b-2">
                                <div className="flex items-center justify-between p-2 font-bold">
                                    <span className="w-2/5 text-center">Class Name</span>
                                    <span className="w-1/5 text-center">Classroom</span>
                                    <span className="w-2/5 text-center">Number of Students</span>
                                </div>
                            </li>
                            {classes.map(classGroup => (
                                <li key={classGroup.id} className="border rounded shadow">
                                    <div className="flex items-center justify-between p-2">
                                        <span className="w-2/5 text-center">{classGroup.name}</span>
                                        <span className="w-1/5 text-center">{classGroup.classroom}</span>
                                        <span className="w-2/5 text-center">{classGroup.students.length}</span>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <h1 className="text-xl font-bold text-center">No classes available</h1>
                    )}
                </div>
            )}
        </>
    );
};

export default ClassesTab;