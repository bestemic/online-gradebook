import '@mantine/core/styles.css';
import 'mantine-react-table/styles.css';
import {useState} from 'react';
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";

const SubjectsTab = () => {
    const axiosPrivate = useAxiosPrivate();
    const [error, setError] = useState<string | null>(null);

    return (
        <>
            {error ? (
                <div className="h-full flex items-center justify-center">
                    <div className="text-center">
                        <h2 className="text-3xl font-semibold text-red-500">{error}</h2>
                    </div>
                </div>
            ) : (
                <span>Test</span>
            )}
        </>
    );
};

export default SubjectsTab;