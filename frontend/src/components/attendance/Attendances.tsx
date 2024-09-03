import useSubject from "../../hooks/useSubject.ts";
import {useEffect, useState} from "react";
import {ICreateAttendance} from "../../interfaces/attendance/CreateAttendanceInterface.ts";
import {ICreateAttendancesLesson} from "../../interfaces/attendance/CreateAttendancesLessonInterface.ts";
import {useParams} from "react-router-dom";
import attendanceService from "../../services/attendances.ts";
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import {IAttendance} from "../../interfaces/attendance/AttendanceInterface.ts";
import {IAttendancesLesson} from "../../interfaces/attendance/AttendancesLessonInferface.ts";
import JwtInterface from "../../interfaces/helper/JwtInterface.ts";
import {jwtDecode} from "jwt-decode";
import useAuth from "../../hooks/useAuth.ts";

const Attendances = () => {
    const {id} = useParams();
    const {auth} = useAuth();
    const {subject, schoolClass} = useSubject();
    const axiosPrivate = useAxiosPrivate();
    const [attendances, setAttendances] = useState<IAttendance[]>([]);
    const [studentAttendance, setStudentAttendance] = useState<IAttendance>(null);
    const [attendancesCreate, setAttendancesCreate] = useState<ICreateAttendance[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const decoded: JwtInterface | undefined = auth?.token ? jwtDecode(auth.token) : undefined;
    const currentUserId: number = decoded?.id || 0;

    useEffect(() => {
            if (currentUserId === subject?.teacher.id) {
                if (schoolClass && schoolClass.students) {
                    const initialAttendances = schoolClass.students.map(student => ({
                        studentId: student.id,
                        present: false,
                    }));
                    setAttendancesCreate(initialAttendances);
                }
            }
        }, [currentUserId, schoolClass, subject?.teacher.id]
    );

    useEffect(() => {
        if (currentUserId === subject?.teacher.id && id) {
            attendanceService.getByLessonId(axiosPrivate, parseInt(id))
                .then((data: IAttendancesLesson) => {
                    setAttendances(data.attendances);
                    setError(null);
                })
                .catch(error => {
                    if (error.message.includes("Attendance not found")) {
                        setAttendances([]);
                        setError(null);
                    } else {
                        setError(error.message);
                    }
                });
        }
    }, [axiosPrivate, currentUserId, id, subject?.teacher.id]);

    useEffect(() => {
        if (schoolClass && schoolClass.students.some(student => student.id === currentUserId)) {
            attendanceService.getByLessonIdAndStudentId(axiosPrivate, parseInt(id), currentUserId)
                .then((data: IAttendance) => {
                    setStudentAttendance(data);
                    setError(null);
                })
                .catch(error => {
                    if (error.message.includes("Attendance not found")) {
                        setStudentAttendance(null);
                        setError(null);
                    } else {
                        setError(error.message);
                    }
                });
        }
    }, [axiosPrivate, currentUserId, id, schoolClass]);

    const handleAttendanceChange = (studentId: number, present: boolean) => {
        setAttendancesCreate(prevAttendances =>
            prevAttendances.map(attendance =>
                attendance.studentId === studentId ? {...attendance, present} : attendance
            )
        );
    };

    const handleSubmit = async () => {
        setIsSubmitting(true);
        const attendanceData: ICreateAttendancesLesson = {
            lessonId: parseInt(id || "0"),
            attendances: attendancesCreate
        };

        attendanceService.create(axiosPrivate, attendanceData)
            .then((data: IAttendancesLesson) => {
                setAttendances(data.attendances)
                setError(null);
            })
            .catch(error => {
                setError(error.message);
            })
            .finally(() => {
                setIsSubmitting(false);
            });
    };

    return (
        <div className="h-full flex flex-col items-center justify-center w-full">
            <div className="w-full">

                {currentUserId === subject?.teacher.id && attendances.length === 0 && (
                    <div>
                        <h1 className="text-xl font-bold mb-2">Check attendance</h1>
                        <ul className="space-y-2 mb-4">
                            {schoolClass && schoolClass.students.map((student) => (
                                <li key={student.id} className="flex items-center justify-between p-1 border-b">
                                    <span>{`${student.firstName} ${student.lastName}`}</span>
                                    <div className="flex items-center space-x-4">
                                        <label className="flex items-center space-x-2">
                                            <input
                                                type="radio"
                                                name={`attendance-${student.id}`}
                                                checked={attendancesCreate.find(a => a.studentId === student.id)?.present === true}
                                                onChange={() => handleAttendanceChange(student.id, true)}
                                            />
                                            <span>Present</span>
                                        </label>
                                        <label className="flex items-center space-x-2">
                                            <input
                                                type="radio"
                                                name={`attendance-${student.id}`}
                                                checked={attendancesCreate.find(a => a.studentId === student.id)?.present === false}
                                                onChange={() => handleAttendanceChange(student.id, false)}
                                            />
                                            <span>Absent</span>
                                        </label>
                                    </div>
                                </li>
                            ))}
                        </ul>

                        <button
                            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500 transition duration-300"
                            onClick={handleSubmit}
                            disabled={isSubmitting}
                        >
                            {isSubmitting ? "Submitting..." : "Submit Attendance"}
                        </button>
                    </div>
                )}

                {currentUserId === subject?.teacher.id && attendances.length > 0 && (
                    <div>
                        <h1 className="text-xl font-bold mb-2">Class attendances</h1>
                        <ul className="space-y-2 mb-4">
                            {attendances.map(attendance => (
                                <li key={attendance.id}
                                    className="flex items-center justify-between p-1 border-b">
                                    <span>{`${attendance.student.firstName} ${attendance.student.lastName}`}</span>
                                    <span>{attendance.present ? "Present" : "Absent"}</span>
                                </li>
                            ))}
                        </ul>
                    </div>
                )}

                {schoolClass && schoolClass.students.some(student => student.id === currentUserId) && studentAttendance && (
                    <div>
                        <h1 className="text-xl font-bold mb-2">Your attendance</h1>
                        <p>You were {studentAttendance.present ? "present" : "absent"} in this lesson.</p>
                    </div>
                )}

                {error && (
                    <div className="text-lg text-red-500 mt-2">{error}</div>
                )}
            </div>
        </div>
    );
};

export default Attendances;