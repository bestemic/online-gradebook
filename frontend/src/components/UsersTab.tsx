import '@mantine/core/styles.css';
import 'mantine-react-table/styles.css';
import {useEffect, useMemo, useState} from 'react';
import {
    MantineReactTable,
    type MRT_ColumnDef,
    MRT_GlobalFilterTextInput,
    MRT_Row,
    MRT_ToggleFiltersButton,
    useMantineReactTable,
} from 'mantine-react-table';
import {IUser} from "../interfaces/UserInterface.ts";
import {IconRefresh, IconUserCircle} from "@tabler/icons-react";
import {Menu} from '@mantine/core';
import {IRole} from "../interfaces/RoleInterface.ts";
import useAxiosPrivate from "../hooks/useAxiosPrivate.ts";
import userService from "../services/users.ts";
import {toast} from "react-toastify";

const UsersTab = () => {
    const axiosPrivate = useAxiosPrivate();
    const [users, setUsers] = useState<IUser[]>([]);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        userService.getAll(axiosPrivate)
            .then(data => {
                setUsers(data);
                setError(null);
            })
            .catch(error => {
                setError(error.message);
            });
    }, [axiosPrivate]);

    const copyToClipboard = (text: string) => {
        navigator.clipboard.writeText(text);
    };

    const handlePasswordsReset = () => {
        const selectedIds = table.getSelectedRowModel().flatRows.map(row => row.original.id);
        userService.resetPasswords(axiosPrivate, selectedIds)
            .catch((error) => {
                toast.error(error.message, {
                    position: "top-center",
                    autoClose: false,
                    closeOnClick: false,
                });
            });
    };

    const handlePasswordReset = (row: MRT_Row<IUser>) => {
        userService.resetPassword(axiosPrivate, row.original.id)
            .then((password) => {
                toast.success(
                    <span className="cursor-pointer hover:bg-gray-200 p-2 rounded"
                          onClick={() => copyToClipboard(password)}>New password: {password}</span>,
                    {
                        position: "top-center",
                        autoClose: false,
                        closeOnClick: false,
                    }
                );
            })
            .catch((error) => {
                toast.error(error.message, {
                    position: "top-center",
                    autoClose: false,
                    closeOnClick: false,
                });
            });
    };

    const handleViewProfile = () => {
        alert('profile');  // TODO profile view
    };

    const formatRoleName = (roleName: string) => {
        const parts = roleName.split('_');
        const formatted = parts.length > 1 ? parts[1].toLowerCase() : roleName.toLowerCase();
        return formatted.charAt(0).toUpperCase() + formatted.slice(1);
    };

    const columns = useMemo<MRT_ColumnDef<IUser>[]>(
        () => [
            {
                accessorKey: "firstName",
                header: "First Name",
            },
            {
                accessorKey: "lastName",
                header: "Last Name",
            },
            {
                accessorKey: "email",
                header: "Email",
                enableClickToCopy: true,
            },
            {
                accessorKey: "birth",
                header: "Birth Date",
                Cell: ({cell}) => new Date(cell.getValue<string>()).toLocaleDateString(),
            },
            {
                accessorKey: "phoneNumber",
                header: "Phone",
                enableClickToCopy: true,

            },
            {
                accessorKey: "roles",
                header: "Roles",
                mantineFilterSelectProps: {
                    data: ['Admin', 'Teacher', 'Student'],
                },
                filterVariant: 'select',
                Cell: ({cell}) => cell.getValue<IRole[]>().flatMap((role) => formatRoleName(role.name)).join(", "),
                filterFn: (row, id, filterValue) => row.getValue<IRole[]>(id).flatMap((role: IRole) => formatRoleName(role.name)).join(", ").includes(filterValue),
            }
        ], []);

    const table = useMantineReactTable({
        columns,
        data: users,
        enableRowActions: true,
        positionActionsColumn: 'last',
        enableRowSelection: true,
        initialState: {
            showGlobalFilter: true,
        },
        positionToolbarAlertBanner: 'bottom',
        renderRowActionMenuItems: ({row}) => (
            <>
                <Menu.Item leftSection={<IconUserCircle/>} onClick={handleViewProfile}>View profile</Menu.Item>
                <Menu.Item leftSection={<IconRefresh/>} onClick={() => handlePasswordReset(row)}>Reset
                    password</Menu.Item>
            </>
        ),
        renderTopToolbar: ({table}) => {
            return (
                <div className="flex justify-between p-4">
                    <div className="flex space-x-2">
                        <MRT_GlobalFilterTextInput table={table}/>
                        <MRT_ToggleFiltersButton table={table}/>
                    </div>
                    <div className="flex space-x-2">
                        <button
                            className="bg-blue-500 text-white px-4 py-2 rounded disabled:opacity-50"
                            disabled={!(table.getIsAllRowsSelected() || table.getIsSomeRowsSelected())}
                            onClick={handlePasswordsReset}
                        >
                            Reset password
                        </button>
                    </div>
                </div>
            );
        },
    });

    return (
        <>
            {error ? (
                <div className="h-full flex items-center justify-center">
                    <div className="text-center">
                        <h2 className="text-3xl font-semibold text-red-500">{error}</h2>
                    </div>
                </div>
            ) : (
                <MantineReactTable table={table}/>
            )}
        </>
    );
};

export default UsersTab;