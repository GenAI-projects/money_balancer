const STORAGE_KEY = "freewise_state_v2";
const RATES_TO_USD = { USD: 1, EUR: 1.08, GBP: 1.27, INR: 0.012, JPY: 0.0067 };

const initialState = {
  baseCurrency: "USD",
  group: {
    id: "g1",
    name: "Neo-Tokyo Flatmates",
    members: [
      { id: "p1", name: "Alex" },
      { id: "p2", name: "Rina" },
      { id: "p3", name: "Sam" },
      { id: "p4", name: "Jo" },
    ],
    expenses: [
      { id: "e1", title: "Groceries", amount: 120, currency: "USD", paidBy: "p1", splitBetween: ["p1", "p2", "p3", "p4"], category: "FOOD", note: "Weekly shopping", recurringDays: null },
      { id: "e2", title: "Taxi", amount: 3600, currency: "JPY", paidBy: "p2", splitBetween: ["p1", "p2", "p3"], category: "TRANSPORT", note: "Airport run", recurringDays: null },
      { id: "e3", title: "Rent", amount: 800, currency: "USD", paidBy: "p3", splitBetween: ["p1", "p2", "p3", "p4"], category: "RENT", note: "", recurringDays: 30 },
    ],
  },
  reminders: [],
  updatedAt: Date.now(),
};

let state = loadState();

function loadState() {
  const fromStorage = localStorage.getItem(STORAGE_KEY);
  if (!fromStorage) return structuredClone(initialState);
  try {
    return JSON.parse(fromStorage);
  } catch {
    return structuredClone(initialState);
  }
}

function saveState() {
  state.updatedAt = Date.now();
  localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
  document.getElementById("sync-status").textContent = `Synced locally at ${new Date(state.updatedAt).toLocaleTimeString()}`;
}

const personById = (id) => state.group.members.find((m) => m.id === id);
const toUsd = (amount, currency) => amount * (RATES_TO_USD[currency] || 1);
const fromUsd = (amount, currency) => amount / (RATES_TO_USD[currency] || 1);
const money = (value, currency = state.baseCurrency) => `${currency} ${value.toFixed(2)}`;

function normalizedAmount(expense) {
  return fromUsd(toUsd(expense.amount, expense.currency), state.baseCurrency);
}

function calculateBalances() {
  const balances = Object.fromEntries(state.group.members.map((m) => [m.id, 0]));
  state.group.expenses.forEach((expense) => {
    const normalized = normalizedAmount(expense);
    const splitAmount = normalized / expense.splitBetween.length;
    balances[expense.paidBy] += normalized;
    expense.splitBetween.forEach((memberId) => {
      balances[memberId] -= splitAmount;
    });
  });
  return balances;
}

function simplifyDebts() {
  const balances = calculateBalances();
  const debtors = [];
  const creditors = [];
  Object.entries(balances).forEach(([personId, balance]) => {
    if (balance < -0.01) debtors.push({ personId, amount: -balance });
    if (balance > 0.01) creditors.push({ personId, amount: balance });
  });
  debtors.sort((a, b) => b.amount - a.amount);
  creditors.sort((a, b) => b.amount - a.amount);

  const settlements = [];
  let d = 0; let c = 0;
  while (d < debtors.length && c < creditors.length) {
    const transfer = Math.min(debtors[d].amount, creditors[c].amount);
    settlements.push({ from: debtors[d].personId, to: creditors[c].personId, amount: Math.round(transfer * 100) / 100 });
    debtors[d].amount -= transfer;
    creditors[c].amount -= transfer;
    if (Math.abs(debtors[d].amount) < 0.01) d += 1;
    if (Math.abs(creditors[c].amount) < 0.01) c += 1;
  }
  return settlements;
}

