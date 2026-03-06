const group = {
  id: "g1",
  name: "Roommates",
  members: [
    { id: "p1", name: "Alex" },
    { id: "p2", name: "Rina" },
    { id: "p3", name: "Sam" },
    { id: "p4", name: "Jo" },
  ],
  expenses: [
    {
      id: "e1",
      title: "Groceries",
      amount: 120,
      paidBy: "p1",
      splitBetween: ["p1", "p2", "p3", "p4"],
      category: "FOOD",
      note: "Weekly shopping",
      recurringDays: null,
    },
    {
      id: "e2",
      title: "Uber",
      amount: 36,
      paidBy: "p2",
      splitBetween: ["p1", "p2", "p3"],
      category: "TRANSPORT",
      note: "",
      recurringDays: null,
    },
    {
      id: "e3",
      title: "Rent",
      amount: 800,
      paidBy: "p3",
      splitBetween: ["p1", "p2", "p3", "p4"],
      category: "RENT",
      note: "",
      recurringDays: 30,
    },
  ],
};

const money = (value) => `$${value.toFixed(2)}`;
const personById = (id) => group.members.find((m) => m.id === id);

function calculateBalances() {
  const balances = Object.fromEntries(group.members.map((m) => [m.id, 0]));

  group.expenses.forEach((expense) => {
    const splitAmount = expense.amount / expense.splitBetween.length;
    balances[expense.paidBy] += expense.amount;
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
  let d = 0;
  let c = 0;

  while (d < debtors.length && c < creditors.length) {
    const transfer = Math.min(debtors[d].amount, creditors[c].amount);
    settlements.push({
      from: debtors[d].personId,
      to: creditors[c].personId,
      amount: Math.round(transfer * 100) / 100,
    });

    debtors[d].amount -= transfer;
    creditors[c].amount -= transfer;

    if (Math.abs(debtors[d].amount) < 0.01) d += 1;
    if (Math.abs(creditors[c].amount) < 0.01) c += 1;
  }

  return settlements;
}

function renderDashboard() {
  const balances = calculateBalances();
  const el = document.getElementById("dashboard");
  const rows = Object.entries(balances)
    .map(([id, amount]) => {
      const label = amount >= 0 ? "gets" : "owes";
      return `<div class="row"><strong>${personById(id).name}</strong><span>${label} ${money(
        Math.abs(amount)
      )}</span></div>`;
    })
    .join("");

  el.innerHTML = `<h2>Group: ${group.name}</h2><p class="small">Who owes what</p>${rows}`;
}

function renderExpenses() {
  const el = document.getElementById("expenses");
  const rows = group.expenses
    .map((expense) => {
      const splitNames = expense.splitBetween.map((id) => personById(id).name).join(", ");
      return `<div class="row"><div><strong>${expense.title}</strong><div class="small">${expense.category} • Paid by ${
        personById(expense.paidBy).name
      }</div><div class="small">Split: ${splitNames}</div>${
        expense.note ? `<div class="small">Note: ${expense.note}</div>` : ""
      }${
        expense.recurringDays ? `<div class="small">Recurring every ${expense.recurringDays} days</div>` : ""
      }</div><span>${money(expense.amount)}</span></div>`;
    })
    .join("");

  el.innerHTML = `<h2>Expenses</h2>${rows || '<p class="small">No expenses yet.</p>'}`;
}

function renderSettlements() {
  const el = document.getElementById("settle");
  const settlements = simplifyDebts();
  const rows = settlements
    .map(
      (s) =>
        `<div class="row"><span><strong>${personById(s.from).name}</strong> → ${personById(
          s.to
        ).name}</span><strong>${money(s.amount)}</strong></div>`
    )
    .join("");

  el.innerHTML = `<h2>Simplified settlements</h2>${rows || '<p class="small">All settled up 🎉</p>'}`;
}

function renderInsights() {
  const el = document.getElementById("insights");
  const total = group.expenses.reduce((sum, e) => sum + e.amount, 0);
  const avg = group.expenses.length ? total / group.expenses.length : 0;
  const recurring = group.expenses.filter((e) => e.recurringDays).length;

  el.innerHTML = `
    <h2>Insights</h2>
    <div class="row"><span>Total tracked</span><strong>${money(total)}</strong></div>
    <div class="row"><span>Average expense</span><strong>${money(avg)}</strong></div>
    <div class="row"><span>Recurring expenses</span><strong>${recurring}</strong></div>
    <p class="small">Planned user-first upgrades: OCR receipts, reminder nudges, offline sync, multi-currency trip mode.</p>
  `;
}

function renderAll() {
  renderDashboard();
  renderExpenses();
  renderSettlements();
  renderInsights();
}

function setupTabs() {
  const tabs = document.querySelectorAll(".tab");
  tabs.forEach((btn) => {
    btn.addEventListener("click", () => {
      tabs.forEach((t) => t.classList.remove("is-active"));
      btn.classList.add("is-active");
      document.querySelectorAll(".panel").forEach((panel) => panel.classList.add("hidden"));
      document.getElementById(btn.dataset.tab).classList.remove("hidden");
    });
  });
}

function setupExpenseForm() {
  const form = document.getElementById("expense-form");
  const paidBy = form.elements.namedItem("paidBy");
  const splitBetween = form.elements.namedItem("splitBetween");

  group.members.forEach((m) => {
    paidBy.add(new Option(m.name, m.id));
    splitBetween.add(new Option(m.name, m.id, false, true));
  });

  form.addEventListener("submit", (e) => {
    e.preventDefault();
    const fd = new FormData(form);
    const selectedMembers = [...splitBetween.selectedOptions].map((opt) => opt.value);

    if (!selectedMembers.length) {
      window.alert("Select at least one member in 'Split between'.");
      return;
    }

    group.expenses.push({
      id: `e${group.expenses.length + 1}`,
      title: String(fd.get("title")),
      amount: Number(fd.get("amount")),
      paidBy: String(fd.get("paidBy")),
      splitBetween: selectedMembers,
      category: String(fd.get("category")),
      note: String(fd.get("note") || ""),
      recurringDays: fd.get("recurringDays") ? Number(fd.get("recurringDays")) : null,
    });

    form.reset();
    [...splitBetween.options].forEach((option) => {
      option.selected = true;
    });
    renderAll();
  });
}

setupTabs();
setupExpenseForm();
renderAll();
