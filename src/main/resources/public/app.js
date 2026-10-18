function createChart(canvasId, color) {
    const canvas = document.getElementById(canvasId);
    return new Chart(canvas, {
        type: "line",
        data: {
            labels: [],
            datasets: [{
                data: [],
                borderColor: color,
                backgroundColor: color + "33",
                fill: true,
                borderWidth: 2,
                pointRadius: 0,
                tension: 0.2
            }]
        },
        options: {
            animation: false,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false }
            },
            scales: {
                x: {
                    ticks: { color: "#8a90a0", maxTicksLimit: 6 },
                    grid: { color: "#262a33" }
                },
                y: {
                    beginAtZero: true,
                    ticks: { color: "#8a90a0" },
                    grid: { color: "#262a33" }
                }
            }
        }
    });
}

function updateChart(chart, labels, values) {
    chart.data.labels = labels;
    chart.data.datasets[0].data = values;
    chart.update();
}

const heapChart = createChart("heapChart", "#4f8cff");
const cpuChart = createChart("cpuChart", "#3ecf8e");
const threadsChart = createChart("threadsChart", "#f5a524");
const gcChart = createChart("gcChart", "#c678dd");
const latencyChart = createChart("latencyChart", "#ef5350");

async function refresh() {
    const status = document.getElementById("status");
    try {
        const response = await fetch("/api/metrics");
        const snapshots = await response.json();

        if (snapshots.length === 0) {
            return; 
        }

        const latest = snapshots[snapshots.length - 1];

        document.getElementById("heap").textContent = latest.heapUsedMb + " / " + latest.heapMaxMb + " MB";
        document.getElementById("cpu").textContent = latest.cpuPercent + " %";
        document.getElementById("threads").textContent = latest.threadCount;
        document.getElementById("gc").textContent = latest.gcCount;
        document.getElementById("latency").textContent = latest.latencyP50Ms + " / " + latest.latencyP95Ms + " ms";

        const labels = snapshots.map(s => new Date(s.timestamp).toLocaleTimeString());

        const gcPerSecond = snapshots.map((s, i) => i === 0 ? 0 : s.gcCount - snapshots[i - 1].gcCount);

        updateChart(heapChart, labels, snapshots.map(s => s.heapUsedMb));
        updateChart(cpuChart, labels, snapshots.map(s => s.cpuPercent));
        updateChart(threadsChart, labels, snapshots.map(s => s.threadCount));
        updateChart(gcChart, labels, gcPerSecond);
        updateChart(latencyChart, labels, snapshots.map(s => s.latencyP95Ms));
        
        const chaosResponse = await fetch("/api/chaos/status");
        showChaosState(await chaosResponse.json());

        status.textContent = "live";
        status.className = "status ok";
    } catch (error) {
        status.textContent = "offline";
        status.className = "status offline";
    }
}

function showChaosState(state) {
    const label = document.getElementById("chaosState");
    label.textContent = "leak: " + (state.leak ? "ON" : "off") + " | cpu: " + (state.cpu ? "ON" : "off");
    if (state.leak || state.cpu) {
        label.className = "chaos-state on";
    } else {
        label.className = "chaos-state";
    }
}

async function chaos(action) {
    const response = await fetch("/api/chaos/" + action, { method: "POST" });
    showChaosState(await response.json());
}

document.getElementById("leakStart").addEventListener("click", () => chaos("leak/start"));
document.getElementById("leakStop").addEventListener("click", () => chaos("leak/stop"));
document.getElementById("cpuStart").addEventListener("click", () => chaos("cpu/start"));
document.getElementById("cpuStop").addEventListener("click", () => chaos("cpu/stop"));
document.getElementById("gcButton").addEventListener("click", () => chaos("gc"));

refresh();
setInterval(refresh, 1000);