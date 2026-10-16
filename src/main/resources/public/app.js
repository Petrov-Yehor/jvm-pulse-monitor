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

        const labels = snapshots.map(s => new Date(s.timestamp).toLocaleTimeString());

        const gcPerSecond = snapshots.map((s, i) => i === 0 ? 0 : s.gcCount - snapshots[i - 1].gcCount);

        updateChart(heapChart, labels, snapshots.map(s => s.heapUsedMb));
        updateChart(cpuChart, labels, snapshots.map(s => s.cpuPercent));
        updateChart(threadsChart, labels, snapshots.map(s => s.threadCount));
        updateChart(gcChart, labels, gcPerSecond);

        status.textContent = "live";
        status.className = "status ok";
    } catch (error) {
        status.textContent = "offline";
        status.className = "status offline";
    }
}

refresh();
setInterval(refresh, 1000);