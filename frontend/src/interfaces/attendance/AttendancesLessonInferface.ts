import {ILesson} from "../lesson/LessonInterface.ts";
import {IAttendance} from "./AttendanceInterface.ts";

export interface IAttendancesLesson {
    lesson: ILesson;
    attendances: IAttendance[];
}