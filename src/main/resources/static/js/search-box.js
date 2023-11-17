function searchCourses() {
    var input, filter, table, tr, td, i, txtValue;
    input = document.getElementById('courseSearch');
    filter = input.value.toUpperCase();
    table = document.querySelector('table');
    tr = table.getElementsByTagName('tr');

    for (i = 0; i < tr.length; i++) {
        td = tr[i].getElementsByTagName('td')[0]; // Assuming the search is based on the first column (CourseID)
        if (td) {
            txtValue = td.textContent || td.innerText;
            if (txtValue.toUpperCase().indexOf(filter) > -1) {
                tr[i].style.display = '';
            } else {
                tr[i].style.display = 'none';
            }
        }
    }
}
function resetSearch() {
    var input, table, tr, i;
    input = document.getElementById('courseSearch');
    table = document.querySelector('table');
    tr = table.getElementsByTagName('tr');

    // Clear the search input
    input.value = '';

    // Show all rows in the table
    for (i = 0; i < tr.length; i++) {
        tr[i].style.display = '';
    }
}

// Add an event listener to the reset button
document.addEventListener('DOMContentLoaded', function () {
    var resetButton = document.querySelector('.search-box button[type="reset"]');
    resetButton.addEventListener('click', resetSearch);
});