import {useContext} from "react";
import SubjectProvider from "../context/SubjecProvider.tsx";

const useSubject = () => {
    return useContext(SubjectProvider);
}

export default useSubject;