import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import {
  createProduct,
  getProducts,
  updateProduct,
  updateProductStatus,
} from "../services/api";
import Button from "../components/ui/Button";
import Card from "../components/ui/Card";
import ConfirmModal from "../components/ui/ConfirmModal";
import Input from "../components/ui/Input";

const productTypes = [
  "CHECKING_ACCOUNT",
  "SAVINGS_ACCOUNT",
  "CERTIFICATE_OF_DEPOSIT",
  "BUSINESS_CHECKING_ACCOUNT",
  "STUDENT_SAVINGS_ACCOUNT",
];

const emptyCreateForm = {
  productName: "",
  productType: "CHECKING_ACCOUNT",
  description: "",
  monthlyMaintenanceFee: "0.00",
  minimumBalance: "0.00",
  createdByEmployeeId: "emp001",
};

export default function ProductsPage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [showCreate, setShowCreate] = useState(false);
  const [createForm, setCreateForm] = useState(emptyCreateForm);

  const [editTarget, setEditTarget] = useState(null);
  const [editForm, setEditForm] = useState({});

  const [statusTarget, setStatusTarget] = useState(null);

  useEffect(() => {
    loadProducts();
  }, []);

  const productRows = useMemo(() => normalizeList(products), [products]);

  async function loadProducts() {
    setLoading(true);
    setError("");

    try {
      const { data } = await getProducts();
      setProducts(data);
    } catch (err) {
      setError(err.message || "Failed to load products.");
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

  function validateProductForm(form, creating) {
    if (!form.productName?.trim()) {
      return "Product name is required.";
    }

    if (creating && !form.productType?.trim()) {
      return "Product type is required.";
    }

    if (Number(form.monthlyMaintenanceFee) < 0) {
      return "Monthly fee cannot be negative.";
    }

    if (Number(form.minimumBalance) < 0) {
      return "Minimum balance cannot be negative.";
    }

    return "";
  }

  async function handleCreateProduct(event) {
    event.preventDefault();

    const validationMessage = validateProductForm(createForm, true);

    if (validationMessage) {
      setError(validationMessage);
      return;
    }

    setSaving(true);
    setError("");

    try {
      await createProduct({
        ...createForm,
        productName: createForm.productName.trim(),
        description: createForm.description.trim(),
        monthlyMaintenanceFee: Number(createForm.monthlyMaintenanceFee),
        minimumBalance: Number(createForm.minimumBalance),
      });

      setCreateForm(emptyCreateForm);
      setShowCreate(false);
      await loadProducts();
    } catch (err) {
      setError(err.message || "Failed to create product.");
    } finally {
      setSaving(false);
    }
  }

  function openEdit(product) {
    setEditTarget(product);
    setEditForm({
      productName: product.productName || "",
      description: product.description || "",
      monthlyMaintenanceFee: product.monthlyMaintenanceFee ?? "0.00",
      minimumBalance: product.minimumBalance ?? "0.00",
      updatedByEmployeeId: product.updatedByEmployeeId || "emp001",
    });
  }

  async function handleUpdateProduct(event) {
    event.preventDefault();

    if (!editTarget?.id) {
      setError("Product ID is missing.");
      return;
    }

    const validationMessage = validateProductForm(editForm, false);

    if (validationMessage) {
      setError(validationMessage);
      return;
    }

    setSaving(true);
    setError("");

    try {
      await updateProduct(editTarget.id, {
        ...editForm,
        productName: editForm.productName.trim(),
        description: editForm.description.trim(),
        monthlyMaintenanceFee: Number(editForm.monthlyMaintenanceFee),
        minimumBalance: Number(editForm.minimumBalance),
      });

      setEditTarget(null);
      await loadProducts();
    } catch (err) {
      setError(err.message || "Failed to update product.");
    } finally {
      setSaving(false);
    }
  }

  async function handleStatusUpdate() {
    if (!statusTarget?.id) {
      setError("Product ID is missing.");
      setStatusTarget(null);
      return;
    }

    setSaving(true);
    setError("");

    try {
      await updateProductStatus(statusTarget.id, {
        status: statusTarget.status,
        updatedByEmployeeId: "emp001",
      });

      setStatusTarget(null);
      await loadProducts();
    } catch (err) {
      setError(err.message || "Failed to update product status.");
    } finally {
      setSaving(false);
    }
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
          to="/employees"
          className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-bold text-slate-700 hover:bg-slate-50"
        >
          Employees
        </Link>
      </div>

      <div className="mx-auto max-w-7xl">
        <div className="mb-6 flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
          <div>
            <p className="text-sm font-semibold uppercase tracking-[0.2em] text-amber-600">
              Employee Portal
            </p>

            <h1 className="mt-2 text-3xl font-bold text-staff-950">
              Products
            </h1>

            <p className="mt-2 text-sm font-medium text-slate-500">
              Manage banking product templates used when opening customer accounts.
            </p>
          </div>

          <div className="flex gap-3">
            <Button variant="outline" onClick={loadProducts}>
              Refresh
            </Button>

            <Button onClick={() => setShowCreate(true)}>
              Create Product
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
              Loading products...
            </div>
          </Card>
        ) : productRows.length === 0 ? (
          <Card>
            <div className="flex h-56 flex-col items-center justify-center text-center">
              <h2 className="text-xl font-bold text-staff-950">
                No products found
              </h2>
              <p className="mt-2 text-sm text-slate-500">
                Created banking products will appear here.
              </p>
            </div>
          </Card>
        ) : (
          <Card padding={false} className="overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full min-w-[1000px] text-sm">
                <thead>
                  <tr className="border-b border-slate-200 bg-slate-50">
                    {[
                      "Product",
                      "Type",
                      "Monthly Fee",
                      "Minimum Balance",
                      "Status",
                      "Actions",
                    ].map((header) => (
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
                  {productRows.map((product) => {
                    const status = product.status || "ACTIVE";
                    const nextStatus =
                      status === "ACTIVE" ? "INACTIVE" : "ACTIVE";

                    return (
                      <tr key={product.id} className="bg-white hover:bg-slate-50">
                        <td className="px-5 py-4">
                          <div className="font-bold text-staff-950">
                            {product.productName}
                          </div>
                          <div className="mt-1 text-xs font-medium text-slate-500">
                            {product.description || "No description"}
                          </div>
                        </td>

                        <td className="px-5 py-4 font-mono text-xs font-semibold text-slate-600">
                          {product.productType}
                        </td>

                        <td className="px-5 py-4 font-bold text-slate-700">
                          ${product.monthlyMaintenanceFee}
                        </td>

                        <td className="px-5 py-4 font-bold text-slate-700">
                          ${product.minimumBalance}
                        </td>

                        <td className="px-5 py-4">
                          <StatusBadge status={status} />
                        </td>

                        <td className="px-5 py-4">
                          <div className="flex flex-wrap gap-2">
                            <Button
                              variant="outline"
                              onClick={() => openEdit(product)}
                              className="h-9 px-3 text-xs"
                            >
                              Edit
                            </Button>

                            <Button
                              variant={status === "ACTIVE" ? "danger" : "primary"}
                              onClick={() =>
                                setStatusTarget({
                                  id: product.id,
                                  name: product.productName,
                                  status: nextStatus,
                                })
                              }
                              className="h-9 px-3 text-xs"
                            >
                              {status === "ACTIVE" ? "Disable" : "Enable"}
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
        <ModalCard title="Create product" onClose={() => setShowCreate(false)}>
          <form onSubmit={handleCreateProduct} className="space-y-5">
            <Input
              label="Product name"
              value={createForm.productName}
              onChange={(event) =>
                updateCreateField("productName", event.target.value)
              }
            />

            <SelectField
              label="Product type"
              value={createForm.productType}
              onChange={(value) => updateCreateField("productType", value)}
              options={productTypes}
            />

            <Input
              label="Description"
              value={createForm.description}
              onChange={(event) =>
                updateCreateField("description", event.target.value)
              }
            />

            <div className="grid gap-5 sm:grid-cols-2">
              <Input
                label="Monthly fee"
                type="number"
                value={createForm.monthlyMaintenanceFee}
                onChange={(event) =>
                  updateCreateField("monthlyMaintenanceFee", event.target.value)
                }
              />

              <Input
                label="Minimum balance"
                type="number"
                value={createForm.minimumBalance}
                onChange={(event) =>
                  updateCreateField("minimumBalance", event.target.value)
                }
              />
            </div>

            <Input
              label="Created by employee ID"
              value={createForm.createdByEmployeeId}
              onChange={(event) =>
                updateCreateField("createdByEmployeeId", event.target.value)
              }
            />

            <div className="grid gap-3 pt-2 sm:grid-cols-2">
              <Button
                type="button"
                variant="outline"
                fullWidth
                onClick={() => setShowCreate(false)}
              >
                Cancel
              </Button>

              <Button type="submit" fullWidth loading={saving}>
                Create Product
              </Button>
            </div>
          </form>
        </ModalCard>
      )}

      {editTarget && (
        <ModalCard title="Edit product" onClose={() => setEditTarget(null)}>
          <form onSubmit={handleUpdateProduct} className="space-y-5">
            <Input
              label="Product name"
              value={editForm.productName}
              onChange={(event) =>
                updateEditField("productName", event.target.value)
              }
            />

            <div>
              <label className="mb-2 block text-sm font-bold text-slate-800">
                Product type
              </label>
              <div className="flex h-12 items-center rounded-xl border border-slate-300 bg-slate-100 px-4 text-sm font-semibold text-slate-600">
                {editTarget.productType}
              </div>
            </div>

            <Input
              label="Description"
              value={editForm.description}
              onChange={(event) =>
                updateEditField("description", event.target.value)
              }
            />

            <div className="grid gap-5 sm:grid-cols-2">
              <Input
                label="Monthly fee"
                type="number"
                value={editForm.monthlyMaintenanceFee}
                onChange={(event) =>
                  updateEditField("monthlyMaintenanceFee", event.target.value)
                }
              />

              <Input
                label="Minimum balance"
                type="number"
                value={editForm.minimumBalance}
                onChange={(event) =>
                  updateEditField("minimumBalance", event.target.value)
                }
              />
            </div>

            <Input
              label="Updated by employee ID"
              value={editForm.updatedByEmployeeId}
              onChange={(event) =>
                updateEditField("updatedByEmployeeId", event.target.value)
              }
            />

            <div className="grid gap-3 pt-2 sm:grid-cols-2">
              <Button
                type="button"
                variant="outline"
                fullWidth
                onClick={() => setEditTarget(null)}
              >
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
        open={Boolean(statusTarget)}
        title={`${statusTarget?.status === "ACTIVE" ? "Enable" : "Disable"} product?`}
        message={`Set ${statusTarget?.name} to ${statusTarget?.status}?`}
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
      <span
        className={`h-2 w-2 rounded-full ${
          active ? "bg-emerald-600" : "bg-red-600"
        }`}
      />
      {status}
    </span>
  );
}

function ModalCard({ title, children, onClose }) {
  return (
    <div className="fixed inset-0 z-40 flex items-center justify-center bg-slate-950/70 px-4 py-8 backdrop-blur-sm">
      <Card className="max-h-[90vh] w-full max-w-2xl overflow-y-auto">
        <div className="mb-6 flex items-start justify-between gap-4">
          <div>
            <h3 className="text-2xl font-bold text-staff-950">{title}</h3>
            <p className="mt-2 text-sm text-slate-500">
              Product changes affect future account opening rules.
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