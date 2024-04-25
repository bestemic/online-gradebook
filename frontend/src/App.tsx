import Login from "./components/Login.tsx";
import {Route, Routes} from "react-router-dom";
import Admin from "./components/Admin.tsx";
import Teacher from "./components/Teacher.tsx";
import Student from "./components/Student.tsx";
import NotFound from "./components/NotFound.tsx";
import Unauthorized from "./components/Unauthorized.tsx";
import MainPage from "./components/MainPage.tsx";
import RequireAuth from "./components/RequireAuth.tsx";
import Navigation from "./components/Navigation.tsx";
import {ROLES} from "./constants/roles.ts";

function App() {

    return (
        <div className="App">
            <Routes>
                <Route path="/" element={<Navigation/>}>
                    <Route path="login" element={<Login/>}/>
                    <Route path="unauthorized" element={<Unauthorized/>}/>

                    <Route element={<RequireAuth allowedRoles={[ROLES.Admin, ROLES.Teacher, ROLES.Student]}/>}>
                        <Route index element={<MainPage/>}/>
                    </Route>

                    <Route element={<RequireAuth allowedRoles={[ROLES.Admin]}/>}>
                        <Route path="admin" element={<Admin/>}/>
                    </Route>

                    <Route element={<RequireAuth allowedRoles={[ROLES.Teacher]}/>}>
                        <Route path="teacher" element={<Teacher/>}/>
                    </Route>

                    <Route element={<RequireAuth allowedRoles={[ROLES.Student]}/>}>
                        <Route path="student" element={<Student/>}/>
                    </Route>

                    <Route path="*" element={<NotFound/>}/>
                </Route>
            </Routes>
        </div>
    )
}

export default App
