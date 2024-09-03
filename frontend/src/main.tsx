import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './index.css'
import {BrowserRouter} from "react-router-dom";
import {AuthProvider} from "./context/AuthProvider.tsx";
import {MantineProvider} from "@mantine/core";
import {SubjectProvider} from "./context/SubjecProvider.tsx";

ReactDOM.createRoot(document.getElementById('root')!).render(
    <MantineProvider>
        <BrowserRouter>
            <AuthProvider>
                <SubjectProvider>
                    <App/>
                </SubjectProvider>
            </AuthProvider>
        </BrowserRouter>
    </MantineProvider>
)