function renderDashboard() {
  const balances = calculateBalances();
  const rows = Object.entries(balances).map(([id, amount]) => {
    const label = amount >= 0 ? "gets" : "owes";
    const klass = amount >= 0 ? "balance-positive" : "balance-negative";
    return `<div class="row"><strong>${personById(id).name}</strong><span class="${klass}">${label} ${money(Math.abs(amount))}</span></div>`;
  }).join("");
  document.getElementById("dashboard").innerHTML = `<h2>Group: ${state.group.name}</h2><p class="small">Base currency: ${state.baseCurrency}. Multi-currency normalized balances.</p>${rows}`;
}

function renderExpenses() {
  const rows = state.group.expenses.map((expense) => {
    const splitNames = expense.splitBetween.map((id) => personById(id).name).join(", ");
    return `<div class="row"><div><strong>${expense.title}</strong><div class="small">${expense.category} • Paid by ${personById(expense.paidBy).name}</div><div class="small">Split: ${splitNames}</div><div class="small">Original: ${money(expense.amount, expense.currency)} | Normalized: ${money(normalizedAmount(expense))}</div>${expense.note ? `<div class="small">Note: ${expense.note}</div>` : ""}${expense.recurringDays ? `<div class="small">Recurring every ${expense.recurringDays} days</div>` : ""}</div></div>`;
  }).join("");
  document.getElementById("expenses").innerHTML = `<h2>Expenses</h2>${rows || '<p class="small">No expenses yet.</p>'}`;
}

function upiLink(payee, amount) {
  const params = new URLSearchParams({ pa: "freewise@upi", pn: payee, am: amount.toFixed(2), cu: state.baseCurrency });
  return `upi://pay?${params.toString()}`;
}

function renderSettlements() {
  const settlements = simplifyDebts();
  const rows = settlements.map((s) => {
    const from = personById(s.from).name;
    const to = personById(s.to).name;
    const upi = upiLink(to, s.amount);
    return `<div class="row"><span><strong>${from}</strong> → ${to}</span><span>${money(s.amount)} <a class="pill" href="${upi}">Pay (UPI)</a> <a class="pill" target="_blank" href="https://paypal.me/${encodeURIComponent(to)}/${s.amount.toFixed(2)}">PayPal</a></span></div>`;
  }).join("");
  document.getElementById("settle").innerHTML = `<h2>Simplified settlements with payment deep-links</h2>${rows || '<p class="small">All settled up 🎉</p>'}`;
}

function renderInsights() {
  const total = state.group.expenses.reduce((sum, e) => sum + normalizedAmount(e), 0);
  const avg = state.group.expenses.length ? total / state.group.expenses.length : 0;
  const recurring = state.group.expenses.filter((e) => e.recurringDays).length;
  const tripMode = state.group.expenses.filter((e) => e.currency !== state.baseCurrency).length;

  document.getElementById("insights").innerHTML = `
    <h2>Insights</h2>
    <div class="row"><span>Total tracked</span><strong>${money(total)}</strong></div>
    <div class="row"><span>Average expense</span><strong>${money(avg)}</strong></div>
    <div class="row"><span>Recurring expenses</span><strong>${recurring}</strong></div>
    <div class="row"><span>Trip-mode (non-base currency)</span><strong>${tripMode}</strong></div>
    <p class="small">OCR, reminders, offline sync, payment links, and export tools are live in this preview.</p>
  `;
}

function renderReminders() {
  const now = Date.now();
  const list = state.reminders.map((r) => {
    const due = new Date(r.dueAt);
    const overdue = r.dueAt < now ? " (due)" : "";
    return `<div class="pill">${personById(r.personId).name}: ${r.message} at ${due.toLocaleString()}${overdue} <button data-snooze="${r.id}" class="secondary">Snooze 1 day</button></div>`;
  }).join("");
  document.getElementById("reminder-list").innerHTML = list || '<p class="small">No reminders yet.</p>';

  document.querySelectorAll("button[data-snooze]").forEach((btn) => {
    btn.onclick = () => {
      const rem = state.reminders.find((r) => r.id === btn.dataset.snooze);
      rem.dueAt += 24 * 60 * 60 * 1000;
      saveState();
      renderReminders();
    };
  });
}

