function openSidebar() {
    document.getElementById("sidebar").classList.add("open");
    document.getElementById("overlay").style.display = "block";
    }
function closeSidebar() {
    document.getElementById("sidebar").classList.remove("open");
    document.getElementById("overlay").style.display = "none";
    }