import SubjectOverview from "./SubjectOverview.tsx";
import {useState} from "react";
import LessonsTab from "../lesson/LessonsTab.tsx";
import GradesTab from "../grade/GradesTab.tsx";
import MaterialsTab from "../material/MaterialsTab.tsx";

const SubjectProfile = () => {
    const [activeTab, setActiveTab] = useState<string>("subjectOverview");

    return (
        <div className="h-full flex flex-col">
            <div className="flex justify-end">
                <button
                    onClick={() => setActiveTab("subjectOverview")}
                    className={`px-4 py-2 rounded-t-2xl text-white text-lg font-bold border border-b-0 ${
                        activeTab === "subjectOverview" ? "bg-blue-600" : "bg-blue-400"
                    }`}
                    style={{minWidth: "15%"}}
                >
                    Overview
                </button>
                <button
                    onClick={() => setActiveTab("lessonsTab")}
                    className={`px-4 py-2 rounded-t-2xl text-white text-lg font-bold border border-b-0 ${
                        activeTab === "lessonsTab" ? "bg-blue-600" : "bg-blue-400"
                    }`}
                    style={{minWidth: "15%"}}
                >
                    Lessons
                </button>
                <button
                    onClick={() => setActiveTab("gradesTab")}
                    className={`px-4 py-2 rounded-t-2xl text-white text-lg font-bold border border-b-0 ${
                        activeTab === "gradesTab" ? "bg-blue-600" : "bg-blue-400"
                    }`}
                    style={{minWidth: "15%"}}
                >
                    Grades
                </button>
                <button
                    onClick={() => setActiveTab("materialsTab")}
                    className={`px-4 py-2 rounded-t-2xl text-white text-lg font-bold border border-b-0 ${
                        activeTab === "materialsTab" ? "bg-blue-600" : "bg-blue-400"
                    }`}
                    style={{minWidth: "15%"}}
                >
                    Materials
                </button>
            </div>

            <div className="h-full bg-white rounded-b shadow-2xl">
                {activeTab === "subjectOverview" && (<SubjectOverview/>)}
                {activeTab === "lessonsTab" && (<LessonsTab/>)}
                {activeTab === "gradesTab" && (<GradesTab/>)}
                {activeTab === "materialsTab" && (<MaterialsTab/>)}
            </div>
        </div>
    );
};

export default SubjectProfile;
