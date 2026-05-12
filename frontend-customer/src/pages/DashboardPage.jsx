import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getCustomerById, updateCustomer } from "../services/api";
import Button from "../components/ui/Button";
import Card from "../components/ui/Card";
import ConfirmModal from "../components/ui/ConfirmModal";
import Input from "../components/ui/Input";
import { useAuth } from "../context/AuthContext";

export default function DashboardPage() {
  // Used to return customer to login after logout.
  const navigate = useNavigate();

  // Reads logged-in customer and auth actions from global auth context.
  const { user, loginUser, logoutUser } = useAuth();

  // Controls logout confirmation popup.
  const [showLogout, setShowLogout] = useState(false);

  // Controls edit profile popup.
  const [showEdit, setShowEdit] = useState(false);

  // Controls save confirmation popup.
  const [showSaveConfirm, setShowSaveConfirm] = useState(false);

  // Tracks profile update loading state.
  const [saving, setSaving] = useState(false);

  // Stores page-level error messages.
  const [error, setError] = useState("");

  // Stores editable customer fields.
  const [editForm, setEditForm] = useState({
    firstName: "",
    lastName: "",
  });

  // Gets the customer ID from possible backend field names.
  const customerId = user?.id || user?.customerId || user?._id;

  // Gets customer full name for display.
  const customerName = useMemo(() => {
    const fullName = `${user?.firstName || ""} ${user?.lastName || ""}`.trim();

    return fullName || user?.username || user?.email || "Customer";
  }, [user]);

  // Gets first letter for profile avatar.
  const customerInitial = customerName.charAt(0).toUpperCase();

  // Keeps edit form synced with logged-in customer.
  useEffect(() => {
    setEditForm({
      firstName: user?.firstName || "",
      lastName: user?.lastName || "",
    });
  }, [user]);

  // Formats backend date values for cleaner display.
  function formatDate(value) {
    if (!value) {
      return "Not available";
    }

    return String(value).split("T")[0];
  }

  // Opens edit modal with current profile values.
  function openEditProfile() {
    setError("");
    setEditForm({
      firstName: user?.firstName || "",
      lastName: user?.lastName || "",
    });
    setShowEdit(true);
  }

  // Updates one editable profile field.
  function updateEditField(field, value) {
    setEditForm((previous) => ({
      ...previous,
      [field]: value,
    }));
  }

  // Validates edit form before save confirmation.
  function handleEditSubmit(e) {
    e.preventDefault();

    if (!editForm.firstName.trim()) {
      setError("First name is required.");
      return;
    }

    if (!editForm.lastName.trim()) {
      setError("Last name is required.");
      return;
    }

    setError("");
    setShowSaveConfirm(true);
  }

  // Saves allowed customer profile changes.
  async function handleSaveProfile() {
    if (!customerId) {
      setError("Customer ID is missing. Please log in again.");
      setShowSaveConfirm(false);
      return;
    }

    setSaving(true);
    setError("");

    try {
      const payload = {
        firstName: editForm.firstName.trim(),
        lastName: editForm.lastName.trim(),
      };

      const { data } = await updateCustomer(customerId, payload);

      const updatedCustomer = data?.id || data?.customerId ? data : { ...user, ...payload };

      loginUser(updatedCustomer);
      localStorage.setItem("authUser", JSON.stringify(updatedCustomer));

      setShowSaveConfirm(false);
      setShowEdit(false);
    } catch (err) {
      setError(err.message || "Failed to update profile.");
      setShowSaveConfirm(false);
    } finally {
      setSaving(false);
    }
  }

  // Refreshes logged-in customer from backend.
  async function refreshProfile() {
    if (!customerId) {
      setError("Customer ID is missing. Please log in again.");
      return;
    }

    setError("");

    try {
      const { data } = await getCustomerById(customerId);
      loginUser(data);
      localStorage.setItem("authUser", JSON.stringify(data));
    } catch (err) {
      setError(err.message || "Failed to refresh profile.");
    }
  }

  // Clears saved login data and returns customer to login page.
  function handleLogout() {
    logoutUser();
    navigate("/login", { replace: true });
  }

  return (
    <div className="min-h-screen bg-slate-100 text-slate-900">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-5">
          <div className="flex items-center gap-4">
            <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-blue-900 text-white shadow-lg shadow-blue-900/20">
              <svg className="h-6 w-6" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 2 3 6.5V9h18V6.5L12 2Zm-7 9v7h2v-7H5Zm4 0v7h2v-7H9Zm4 0v7h2v-7h-2Zm4 0v7h2v-7h-2ZM3 20v2h18v-2H3Z" />
              </svg>
            </div>

            <div>
              <h1 className="text-xl font-bold text-slate-950">
                Bank of Georgia
              </h1>
              <p className="text-sm font-medium text-slate-500">
                Customer Banking Dashboard
              </p>
            </div>
          </div>

          <Button variant="outline" onClick={() => setShowLogout(true)}>
            Sign Out
          </Button>
        </div>
      </header>

      <main className="mx-auto max-w-6xl px-6 py-8">
        {error && (
          <div className="mb-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">
            {error}
          </div>
        )}

        <section className="mb-8 overflow-hidden rounded-3xl bg-slate-950 shadow-2xl shadow-slate-300/70">
          <div className="grid gap-8 p-8 lg:grid-cols-[1fr_320px] lg:p-10">
            <div>
              <p className="text-sm font-semibold uppercase tracking-[0.22em] text-blue-300">
                My Profile
              </p>

              <h2 className="mt-4 text-4xl font-bold tracking-tight text-white">
                Welcome, {customerName}
              </h2>

              <p className="mt-4 max-w-2xl text-base leading-7 text-slate-300">
                View your customer information and update eligible profile details.
              </p>

              <div className="mt-8 grid gap-4 sm:grid-cols-2">
                <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
                  <p className="text-xs font-semibold uppercase tracking-wider text-slate-400">
                    Customer ID
                  </p>
                  <p className="mt-2 truncate text-lg font-bold text-white">
                    {customerId || "Not available"}
                  </p>
                </div>

                <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
                  <p className="text-xs font-semibold uppercase tracking-wider text-slate-400">
                    Status
                  </p>
                  <p className="mt-2 text-lg font-bold text-emerald-300">
                    {user?.status || "ACTIVE"}
                  </p>
                </div>
              </div>
            </div>

            <aside className="rounded-3xl border border-white/10 bg-white p-6 text-slate-900">
              <div className="flex items-center gap-4">
                <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-blue-900 text-2xl font-bold text-white">
                  {customerInitial}
                </div>

                <div className="min-w-0">
                  <p className="truncate text-lg font-bold text-slate-950">
                    {customerName}
                  </p>
                  <p className="truncate text-sm font-medium text-slate-500">
                    {user?.email || user?.phone || "Customer profile"}
                  </p>
                </div>
              </div>

              <div className="mt-6 grid gap-3">
                <Button fullWidth onClick={openEditProfile}>
                  Update Profile
                </Button>

                <Button fullWidth variant="outline" onClick={refreshProfile}>
                  Refresh Details
                </Button>
              </div>
            </aside>
          </div>
        </section>

        <Card>
          <div className="mb-6 flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
            <div>
              <h3 className="text-2xl font-bold text-slate-950">
                Customer Details
              </h3>
              <p className="mt-1 text-sm font-medium text-slate-500">
                Your registered banking profile information.
              </p>
            </div>
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <DetailItem label="First Name" value={user?.firstName} />
            <DetailItem label="Last Name" value={user?.lastName} />
            <DetailItem label="Username" value={user?.username || user?.loginId} />
            <DetailItem label="Email" value={user?.email} />
            <DetailItem label="Phone" value={user?.phone || user?.phoneNumber} />
            <DetailItem label="Date of Birth" value={formatDate(user?.dateOfBirth)} />
            <DetailItem label="Status" value={user?.status || "ACTIVE"} />
            <DetailItem label="Created At" value={formatDate(user?.createdAt)} />
          </div>
        </Card>
      </main>

      {showEdit && (
        <div className="fixed inset-0 z-40 flex items-center justify-center bg-slate-950/70 px-4 backdrop-blur-sm">
          <Card className="w-full max-w-lg">
            <div className="mb-6">
              <h3 className="text-2xl font-bold text-slate-950">
                Update Profile
              </h3>
              <p className="mt-2 text-sm text-slate-500">
                Only basic name fields can be updated here. Email, phone number, username, date of birth, and status are bank-verified fields.
              </p>
            </div>

            <form onSubmit={handleEditSubmit} className="space-y-5">
              <div className="grid gap-5 sm:grid-cols-2">
                <Input
                  label="First Name"
                  id="firstName"
                  value={editForm.firstName}
                  onChange={(e) => updateEditField("firstName", e.target.value)}
                  required
                />

                <Input
                  label="Last Name"
                  id="lastName"
                  value={editForm.lastName}
                  onChange={(e) => updateEditField("lastName", e.target.value)}
                  required
                />
              </div>

              <ReadOnlyField label="Email" value={user?.email} />
              <ReadOnlyField label="Phone" value={user?.phone || user?.phoneNumber} />
              <ReadOnlyField label="Date of Birth" value={formatDate(user?.dateOfBirth)} />

              <div className="grid gap-3 pt-2 sm:grid-cols-2">
                <Button
                  type="button"
                  variant="outline"
                  fullWidth
                  onClick={() => {
                    setShowEdit(false);
                    setError("");
                  }}
                  disabled={saving}
                >
                  Cancel
                </Button>

                <Button type="submit" fullWidth loading={saving}>
                  Save Changes
                </Button>
              </div>
            </form>
          </Card>
        </div>
      )}

      <ConfirmModal
        open={showSaveConfirm}
        title="Save profile changes?"
        message="This will update your customer profile name."
        confirmLabel="Save Changes"
        cancelLabel="Review"
        onConfirm={handleSaveProfile}
        onCancel={() => setShowSaveConfirm(false)}
        loading={saving}
      />

      <ConfirmModal
        open={showLogout}
        title="Sign out?"
        message="You will be returned to the login screen."
        confirmLabel="Yes, sign out"
        cancelLabel="Stay"
        variant="danger"
        onConfirm={handleLogout}
        onCancel={() => setShowLogout(false)}
      />
    </div>
  );
}

// Displays one customer detail row.
function DetailItem({ label, value }) {
  return (
    <div className="rounded-2xl border border-slate-200 bg-slate-50 px-5 py-4">
      <p className="text-xs font-bold uppercase tracking-wider text-slate-500">
        {label}
      </p>
      <p className="mt-2 break-words text-base font-bold text-slate-950">
        {value || "Not available"}
      </p>
    </div>
  );
}

// Displays locked fields inside the profile update modal.
function ReadOnlyField({ label, value }) {
  return (
    <div>
      <label className="mb-2 block text-sm font-bold text-slate-800">
        {label}
      </label>
      <div className="flex h-12 items-center rounded-xl border border-slate-300 bg-slate-100 px-4 text-sm font-semibold text-slate-600">
        {value || "Not available"}
      </div>
    </div>
  );
}