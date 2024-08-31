import React, {createContext, useCallback, useState} from "react";
import {ISchoolClass} from "../interfaces/school_class/SchoolClassInterface.ts";
import {ISubject} from "../interfaces/subject/SubjectInterface.ts";
import {ISubjectContext, subjectContextDefaults} from "../interfaces/helper/SubjectContextInterface.ts";

type Props = {
    children: React.ReactNode;
};

const SubjectContext = createContext<ISubjectContext>(subjectContextDefaults);

export const SubjectProvider = ({children}: Props) => {
    const [subject, setSubjectState] = useState<ISubject | null>(subjectContextDefaults.subject);
    const [schoolClass, setSchoolClassState] = useState<ISchoolClass | null>(subjectContextDefaults.schoolClass);

    const setSubject = useCallback((subject: ISubject) => {
        setSubjectState(subject);
    }, []);

    const setSchoolClass = useCallback((schoolClass: ISchoolClass) => {
        setSchoolClassState(schoolClass);
    }, []);

    return (
        <SubjectContext.Provider value={{subject, setSubject, schoolClass, setSchoolClass}}>
            {children}
        </SubjectContext.Provider>
    );
};

export default SubjectContext;