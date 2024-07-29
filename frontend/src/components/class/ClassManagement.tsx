import {useState} from "react";
import ClassesTab from "./ClassesTab.tsx";
import AddClass from "./AddClass.tsx";


const ClassManagement = () => {
    const [activeTab, setActiveTab] = useState<"classes" | "addClass">("classes");

    return (
        <div className="h-full flex flex-col">
            <div className="flex justify-end">
                <button
                    onClick={() => setActiveTab("classes")}
                    className={`px-4 py-2 rounded-t-2xl text-white text-lg font-bold border border-b-0 ${
                        activeTab === "classes" ? "bg-blue-600" : "bg-blue-400"
                    }`}
                    style={{minWidth: "10%"}}
                >
                    Classes
                </button>
                <button
                    onClick={() => setActiveTab("addClass")}
                    className={`px-4 py-2 rounded-t-2xl text-white text-lg font-bold border border-b-0 ${
                        activeTab === "addClass" ? "bg-blue-600" : "bg-blue-400"
                    }`}
                    style={{minWidth: "10%"}}
                >
                    Add Class
                </button>
            </div>

            <div className="h-full bg-white rounded-b shadow-2xl">
                {activeTab === "classes" ? <ClassesTab/> : <AddClass/>}
            </div>
        </div>
    );
};

export default ClassManagement;