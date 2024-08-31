import Login from "./components/user/Login.tsx";
import {Route, Routes} from "react-router-dom";
import UserManagement from "./components/user/UserManagement.tsx";
import NotFound from "./components/NotFound.tsx";
import Unauthorized from "./components/Unauthorized.tsx";
import MainPage from "./components/MainPage.tsx";
import RequireAuth from "./components/wrapper/RequireAuth.tsx";
import Navigation from "./components/Navigation.tsx";
import {ROLES} from "./constants/roles.ts";
import ChangePassword from "./components/user/ChangePassword.tsx";
import {ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import UserProfile from "./components/user/UserProfile.tsx";
import ClassManagement from "./components/class/ClassManagement.tsx";
import ClassProfile from "./components/class/ClassProfile.tsx";
import SubjectsManagement from "./components/subject/SubjectsManagement.tsx";
import SubjectProfile from "./components/subject/SubjectProfile.tsx";
import LessonProfile from "./components/lesson/LessonProfile.tsx";

function App() {

    return (
        <div className="App">
            <ToastContainer/>
            <Routes>
                <Route path="/" element={<Navigation/>}>
                    <Route path="login" element={<Login/>}/>
                    <Route path="unauthorized" element={<Unauthorized/>}/>

                    <Route element={<RequireAuth allowedRoles={[ROLES.Admin, ROLES.Teacher, ROLES.Student]}/>}>
                        <Route index element={<MainPage/>}/>
                        <Route path="change-password" element={<ChangePassword/>}/>
                        <Route path="/users/:id" element={<UserProfile/>}/>
                        <Route path="/classes/:id" element={<ClassProfile/>}/>
                        <Route path="subjects" element={<SubjectsManagement/>}/>
                    </Route>

                    <Route element={<RequireAuth allowedRoles={[ROLES.Teacher, ROLES.Student]}/>}>
                        <Route path="subjects/:id" element={<SubjectProfile/>}/>
                        <Route path="lessons/:id" element={<LessonProfile/>}/>
                    </Route>

                    <Route element={<RequireAuth allowedRoles={[ROLES.Admin]}/>}>
                        <Route path="users" element={<UserManagement/>}/>
                        <Route path="classes" element={<ClassManagement/>}/>
                    </Route>

                    <Route path="*" element={<NotFound/>}/>
                </Route>
            </Routes>
        </div>
    )
}

export default App
