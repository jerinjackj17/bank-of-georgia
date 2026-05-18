import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  getAllProducts,
  getAccountsByCustomerId,
  openAccount,
} from "../services/api";
import Button from "../components/ui/Button";
import Card from "../components/ui/Card";
import ConfirmModal from "../components/ui/ConfirmModal";
import { useAuth } from "../context/AuthContext";

// Formats enum strings like SAVINGS_ACCOUNT into Savings Account.
function formatEnum(value) {
  if (!value) return "Not available";
  return value
    .replace(/_/g, " ")
    .toLowerCase()
    .replace(/\b\w/g, (c) => c.toUpperCase());
}

// Formats a BigDecimal balance as USD currency.
function formatCurrency(value) {
  if (value == null) return "$0.00";
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
  }).format(value);
}

// Returns Tailwind classes for account status badges.
function accountStatusClasses(status) {
  if (status === "ACTIVE")
    return "text-emerald-700 bg-emerald-50 border border-emerald-200";
  if (status === "FROZEN")
    return "text-amber-700 bg-amber-50 border border-amber-200";
  if (status === "CLOSED")
    return "text-red-700 bg-red-50 border border-red-200";
  return "text-slate-700 bg-slate-50 border border-slate-200";
}

export default function AccountsPage() {
  const navigate = useNavigate();
  const { user, logoutUser } = useAuth();

  const [accounts, setAccounts] = useState([]);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // The product the customer has selected to open an account with.
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [opening, setOpening] = useState(false);

  const [showLogout, setShowLogout] = useState(false);

  const customerId = user?.id || user?.customerId || user?._id;

  // Fetches customer accounts and all products in parallel on mount.
  useEffect(() => {
    if (!customerId) {
      setError("Customer ID is missing. Please log in again.");
      setLoading(false);
      return;
    }

    async function fetchData() {
      try {
        const [accountsRes, productsRes] = await Promise.all([
          getAccountsByCustomerId(customerId),
          getAllProducts(),
        ]);
        setAccounts(accountsRes.data);
        setProducts(productsRes.data);
      } catch (err) {
        setError(err.message || "Failed to load accounts and products.");
      } finally {
        setLoading(false);
      }
    }

    fetchData();
  }, [customerId]);

  // Opens an account with the selected product, then appends it to the list.
  async function handleOpenAccount() {
    if (!selectedProduct) return;

    setOpening(true);

    try {
      const { data } = await openAccount(customerId, selectedProduct.id);
      setAccounts((prev) => [...prev, data]);
      setSelectedProduct(null);
    } catch (err) {
      setError(err.message || "Failed to open account.");
      setSelectedProduct(null);
    } finally {
      setOpening(false);
    }
  }

  function handleLogout() {
    logoutUser();
    navigate("/login", { replace: true });
  }

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-100 text-sm font-semibold text-slate-600">
        Loading accounts...
      </div>
    );
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
                My Accounts
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <Button variant="ghost" onClick={() => navigate("/dashboard")}>
              ← Dashboard
            </Button>

            <Button variant="outline" onClick={() => setShowLogout(true)}>
              Sign Out
            </Button>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-6xl px-6 py-8 space-y-8">
        {error && (
          <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">
            {error}
          </div>
        )}

        {/* My Accounts section */}
        <section>
          <div className="mb-5">
            <h2 className="text-2xl font-bold text-slate-950">My Accounts</h2>
            <p className="mt-1 text-sm font-medium text-slate-500">
              Click an account to view its details.
            </p>
          </div>

          {accounts.length === 0 ? (
            <Card>
              <div className="flex flex-col items-center py-10 text-center">
                <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-slate-100">
                  <svg
                    className="h-7 w-7 text-slate-400"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={1.5}
                      d="M2.25 8.25h19.5M2.25 9h19.5m-16.5 5.25h6m-6 2.25h3m-3.75 3h15a2.25 2.25 0 0 0 2.25-2.25V6.75A2.25 2.25 0 0 0 19.5 4.5h-15a2.25 2.25 0 0 0-2.25 2.25v10.5A2.25 2.25 0 0 0 4.5 19.5Z"
                    />
                  </svg>
                </div>
                <p className="mt-4 text-base font-bold text-slate-700">
                  No accounts yet
                </p>
                <p className="mt-1 text-sm text-slate-500">
                  Open your first account by selecting a product below.
                </p>
              </div>
            </Card>
          ) : (
            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
              {accounts.map((account) => (
                <Card
                  key={account.accountNumber}
                  onClick={() =>
                    navigate(`/accounts/${account.accountNumber}`)
                  }
                >
                  <div className="mb-4 flex items-start justify-between gap-2">
                    <p className="text-base font-bold text-slate-950">
                      {account.accountNumber}
                    </p>
                    <span
                      className={`rounded-full px-2.5 py-0.5 text-xs font-bold ${accountStatusClasses(account.status)}`}
                    >
                      {account.status}
                    </span>
                  </div>

                  <p className="text-xs font-semibold uppercase tracking-wider text-slate-400">
                    {formatEnum(account.productType)}
                  </p>

                  <p className="mt-3 text-2xl font-bold text-slate-950">
                    {formatCurrency(account.balance)}
                  </p>

                  <p className="mt-4 text-xs font-medium text-slate-400">
                    Opened {account.createdAt
                      ? String(account.createdAt).split("T")[0]
                      : "—"}
                  </p>
                </Card>
              ))}
            </div>
          )}
        </section>

        {/* Available Products section */}
        <section>
          <div className="mb-5">
            <h2 className="text-2xl font-bold text-slate-950">
              Available Products
            </h2>
            <p className="mt-1 text-sm font-medium text-slate-500">
              Select an active product to open a new account.
            </p>
          </div>

          {products.length === 0 ? (
            <Card>
              <p className="py-8 text-center text-sm font-medium text-slate-500">
                No products available at this time.
              </p>
            </Card>
          ) : (
            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
              {products.map((product) => {
                const isActive = product.status === "ACTIVE";

                return (
                  <div
                    key={product.id}
                    onClick={() => isActive && setSelectedProduct(product)}
                    className={`
                      rounded-3xl border bg-white p-6 shadow-lg shadow-slate-200/70 transition
                      ${isActive
                        ? "cursor-pointer border-slate-200 hover:-translate-y-0.5 hover:shadow-xl"
                        : "cursor-not-allowed border-slate-100 opacity-50"
                      }
                    `}
                  >
                    <div className="mb-3 flex items-start justify-between gap-2">
                      <p className="text-base font-bold text-slate-950">
                        {product.productName}
                      </p>
                      {!isActive && (
                        <span className="rounded-full border border-slate-200 bg-slate-100 px-2.5 py-0.5 text-xs font-bold text-slate-500">
                          Unavailable
                        </span>
                      )}
                    </div>

                    <p className="text-xs font-semibold uppercase tracking-wider text-slate-400">
                      {formatEnum(product.productType)}
                    </p>

                    {product.description && (
                      <p className="mt-3 text-sm leading-6 text-slate-600">
                        {product.description}
                      </p>
                    )}

                    <div className="mt-4 grid grid-cols-2 gap-3">
                      <div className="rounded-xl border border-slate-100 bg-slate-50 px-3 py-2">
                        <p className="text-xs font-semibold text-slate-400">
                          Monthly Fee
                        </p>
                        <p className="mt-0.5 text-sm font-bold text-slate-800">
                          {formatCurrency(product.monthlyMaintenanceFee)}
                        </p>
                      </div>
                      <div className="rounded-xl border border-slate-100 bg-slate-50 px-3 py-2">
                        <p className="text-xs font-semibold text-slate-400">
                          Min. Balance
                        </p>
                        <p className="mt-0.5 text-sm font-bold text-slate-800">
                          {formatCurrency(product.minimumBalance)}
                        </p>
                      </div>
                    </div>

                    {isActive && (
                      <p className="mt-4 text-center text-xs font-semibold text-blue-700">
                        Click to open account →
                      </p>
                    )}
                  </div>
                );
              })}
            </div>
          )}
        </section>
      </main>

      {/* Open account confirmation */}
      <ConfirmModal
        open={Boolean(selectedProduct)}
        title={`Open a ${selectedProduct?.productName ?? ""} account?`}
        message={`A new ${formatEnum(selectedProduct?.productType)} account will be opened for you with a starting balance of $0.00.`}
        confirmLabel="Open Account"
        cancelLabel="Cancel"
        onConfirm={handleOpenAccount}
        onCancel={() => setSelectedProduct(null)}
        loading={opening}
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
