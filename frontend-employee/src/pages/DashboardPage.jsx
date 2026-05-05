import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  getAllCustomers,
  getAllEmployees,
  getProducts,
} from "../services/api";
import Button from "../components/ui/Button";
import Card from "../components/ui/Card";
import ConfirmModal from "../components/ui/ConfirmModal";
import { useAuth } from "../context/AuthContext";

export default function DashboardPage() {
  const navigate = useNavigate();
  const { employee, user, logoutUser } = useAuth();

  const currentEmployee = employee || user;

  const [showLogout, setShowLogout] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [customers, setCustomers] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [products, setProducts] = useState([]);

  const employeeName = useMemo(() => {
    const fullName = `${currentEmployee?.firstName || ""} ${currentEmployee?.lastName || ""}`.trim();

    return fullName || currentEmployee?.username || "Employee";
  }, [currentEmployee]);

  useEffect(() => {
    loadDashboardData();
  }, []);

  async function loadDashboardData() {
    setLoading(true);
    setError("");

    try {
      const [customersResponse, employeesResponse, productsResponse] =
        await Promise.all([
          getAllCustomers(),
          getAllEmployees(),
          getProducts(),
        ]);

      setCustomers(normalizeList(customersResponse.data));
      setEmployees(normalizeList(employeesResponse.data));
      setProducts(normalizeList(productsResponse.data));
    } catch (err) {
      setError(err.message || "Failed to load dashboard data.");
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

  function handleLogout() {
    logoutUser();
    navigate("/login", { replace: true });
  }

  return (
    <div className="min-h-screen bg-slate-100 text-slate-900">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-5">
          <div>
            <h1 className="text-2xl font-bold text-slate-950">
              Bank of Georgia
            </h1>
            <p className="mt-1 text-sm font-semibold text-slate-500">
              Employee Portal
            </p>
          </div>

          <div className="flex items-center gap-3">
            <div className="hidden text-right sm:block">
              <p className="text-sm font-bold text-slate-900">
                {employeeName}
              </p>
              <p className="text-xs font-medium text-slate-500">
                {currentEmployee?.role || "Staff"}
              </p>
            </div>

            <Button variant="outline" onClick={() => setShowLogout(true)}>
              Sign Out
            </Button>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-6 py-8">
        {error && (
          <div className="mb-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">
            {error}
          </div>
        )}

        <section className="mb-6 rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
          <div className="flex flex-col justify-between gap-4 lg:flex-row lg:items-center">
            <div>
              <h2 className="text-2xl font-bold text-slate-950">
                Dashboard
              </h2>
              <p className="mt-1 text-sm font-medium text-slate-500">
                Signed in as {employeeName}
              </p>
            </div>

            <Button variant="outline" onClick={loadDashboardData} loading={loading}>
              Refresh
            </Button>
          </div>
        </section>

        <section className="grid gap-5 md:grid-cols-3">
          <StatCard title="Customers" value={customers.length} loading={loading} />
          <StatCard title="Employees" value={employees.length} loading={loading} />
          <StatCard title="Products" value={products.length} loading={loading} />
        </section>

        <section className="mt-8 grid gap-6 lg:grid-cols-3">
          <ActionCard title="Customers" to="/customers" />
          <ActionCard title="Employees" to="/employees" />
          <ActionCard title="Products" to="/products" />
        </section>
      </main>

      <ConfirmModal
        open={showLogout}
        title="Sign out?"
        message="You will be returned to the employee login screen."
        confirmLabel="Sign out"
        cancelLabel="Cancel"
        variant="danger"
        onConfirm={handleLogout}
        onCancel={() => setShowLogout(false)}
      />
    </div>
  );
}

function StatCard({ title, value, loading }) {
  return (
    <Card>
      <p className="text-sm font-bold uppercase tracking-wider text-slate-500">
        {title}
      </p>
      <p className="mt-3 text-4xl font-bold text-slate-950">
        {loading ? "..." : value}
      </p>
    </Card>
  );
}

function ActionCard({ title, to }) {
  return (
    <Card>
      <h3 className="text-xl font-bold text-slate-950">{title}</h3>
      <Link
        to={to}
        className="mt-6 inline-flex rounded-xl bg-slate-950 px-4 py-3 text-sm font-bold text-white hover:bg-slate-800"
      >
        Open
      </Link>
    </Card>
  );
}