import {useState} from "react";
import SubjectsTab from "./SubjectsTab.tsx";
import AddSubject from "./AddSubject.tsx";


const AdminSubjectsManagement = () => {
    const [activeTab, setActiveTab] = useState<"subjects" | "addSubject">("subjects");

    return (
        <div className="h-full flex flex-col">
            <div className="flex justify-end">
                <button
                    onClick={() => setActiveTab("subjects")}
                    className={`px-4 py-2 rounded-t-2xl text-white text-lg font-bold border border-b-0 ${
                        activeTab === "subjects" ? "bg-blue-600" : "bg-blue-400"
                    }`}
                    style={{minWidth: "10%"}}
                >
                    Subjects
                </button>
                <button
                    onClick={() => setActiveTab("addSubject")}
                    className={`px-4 py-2 rounded-t-2xl text-white text-lg font-bold border border-b-0 ${
                        activeTab === "addSubject" ? "bg-blue-600" : "bg-blue-400"
                    }`}
                    style={{minWidth: "10%"}}
                >
                    Add Subject
                </button>
            </div>

            <div className="h-full bg-white rounded-b shadow-2xl">
                {activeTab === "subjects" ? <SubjectsTab/> : <AddSubject/>}
            </div>
        </div>
    );
};

export default AdminSubjectsManagement;