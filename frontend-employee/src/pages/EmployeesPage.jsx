import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import {
  createEmployee,
  getAllEmployees,
  updateEmployee,
  updateEmployeeRole,
  updateEmployeeStatus,
} from "../services/api";
import Button from "../components/ui/Button";
import Card from "../components/ui/Card";
import ConfirmModal from "../components/ui/ConfirmModal";
import Input from "../components/ui/Input";

const emptyCreateForm = {
  firstName: "",
  lastName: "",
  email: "",
  username: "",
  phone: "",
  password: "",
  dateOfBirth: "",
  employeeId: "",
  role: "TELLER",
  department: "OPERATIONS",
};

export default function EmployeesPage() {
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [showCreate, setShowCreate] = useState(false);
  const [createForm, setCreateForm] = useState(emptyCreateForm);

  const [editTarget, setEditTarget] = useState(null);
  const [editForm, setEditForm] = useState({});

  const [roleTarget, setRoleTarget] = useState(null);
  const [statusTarget, setStatusTarget] = useState(null);

  useEffect(() => {
    loadEmployees();
  }, []);

  const employeeRows = useMemo(() => normalizeList(employees), [employees]);

  async function loadEmployees() {
    setLoading(true);
    setError("");

    try {
      const { data } = await getAllEmployees();
      setEmployees(data);
    } catch (err) {
      setError(err.message || "Failed to load employees.");
    } finally {
      setLoading(false);
    }
  }

  function normalizeList(data) {
    if (Array.isArray(data)) {
      return data;
    }

    if (Array.isArray(data?.content)) {
      return data.content;
    }

    if (Array.isArray(data?.data)) {
      return data.data;
    }

    return [];
  }

  function getEmployeeDbId(employee) {
    return employee?.id || employee?._id;
  }

  function updateCreateField(field, value) {
    setCreateForm((previous) => ({
      ...previous,
      [field]: value,
    }));
  }

  function updateEditField(field, value) {
    setEditForm((previous) => ({
      ...previous,
      [field]: value,
    }));
  }

  function validateCreateForm() {
    if (!createForm.firstName.trim()) return "First name is required.";
    if (!createForm.lastName.trim()) return "Last name is required.";
    if (!createForm.email.trim()) return "Email is required.";
    if (!createForm.username.trim()) return "Username is required.";
    if (!createForm.phone.trim()) return "Phone is required.";
    if (!createForm.password.trim()) return "Password is required.";
    if (!createForm.dateOfBirth.trim()) return "Date of birth is required.";
    if (!createForm.employeeId.trim()) return "Employee ID is required.";
    if (!createForm.role.trim()) return "Role is required.";
    if (!createForm.department.trim()) return "Department is required.";
    return "";
  }

  async function handleCreateEmployee(event) {
    event.preventDefault();

    const validationMessage = validateCreateForm();

    if (validationMessage) {
      setError(validationMessage);
      return;
    }

    setSaving(true);
    setError("");

    try {
      await createEmployee({
        ...createForm,
        firstName: createForm.firstName.trim(),
        lastName: createForm.lastName.trim(),
        email: createForm.email.trim(),
        username: createForm.username.trim(),
        phone: createForm.phone.trim(),
        employeeId: createForm.employeeId.trim(),
        role: createForm.role.trim(),
        department: createForm.department.trim(),
      });

      setCreateForm(emptyCreateForm);
      setShowCreate(false);
      await loadEmployees();
    } catch (err) {
      setError(err.message || "Failed to create employee.");
    } finally {
      setSaving(false);
    }
  }

  function openEdit(employee) {
    setEditTarget(employee);
    setEditForm({
      firstName: employee.firstName || "",
      lastName: employee.lastName || "",
      email: employee.email || "",
      phone: employee.phone || "",
      department: employee.department || "",
    });
  }

  async function handleUpdateEmployee(event) {
    event.preventDefault();

    const employeeId = getEmployeeDbId(editTarget);

    if (!employeeId) {
      setError("Employee database ID is missing.");
      return;
    }

    setSaving(true);
    setError("");

    try {
      await updateEmployee(employeeId, editForm);
      setEditTarget(null);
      await loadEmployees();
    } catch (err) {
      setError(err.message || "Failed to update employee.");
    } finally {
      setSaving(false);
    }
  }

  async function handleRoleUpdate() {
    if (!roleTarget?.id) {
      setError("Employee database ID is missing.");
      setRoleTarget(null);
      return;
    }

    setSaving(true);
    setError("");

    try {
      await updateEmployeeRole(roleTarget.id, roleTarget.role);
      setRoleTarget(null);
      await loadEmployees();
    } catch (err) {
      setError(err.message || "Failed to update employee role.");
    } finally {
      setSaving(false);
    }
  }

  async function handleStatusUpdate() {
    if (!statusTarget?.id) {
      setError("Employee database ID is missing.");
      setStatusTarget(null);
      return;
    }

    setSaving(true);
    setError("");

    try {
      await updateEmployeeStatus(statusTarget.id, statusTarget.status);
      setStatusTarget(null);
      await loadEmployees();
    } catch (err) {
      setError(err.message || "Failed to update employee status.");
    } finally {
      setSaving(false);
    }
  }

  function formatName(employee) {
    return `${employee.firstName || ""} ${employee.lastName || ""}`.trim() || "Employee";
  }

  return (
    <div className="min-h-screen bg-slate-100 px-6 py-8 text-slate-900">
      <div className="mb-6 flex flex-wrap items-center gap-3">
        <Link
          to="/dashboard"
          className="rounded-xl bg-staff-950 px-4 py-2 text-sm font-bold text-white hover:bg-staff-800"
        >
          Dashboard
        </Link>
        <Link
          to="/customers"
          className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-bold text-slate-700 hover:bg-slate-50"
        >
          Customers
        </Link>
        <Link
          to="/products"
          className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-bold text-slate-700 hover:bg-slate-50"
        >
          Products
        </Link>
      </div>

      <div className="mx-auto max-w-7xl">
        <div className="mb-6 flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
          <div>
            <p className="text-sm font-semibold uppercase tracking-[0.2em] text-amber-600">
              Employee Portal
            </p>
            <h1 className="mt-2 text-3xl font-bold text-staff-950">
              Employees
            </h1>
            <p className="mt-2 text-sm font-medium text-slate-500">
              Create employee accounts, update staff profiles, change roles, and manage status.
            </p>
          </div>

          <div className="flex gap-3">
            <Button variant="outline" onClick={loadEmployees}>
              Refresh
            </Button>
            <Button onClick={() => setShowCreate(true)}>
              Create Employee
            </Button>
          </div>
        </div>

        {error && (
          <div className="mb-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">
            {error}
          </div>
        )}

        {loading ? (
          <Card>
            <div className="flex h-56 items-center justify-center text-sm font-semibold text-slate-500">
              Loading employees...
            </div>
          </Card>
        ) : employeeRows.length === 0 ? (
          <Card>
            <div className="flex h-56 flex-col items-center justify-center text-center">
              <h2 className="text-xl font-bold text-staff-950">
                No employees found
              </h2>
              <p className="mt-2 text-sm text-slate-500">
                Created employees will appear here.
              </p>
            </div>
          </Card>
        ) : (
          <Card padding={false} className="overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full min-w-[1050px] text-sm">
                <thead>
                  <tr className="border-b border-slate-200 bg-slate-50">
                    {["Employee", "Employee ID", "Email", "Role", "Department", "Status", "Actions"].map((header) => (
                      <th
                        key={header}
                        className="px-5 py-4 text-left text-xs font-bold uppercase tracking-wider text-slate-500"
                      >
                        {header}
                      </th>
                    ))}
                  </tr>
                </thead>

                <tbody className="divide-y divide-slate-200">
                  {employeeRows.map((employee) => {
                    const dbId = getEmployeeDbId(employee);
                    const status = employee.status || "ACTIVE";
                    const nextStatus = status === "ACTIVE" ? "INACTIVE" : "ACTIVE";

                    return (
                      <tr key={dbId || employee.employeeId} className="bg-white hover:bg-slate-50">
                        <td className="px-5 py-4">
                          <div className="font-bold text-staff-950">
                            {formatName(employee)}
                          </div>
                          <div className="mt-1 text-xs font-medium text-slate-500">
                            @{employee.username || "not available"}
                          </div>
                        </td>

                        <td className="px-5 py-4 font-mono text-xs font-semibold text-slate-600">
                          {employee.employeeId || "Not available"}
                        </td>

                        <td className="px-5 py-4 font-medium text-slate-600">
                          {employee.email || "Not available"}
                        </td>

                        <td className="px-5 py-4">
                          <RoleSelect
                            value={employee.role || "TELLER"}
                            onChange={(role) =>
                              setRoleTarget({
                                id: dbId,
                                name: formatName(employee),
                                role,
                              })
                            }
                          />
                        </td>

                        <td className="px-5 py-4 font-medium text-slate-600">
                          {employee.department || "Not available"}
                        </td>

                        <td className="px-5 py-4">
                          <StatusBadge status={status} />
                        </td>

                        <td className="px-5 py-4">
                          <div className="flex flex-wrap gap-2">
                            <Button
                              variant="outline"
                              onClick={() => openEdit(employee)}
                              className="h-9 px-3 text-xs"
                            >
                              Edit
                            </Button>
                            <Button
                              variant={status === "ACTIVE" ? "danger" : "primary"}
                              onClick={() =>
                                setStatusTarget({
                                  id: dbId,
                                  name: formatName(employee),
                                  status: nextStatus,
                                })
                              }
                              className="h-9 px-3 text-xs"
                            >
                              {status === "ACTIVE" ? "Deactivate" : "Activate"}
                            </Button>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </Card>
        )}
      </div>

      {showCreate && (
        <ModalCard title="Create employee" onClose={() => setShowCreate(false)}>
          <form onSubmit={handleCreateEmployee} className="space-y-5">
            <div className="grid gap-5 sm:grid-cols-2">
              <Input label="First name" value={createForm.firstName} onChange={(e) => updateCreateField("firstName", e.target.value)} />
              <Input label="Last name" value={createForm.lastName} onChange={(e) => updateCreateField("lastName", e.target.value)} />
              <Input label="Email" type="email" value={createForm.email} onChange={(e) => updateCreateField("email", e.target.value)} />
              <Input label="Username" value={createForm.username} onChange={(e) => updateCreateField("username", e.target.value)} />
              <Input label="Phone" value={createForm.phone} onChange={(e) => updateCreateField("phone", e.target.value)} />
              <Input label="Password" type="password" value={createForm.password} onChange={(e) => updateCreateField("password", e.target.value)} />
              <Input label="Date of birth" type="date" value={createForm.dateOfBirth} onChange={(e) => updateCreateField("dateOfBirth", e.target.value)} />
              <Input label="Employee ID" value={createForm.employeeId} onChange={(e) => updateCreateField("employeeId", e.target.value)} />
            </div>

            <div className="grid gap-5 sm:grid-cols-2">
              <SelectField label="Role" value={createForm.role} onChange={(value) => updateCreateField("role", value)} options={["ADMIN", "MANAGER", "TELLER", "SUPPORT"]} />
              <SelectField label="Department" value={createForm.department} onChange={(value) => updateCreateField("department", value)} options={["OPERATIONS", "CUSTOMER_SERVICE", "PRODUCTS", "COMPLIANCE"]} />
            </div>

            <div className="grid gap-3 pt-2 sm:grid-cols-2">
              <Button type="button" variant="outline" fullWidth onClick={() => setShowCreate(false)}>
                Cancel
              </Button>
              <Button type="submit" fullWidth loading={saving}>
                Create Employee
              </Button>
            </div>
          </form>
        </ModalCard>
      )}

      {editTarget && (
        <ModalCard title="Edit employee" onClose={() => setEditTarget(null)}>
          <form onSubmit={handleUpdateEmployee} className="space-y-5">
            <div className="grid gap-5 sm:grid-cols-2">
              <Input label="First name" value={editForm.firstName} onChange={(e) => updateEditField("firstName", e.target.value)} />
              <Input label="Last name" value={editForm.lastName} onChange={(e) => updateEditField("lastName", e.target.value)} />
              <Input label="Email" type="email" value={editForm.email} onChange={(e) => updateEditField("email", e.target.value)} />
              <Input label="Phone" value={editForm.phone} onChange={(e) => updateEditField("phone", e.target.value)} />
            </div>

            <Input label="Department" value={editForm.department} onChange={(e) => updateEditField("department", e.target.value)} />

            <div className="grid gap-3 pt-2 sm:grid-cols-2">
              <Button type="button" variant="outline" fullWidth onClick={() => setEditTarget(null)}>
                Cancel
              </Button>
              <Button type="submit" fullWidth loading={saving}>
                Save Changes
              </Button>
            </div>
          </form>
        </ModalCard>
      )}

      <ConfirmModal
        open={Boolean(roleTarget)}
        title="Update employee role?"
        message={`Set ${roleTarget?.name}'s role to ${roleTarget?.role}?`}
        confirmLabel="Update Role"
        cancelLabel="Cancel"
        onConfirm={handleRoleUpdate}
        onCancel={() => setRoleTarget(null)}
        loading={saving}
      />

      <ConfirmModal
        open={Boolean(statusTarget)}
        title={`${statusTarget?.status === "ACTIVE" ? "Activate" : "Deactivate"} employee?`}
        message={`Set ${statusTarget?.name}'s status to ${statusTarget?.status}?`}
        confirmLabel="Confirm"
        cancelLabel="Cancel"
        variant={statusTarget?.status === "ACTIVE" ? "primary" : "danger"}
        onConfirm={handleStatusUpdate}
        onCancel={() => setStatusTarget(null)}
        loading={saving}
      />
    </div>
  );
}

function RoleSelect({ value, onChange }) {
  return (
    <select
      value={value}
      onChange={(event) => onChange(event.target.value)}
      className="h-10 rounded-xl border border-slate-300 bg-white px-3 text-xs font-bold text-slate-700 outline-none focus:border-amber-500 focus:ring-4 focus:ring-amber-100"
    >
      <option value="ADMIN">ADMIN</option>
      <option value="MANAGER">MANAGER</option>
      <option value="TELLER">TELLER</option>
      <option value="SUPPORT">SUPPORT</option>
    </select>
  );
}

function SelectField({ label, value, onChange, options }) {
  return (
    <label className="block">
      <span className="mb-2 block text-sm font-bold text-slate-800">
        {label}
      </span>
      <select
        value={value}
        onChange={(event) => onChange(event.target.value)}
        className="h-12 w-full rounded-xl border border-slate-300 bg-white px-4 text-sm font-semibold text-slate-900 outline-none focus:border-amber-500 focus:ring-4 focus:ring-amber-100"
      >
        {options.map((option) => (
          <option key={option} value={option}>
            {option}
          </option>
        ))}
      </select>
    </label>
  );
}

function StatusBadge({ status }) {
  const active = status === "ACTIVE";

  return (
    <span
      className={`inline-flex items-center gap-2 rounded-full px-3 py-1 text-xs font-bold ${
        active ? "bg-emerald-100 text-emerald-700" : "bg-red-100 text-red-700"
      }`}
    >
      <span className={`h-2 w-2 rounded-full ${active ? "bg-emerald-600" : "bg-red-600"}`} />
      {status}
    </span>
  );
}

function ModalCard({ title, children, onClose }) {
  return (
    <div className="fixed inset-0 z-40 flex items-center justify-center bg-slate-950/70 px-4 py-8 backdrop-blur-sm">
      <Card className="max-h-[90vh] w-full max-w-3xl overflow-y-auto">
        <div className="mb-6 flex items-start justify-between gap-4">
          <div>
            <h3 className="text-2xl font-bold text-staff-950">{title}</h3>
            <p className="mt-2 text-sm text-slate-500">
              Employee records are managed by authorized staff.
            </p>
          </div>

          <button
            type="button"
            onClick={onClose}
            className="rounded-xl border border-slate-300 px-3 py-2 text-sm font-bold text-slate-600 hover:bg-slate-50"
          >
            Close
          </button>
        </div>

        {children}
      </Card>
    </div>
  );
}