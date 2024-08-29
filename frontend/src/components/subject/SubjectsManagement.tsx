import {useEffect, useState} from "react";
import AdminSubjects from "./AdminSubjects.tsx";
import TeacherSubjects from "./TeacherSubjects.tsx";
import StudentSubjects from "./StudentSubjects.tsx";
import RequireRole from "../wrapper/RequireRole.tsx";
import {ROLES} from "../../constants/roles.ts";
import useAuth from "../../hooks/useAuth.ts";
import JwtInterface from "../../interfaces/helper/JwtInterface.ts";
import {jwtDecode} from "jwt-decode";
import AddSubject from "./AddSubject.tsx";


const SubjectsManagement = () => {
    const {auth} = useAuth();
    const [activeTab, setActiveTab] = useState<string>("");

    useEffect(() => {
        const decoded: JwtInterface | undefined = auth?.token ? jwtDecode(auth.token) : undefined;
        const roles: string[] = decoded?.roles.split(',') || [];

        if (activeTab === "") {
            if (roles.includes(ROLES.Admin)) {
                setActiveTab("adminSubjects");
            } else if (roles.includes(ROLES.Teacher)) {
                setActiveTab("teacherSubjects");
            } else if (roles.includes(ROLES.Student)) {
                setActiveTab("studentSubjects");
            }
        }
    }, [activeTab, auth.token]);

    return (
        <div className="h-full flex flex-col">
            <div className="flex justify-end">
                <RequireRole allowedRoles={[ROLES.Student]}>
                    <button
                        onClick={() => setActiveTab("studentSubjects")}
                        className={`px-4 py-2 rounded-t-2xl text-white text-lg font-bold border border-b-0 ${
                            activeTab === "studentSubjects" ? "bg-blue-600" : "bg-blue-400"
                        }`}
                        style={{minWidth: "15%"}}
                    >
                        My Subjects
                    </button>
                </RequireRole>
                <RequireRole allowedRoles={[ROLES.Teacher]}>
                    <button
                        onClick={() => setActiveTab("teacherSubjects")}
                        className={`px-4 py-2 rounded-t-2xl text-white text-lg font-bold border border-b-0 ${
                            activeTab === "teacherSubjects" ? "bg-blue-600" : "bg-blue-400"
                        }`}
                        style={{minWidth: "15%"}}
                    >
                        Subjects I Teach
                    </button>
                </RequireRole>
                <RequireRole allowedRoles={[ROLES.Admin]}>
                    <button
                        onClick={() => setActiveTab("adminSubjects")}
                        className={`px-4 py-2 rounded-t-2xl text-white text-lg font-bold border border-b-0 ${
                            activeTab === "adminSubjects" ? "bg-blue-600" : "bg-blue-400"
                        }`}
                        style={{minWidth: "15%"}}
                    >
                        Manage Subjects
                    </button>
                </RequireRole>
                <RequireRole allowedRoles={[ROLES.Admin]}>
                    <button
                        onClick={() => setActiveTab("addSubject")}
                        className={`px-4 py-2 rounded-t-2xl text-white text-lg font-bold border border-b-0 ${
                            activeTab === "addSubject" ? "bg-blue-600" : "bg-blue-400"
                        }`}
                        style={{minWidth: "15%"}}
                    >
                        Add Subject
                    </button>
                </RequireRole>
            </div>

            <div className="h-full bg-white rounded-b shadow-2xl">
                {activeTab === "adminSubjects" && (<AdminSubjects/>)}
                {activeTab === "addSubject" && (<AddSubject/>)}
                {activeTab === "teacherSubjects" && (<TeacherSubjects/>)}
                {activeTab === "studentSubjects" && (<StudentSubjects/>)}
            </div>
        </div>
    );
};

export default SubjectsManagement;