import { useEffect, useMemo, useState } from "react";
import { getAllCustomers, updateCustomer, updateCustomerStatus } from "../services/api";
import Button from "../components/ui/Button";
import Card from "../components/ui/Card";
import ConfirmModal from "../components/ui/ConfirmModal";
import Input from "../components/ui/Input";

export default function CustomersPage() {
  // Stores customer list from backend.
  const [customers, setCustomers] = useState([]);

  // Tracks page loading state.
  const [loading, setLoading] = useState(true);

  // Stores page-level error messages.
  const [error, setError] = useState("");

  // Stores the customer currently being edited.
  const [editTarget, setEditTarget] = useState(null);

  // Stores editable customer fields.
  const [editForm, setEditForm] = useState({});

  // Tracks edit save loading state.
  const [editLoading, setEditLoading] = useState(false);

  // Controls edit confirmation modal.
  const [showEditConfirm, setShowEditConfirm] = useState(false);

  // Stores the customer selected for status change.
  const [statusTarget, setStatusTarget] = useState(null);

  // Tracks status update loading state.
  const [statusLoading, setStatusLoading] = useState(false);

  // Controls status confirmation modal.
  const [showStatusConfirm, setShowStatusConfirm] = useState(false);

  // Loads customers when page opens.
  useEffect(() => {
    fetchCustomers();
  }, []);

  // Normalizes backend customer response into an array.
  const customerRows = useMemo(() => {
    if (Array.isArray(customers)) {
      return customers;
    }

    if (Array.isArray(customers?.content)) {
      return customers.content;
    }

    if (Array.isArray(customers?.data)) {
      return customers.data;
    }

    return [];
  }, [customers]);

  // Gets a stable customer database ID.
  const getCustomerId = (customer) =>
    customer?.id || customer?.customerId || customer?._id;

  // Gets phone number from possible backend field names.
  const getPhoneNumber = (customer) =>
    customer?.phoneNumber || customer?.phone || "Not available";

  // Loads all customers from backend.
  const fetchCustomers = async () => {
    setLoading(true);
    setError("");

    try {
      const { data } = await getAllCustomers();
      setCustomers(data);
    } catch (err) {
      setError(err.message || "Failed to load customers.");
    } finally {
      setLoading(false);
    }
  };

  // Opens edit modal with existing customer values.
  const openEdit = (customer) => {
    setEditTarget(customer);
    setEditForm({
      firstName: customer.firstName || "",
      lastName: customer.lastName || "",
      phoneNumber: customer.phoneNumber || customer.phone || "",
      dateOfBirth: customer.dateOfBirth || "",
    });
  };

  // Updates one edit form field.
  const updateEditField = (field, value) => {
    setEditForm((previous) => ({
      ...previous,
      [field]: value,
    }));
  };

  // Shows confirmation before saving profile changes.
  const handleEditSubmit = (e) => {
    e.preventDefault();
    setShowEditConfirm(true);
  };

  // Saves edited customer details to backend.
  const handleEditConfirm = async () => {
    const customerId = getCustomerId(editTarget);

    if (!customerId) {
      setError("Cannot update customer because customer ID is missing.");
      setShowEditConfirm(false);
      return;
    }

    setEditLoading(true);
    setError("");

    try {
      await updateCustomer(customerId, editForm);
      setShowEditConfirm(false);
      setEditTarget(null);
      await fetchCustomers();
    } catch (err) {
      setError(err.message || "Failed to update customer.");
      setShowEditConfirm(false);
    } finally {
      setEditLoading(false);
    }
  };

  // Opens confirmation before changing customer status.
  const handleStatusClick = (customer) => {
    const currentStatus = customer.status || customer.customerStatus || "ACTIVE";
    const newStatus = currentStatus === "ACTIVE" ? "BLOCKED" : "ACTIVE";

    setStatusTarget({
      id: getCustomerId(customer),
      name: `${customer.firstName || ""} ${customer.lastName || ""}`.trim() || customer.username || "customer",
      newStatus,
    });

    setShowStatusConfirm(true);
  };

  // Sends status update request to backend.
  const handleStatusConfirm = async () => {
    if (!statusTarget?.id) {
      setError("Cannot update status because customer ID is missing.");
      setShowStatusConfirm(false);
      setStatusTarget(null);
      return;
    }

    setStatusLoading(true);
    setError("");

    try {
      await updateCustomerStatus(statusTarget.id, statusTarget.newStatus);
      setShowStatusConfirm(false);
      setStatusTarget(null);
      await fetchCustomers();
    } catch (err) {
      setError(err.message || "Failed to update customer status.");
      setShowStatusConfirm(false);
    } finally {
      setStatusLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 px-6 py-8 text-slate-900">
      <div className="mx-auto max-w-7xl">
        <div className="mb-6 flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
          <div>
            <p className="text-sm font-semibold uppercase tracking-[0.2em] text-blue-700">
              Customer Service
            </p>

            <h1 className="mt-2 text-3xl font-bold text-slate-950">
              Customers
            </h1>

            <p className="mt-2 text-sm font-medium text-slate-500">
              View, edit, and manage registered banking customers.
            </p>
          </div>

          <Button variant="outline" onClick={fetchCustomers}>
            Refresh
          </Button>
        </div>

        {error && (
          <div className="mb-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">
            {error}
          </div>
        )}

        {loading ? (
          <Card>
            <div className="flex h-56 items-center justify-center text-sm font-semibold text-slate-500">
              Loading customers...
            </div>
          </Card>
        ) : customerRows.length === 0 ? (
          <Card>
            <div className="flex h-56 flex-col items-center justify-center text-center">
              <h2 className="text-xl font-bold text-slate-950">
                No customers found
              </h2>

              <p className="mt-2 text-sm text-slate-500">
                Registered customers will appear here.
              </p>
            </div>
          </Card>
        ) : (
          <Card padding={false} className="overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full min-w-[900px] text-sm">
                <thead>
                  <tr className="border-b border-slate-200 bg-slate-50">
                    {["Name", "Username", "Email", "Phone", "Status", "Actions"].map((header) => (
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
                  {customerRows.map((customer) => {
                    const customerId = getCustomerId(customer);
                    const status = customer.status || customer.customerStatus || "ACTIVE";
                    const fullName =
                      `${customer.firstName || ""} ${customer.lastName || ""}`.trim() ||
                      customer.name ||
                      "Not available";

                    return (
                      <tr key={customerId || customer.email} className="bg-white transition hover:bg-slate-50">
                        <td className="px-5 py-4">
                          <div className="font-bold text-slate-950">
                            {fullName}
                          </div>

                          <div className="mt-1 text-xs font-medium text-slate-500">
                            ID: {customerId || "Not available"}
                          </div>
                        </td>

                        <td className="px-5 py-4 font-mono text-xs font-semibold text-slate-600">
                          {customer.username || "Not available"}
                        </td>

                        <td className="px-5 py-4 font-medium text-slate-600">
                          {customer.email || "Not available"}
                        </td>

                        <td className="px-5 py-4 font-medium text-slate-600">
                          {getPhoneNumber(customer)}
                        </td>

                        <td className="px-5 py-4">
                          <span
                            className={`inline-flex items-center gap-2 rounded-full px-3 py-1 text-xs font-bold ${
                              status === "ACTIVE"
                                ? "bg-emerald-100 text-emerald-700"
                                : "bg-red-100 text-red-700"
                            }`}
                          >
                            <span
                              className={`h-2 w-2 rounded-full ${
                                status === "ACTIVE" ? "bg-emerald-600" : "bg-red-600"
                              }`}
                            />
                            {status}
                          </span>
                        </td>

                        <td className="px-5 py-4">
                          <div className="flex flex-wrap gap-2">
                            <Button
                              variant="outline"
                              onClick={() => openEdit(customer)}
                              className="h-9 px-3 text-xs"
                            >
                              Edit
                            </Button>

                            <Button
                              variant={status === "ACTIVE" ? "danger" : "primary"}
                              onClick={() => handleStatusClick(customer)}
                              className="h-9 px-3 text-xs"
                            >
                              {status === "ACTIVE" ? "Block" : "Activate"}
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

      {editTarget && (
        <div className="fixed inset-0 z-40 flex items-center justify-center bg-slate-950/70 px-4 backdrop-blur-sm">
          <Card className="w-full max-w-lg">
            <div className="mb-6">
              <h3 className="text-2xl font-bold text-slate-950">
                Edit customer
              </h3>

              <p className="mt-2 text-sm text-slate-500">
                Update editable customer profile fields.
              </p>
            </div>

            <form onSubmit={handleEditSubmit} className="space-y-5">
              <div className="grid gap-5 sm:grid-cols-2">
                <Input
                  label="First name"
                  id="edit-firstName"
                  value={editForm.firstName}
                  onChange={(e) => updateEditField("firstName", e.target.value)}
                />

                <Input
                  label="Last name"
                  id="edit-lastName"
                  value={editForm.lastName}
                  onChange={(e) => updateEditField("lastName", e.target.value)}
                />
              </div>

              <Input
                label="Phone number"
                id="edit-phoneNumber"
                type="tel"
                value={editForm.phoneNumber}
                onChange={(e) => updateEditField("phoneNumber", e.target.value)}
              />

              <Input
                label="Date of birth"
                id="edit-dateOfBirth"
                type="date"
                value={editForm.dateOfBirth}
                onChange={(e) => updateEditField("dateOfBirth", e.target.value)}
              />

              <div className="grid gap-3 pt-2 sm:grid-cols-2">
                <Button
                  variant="outline"
                  onClick={() => setEditTarget(null)}
                  fullWidth
                >
                  Cancel
                </Button>

                <Button type="submit" fullWidth loading={editLoading}>
                  Save changes
                </Button>
              </div>
            </form>
          </Card>
        </div>
      )}

      <ConfirmModal
        open={showEditConfirm}
        title="Save changes?"
        message={`Update profile for ${editTarget?.firstName || ""} ${editTarget?.lastName || ""}?`}
        confirmLabel="Save changes"
        cancelLabel="Review"
        onConfirm={handleEditConfirm}
        onCancel={() => setShowEditConfirm(false)}
        loading={editLoading}
      />

      <ConfirmModal
        open={showStatusConfirm}
        title={`${statusTarget?.newStatus === "BLOCKED" ? "Block" : "Activate"} customer?`}
        message={`Set ${statusTarget?.name}'s account to ${statusTarget?.newStatus}?`}
        confirmLabel="Confirm"
        cancelLabel="Cancel"
        variant={statusTarget?.newStatus === "BLOCKED" ? "danger" : "primary"}
        onConfirm={handleStatusConfirm}
        onCancel={() => {
          setShowStatusConfirm(false);
          setStatusTarget(null);
        }}
        loading={statusLoading}
      />
    </div>
  );
}