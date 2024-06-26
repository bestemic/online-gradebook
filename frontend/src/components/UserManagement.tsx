import {useState} from "react";
import AddUser from "./AddUser.tsx";
import UsersTab from "./UsersTab.tsx";


const UserManagement = () => {
    const [activeTab, setActiveTab] = useState<"users" | "addUser">("users");

    return (
        <div className="h-full flex flex-col">
            <div className="flex justify-end">
                <button
                    onClick={() => setActiveTab("users")}
                    className={`px-4 py-2 rounded-t-2xl text-white text-lg font-bold border border-b-0 ${
                        activeTab === "users" ? "bg-blue-600" : "bg-blue-400"
                    }`}
                    style={{minWidth: "10%"}}
                >
                    Users
                </button>
                <button
                    onClick={() => setActiveTab("addUser")}
                    className={`px-4 py-2 rounded-t-2xl text-white text-lg font-bold border border-b-0 ${
                        activeTab === "addUser" ? "bg-blue-600" : "bg-blue-400"
                    }`}
                    style={{minWidth: "10%"}}
                >
                    Add User
                </button>
            </div>

            <div className="h-full bg-white rounded-b shadow-2xl">
                {activeTab === "users" ? <UsersTab/> : <AddUser/>}
            </div>
        </div>
    );
};

export default UserManagement;