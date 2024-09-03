import {ICreateAttendance} from "./CreateAttendanceInterface.ts";

export interface ICreateAttendancesLesson {
    lessonId: number;
    attendances: ICreateAttendance[];
}