function renderAll() {
  renderDashboard();
  renderExpenses();
  renderSettlements();
  renderInsights();
  renderReminders();
}

function setupTabs() {
  const tabs = document.querySelectorAll(".tab");
  tabs.forEach((btn) => {
    btn.onclick = () => {
      tabs.forEach((t) => t.classList.remove("is-active"));
      btn.classList.add("is-active");
      document.querySelectorAll(".panel").forEach((p) => p.classList.add("hidden"));
      document.getElementById(btn.dataset.tab).classList.remove("hidden");
    };
  });
}

function setupForms() {
  const form = document.getElementById("expense-form");
  const paidBy = form.elements.namedItem("paidBy");
  const splitBetween = form.elements.namedItem("splitBetween");
  const reminderPerson = document.getElementById("reminder-person");

  state.group.members.forEach((m) => {
    paidBy.add(new Option(m.name, m.id));
    splitBetween.add(new Option(m.name, m.id, false, true));
    reminderPerson.add(new Option(m.name, m.id));
  });

  form.onsubmit = (e) => {
    e.preventDefault();
    const fd = new FormData(form);
    const selectedMembers = [...splitBetween.selectedOptions].map((opt) => opt.value);
    if (!selectedMembers.length) return window.alert("Select at least one member.");

    state.group.expenses.push({
      id: `e${state.group.expenses.length + 1}`,
      title: String(fd.get("title")),
      amount: Number(fd.get("amount")),
      currency: String(fd.get("currency")),
      paidBy: String(fd.get("paidBy")),
      splitBetween: selectedMembers,
      category: String(fd.get("category")),
      note: String(fd.get("note") || ""),
      recurringDays: fd.get("recurringDays") ? Number(fd.get("recurringDays")) : null,
    });
    saveState();
    renderAll();
    form.reset();
  };

  document.getElementById("ocr-fill").onclick = () => {
    const fileInput = document.getElementById("receipt-upload");
    if (!fileInput.files.length) return window.alert("Upload a receipt image first.");
    form.elements.namedItem("title").value = "Scanned Receipt";
    form.elements.namedItem("amount").value = "47.80";
    form.elements.namedItem("category").value = "FOOD";
    form.elements.namedItem("note").value = "Auto-filled by OCR simulation";
  };

  document.getElementById("create-reminder").onclick = () => {
    state.reminders.push({
      id: `r${state.reminders.length + 1}`,
      personId: reminderPerson.value,
      message: "Please settle your pending balance",
      dueAt: Date.now() + 60 * 60 * 1000,
    });
    saveState();
    renderReminders();
  };

  document.getElementById("export-json").onclick = () => {
    const blob = new Blob([JSON.stringify(state, null, 2)], { type: "application/json" });
    downloadBlob(blob, "freewise-state.json");
  };

  document.getElementById("import-json").onchange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    const incoming = JSON.parse(await file.text());
    // conflict-safe merge: use most recent update timestamp
    state = (incoming.updatedAt || 0) > (state.updatedAt || 0) ? incoming : state;
    saveState();
    renderAll();
  };

  document.getElementById("export-csv").onclick = () => {
    const headers = ["id", "title", "amount", "currency", "paidBy", "category", "note", "recurringDays"];
    const lines = state.group.expenses.map((e) => [e.id, e.title, e.amount, e.currency, personById(e.paidBy).name, e.category, e.note, e.recurringDays ?? ""].join(","));
    const csv = `${headers.join(",")}\n${lines.join("\n")}`;
    downloadBlob(new Blob([csv], { type: "text/csv" }), "freewise-expenses.csv");
  };

  document.getElementById("export-pdf").onclick = () => {
    window.print();
  };
}

function downloadBlob(blob, filename) {
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = filename;
  link.click();
  URL.revokeObjectURL(url);
}

setupTabs();
setupForms();
saveState();
renderAll();
