import '@mantine/core/styles.css';
import 'mantine-react-table/styles.css';
import {useEffect, useState} from 'react';
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import {ISubject} from "../../interfaces/SubjectInterface.ts";
import {IconSquareRoundedChevronDown} from "@tabler/icons-react";
import subjectService from "../../services/subjects.ts";


const SubjectsTab = () => {
    const axiosPrivate = useAxiosPrivate();
    const [subjects, setSubjects] = useState<ISubject[]>([]);
    const [expandedSubjectIds, setExpandedSubjectIds] = useState<number[]>([]);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        subjectService.getAll(axiosPrivate)
            .then(data => {
                setSubjects(data);
                setError(null);
            })
            .catch(error => {
                setError(error.message);
            });
    }, [axiosPrivate]);

    const handleToggle = (id: number) => {
        setExpandedSubjectIds(prevIds =>
            prevIds.includes(id) ? prevIds.filter(expandedId => expandedId !== id) : [...prevIds, id]
        );
    };

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
                    <ul className="space-y-4 mt-6 mb-6 max-w-4xl w-full">
                        {subjects.length > 0 ? (
                            subjects.map(subject => (
                                <li key={subject.id} className="border rounded shadow">
                                    <div
                                        onClick={() => subject.teachers.length > 0 && handleToggle(subject.id)}
                                        className={`flex items-center justify-between p-2 ${subject.teachers.length > 0 ? 'cursor-pointer hover:bg-gray-100' : 'cursor-default'}`}
                                    >
                                        <span
                                            className={`${expandedSubjectIds.includes(subject.id) ? 'font-bold' : ''}`}
                                        >{subject.name}</span>
                                        {subject.teachers.length > 0 && (
                                            <IconSquareRoundedChevronDown
                                                size={24}
                                                className={`transition-transform ${expandedSubjectIds.includes(subject.id) ? 'rotate-180' : ''}`}
                                            />
                                        )}
                                    </div>
                                    {expandedSubjectIds.includes(subject.id) && subject.teachers.length > 0 && (
                                        <ul className="pl-8 pr-10">
                                            {subject.teachers.map(teacher => (
                                                <li key={teacher.id} className="flex items-center border-t py-2">
                                                    <div className="flex-1 flex items-center">
                                                        <span>{teacher.firstName} {teacher.lastName}</span>
                                                    </div>
                                                    <span>{teacher.email}</span>
                                                </li>
                                            ))}
                                        </ul>

                                    )}
                                </li>
                            ))
                        ) : (
                            <h1 className="text-xl font-bold text-center">No subjects available</h1>
                        )}
                    </ul>
                </div>
            )}
        </>
    );
};

export default SubjectsTab;