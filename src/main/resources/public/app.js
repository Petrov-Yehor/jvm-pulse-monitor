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

        status.textContent = "live";
        status.className = "status ok";
    } catch (error) {
        status.textContent = "offline";
        status.className = "status offline";
    }
}

refresh();
setInterval(refresh, 1000);