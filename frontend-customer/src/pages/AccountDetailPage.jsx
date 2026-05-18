import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getAccountByAccountNumber } from "../services/api";
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

// Formats ISO timestamp strings to a readable date.
function formatDate(value) {
  if (!value) return "Not available";
  return String(value).split("T")[0];
}

export default function AccountDetailPage() {
  const { accountNumber } = useParams();
  const navigate = useNavigate();
  const { logoutUser } = useAuth();

  const [account, setAccount] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showLogout, setShowLogout] = useState(false);

  // Fetches the account by account number from the URL.
  useEffect(() => {
    async function fetchAccount() {
      try {
        const { data } = await getAccountByAccountNumber(accountNumber);
        setAccount(data);
      } catch (err) {
        setError(err.message || "Failed to load account.");
      } finally {
        setLoading(false);
      }
    }

    fetchAccount();
  }, [accountNumber]);

  function handleLogout() {
    logoutUser();
    navigate("/login", { replace: true });
  }

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-100 text-sm font-semibold text-slate-600">
        Loading account...
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
                Account Details
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <Button variant="ghost" onClick={() => navigate("/accounts")}>
              ← Accounts
            </Button>

            <Button variant="outline" onClick={() => setShowLogout(true)}>
              Sign Out
            </Button>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-6xl px-6 py-8 space-y-6">
        {error && (
          <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">
            {error}
          </div>
        )}

        {account && (
          <>
            {/* Hero section */}
            <section className="overflow-hidden rounded-3xl bg-slate-950 shadow-2xl shadow-slate-300/70">
              <div className="p-8 lg:p-10">
                <p className="text-sm font-semibold uppercase tracking-[0.22em] text-blue-300">
                  {formatEnum(account.productType)}
                </p>

                <h2 className="mt-3 text-4xl font-bold tracking-tight text-white">
                  {formatCurrency(account.balance)}
                </h2>

                <p className="mt-1 text-sm font-medium text-slate-400">
                  Current balance
                </p>

                <div className="mt-8 grid gap-4 sm:grid-cols-3">
                  <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
                    <p className="text-xs font-semibold uppercase tracking-wider text-slate-400">
                      Account Number
                    </p>
                    <p className="mt-2 text-lg font-bold text-white">
                      {account.accountNumber}
                    </p>
                  </div>

                  <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
                    <p className="text-xs font-semibold uppercase tracking-wider text-slate-400">
                      Status
                    </p>
                    <span
                      className={`mt-2 inline-block rounded-full px-2.5 py-0.5 text-sm font-bold ${accountStatusClasses(account.status)}`}
                    >
                      {account.status}
                    </span>
                  </div>

                  <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
                    <p className="text-xs font-semibold uppercase tracking-wider text-slate-400">
                      Opened
                    </p>
                    <p className="mt-2 text-lg font-bold text-white">
                      {formatDate(account.createdAt)}
                    </p>
                  </div>
                </div>
              </div>
            </section>

            {/* Account details */}
            <Card>
              <div className="mb-6">
                <h3 className="text-2xl font-bold text-slate-950">
                  Account Details
                </h3>
                <p className="mt-1 text-sm font-medium text-slate-500">
                  Full record for this account.
                </p>
              </div>

              <div className="grid gap-4 md:grid-cols-2">
                <DetailItem label="Account Number" value={account.accountNumber} />
                <DetailItem label="Product Type" value={formatEnum(account.productType)} />
                <DetailItem label="Balance" value={formatCurrency(account.balance)} />
                <DetailItem label="Status" value={account.status} />
                <DetailItem label="Opened By" value={account.openedByEmployeeId} />
                <DetailItem label="Last Updated By" value={account.updatedByEmployeeId} />
                <DetailItem label="Opened On" value={formatDate(account.createdAt)} />
                <DetailItem label="Last Updated" value={formatDate(account.updatedAt)} />
              </div>
            </Card>

            {/* Transaction history — placeholder for future implementation */}
            <Card>
              <div className="mb-6">
                <h3 className="text-2xl font-bold text-slate-950">
                  Transaction History
                </h3>
                <p className="mt-1 text-sm font-medium text-slate-500">
                  A full ledger of deposits and withdrawals for this account.
                </p>
              </div>

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
                      d="M3 7.5 7.5 3m0 0L12 7.5M7.5 3v13.5m13.5 0L16.5 21m0 0L12 16.5m4.5 4.5V7.5"
                    />
                  </svg>
                </div>
                <p className="mt-4 text-base font-bold text-slate-700">
                  Coming soon
                </p>
                <p className="mt-1 text-sm text-slate-500">
                  Transaction history will appear here once available.
                </p>
              </div>
            </Card>
          </>
        )}
      </main>

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

// Displays one account detail row.
function DetailItem({ label, value }) {
  return (
    <div className="rounded-2xl border border-slate-200 bg-slate-50 px-5 py-4">
      <p className="text-xs font-bold uppercase tracking-wider text-slate-500">
        {label}
      </p>
      <p className="mt-2 text-base font-bold text-slate-950">
        {value || "Not available"}
      </p>
    </div>
  );
}
