document.addEventListener('DOMContentLoaded', function () {

    var addLecturerModal = new bootstrap.Modal(document.getElementById('addLecturerModal'));
    var dataTable = $('#datatable').DataTable();

    document.getElementById('addLecturerBtn').addEventListener('click', function () {
        addLecturerModal.show();
    });

    document.getElementById('addLecturerForm').addEventListener('submit', function (event) {
        event.preventDefault();

        var lecturerPassword = document.getElementById('lecturerPassword').value;
        var lecturerName = document.getElementById('lecturerName').value;
        var lecturerDepartment = document.getElementById('lecturerDepartment').value;
        var lecturerSchool = document.getElementById('lecturerSchool').value;

        // AJAX request
        $.ajax({
            url: '/add-lecturer',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                password: lecturerPassword,
                name: lecturerName,
                department: lecturerDepartment,
                school: lecturerSchool
            }),
            success: function (response) {
                if (response.success) {
                    addLecturerModal.hide();
                    alert('Add Lecturer succeed.');
                    location.reload();
                    // Clear the form fields
                    document.getElementById('lecturerName').value = '';
                    document.getElementById('lecturerPassword').value = '';
                    document.getElementById('lecturerDepartment').value = '';
                    document.getElementById('lecturerSchool').value = '';
                } else {
                    console.error('Error adding lecturer:', response.message);
                    alert('Lecturer with the same name already exists.');
                    // Clear the form fields
                    document.getElementById('lecturerName').value = '';
                    document.getElementById('lecturerPassword').value = '';
                    document.getElementById('lecturerDepartment').value = '';
                    document.getElementById('lecturerSchool').value = '';
                }
            },
            error: function (error) {
                console.error('Error adding lecturer:', error);
                alert('An error occurred while adding the lecturer.');
            }
        });
    });

    $('#datatable').on('click', '.btnDelete', function (e) {
        e.preventDefault();
    
        var confirmation = confirm("Are you sure you want to delete this lecturer?");
    
        if (confirmation) {
            var row = $(this).closest('tr');
            var lecturerId = row.find('td:first-child').text();
    
            $.ajax({
                url: '/delete-lecturer/' + lecturerId,
                method: 'GET',
                success: function (response) {
                    if (response.success) {
                        dataTable.row(row).remove().draw(false);
                        alert('Delete Lecturer succeed.');
                        // updateLecturerTable(response);
                        location.reload();
                    } else {
                        alert(response.message);
                    }
                },
                error: function (error) {
                    console.error('Error deleting lecturer:', error);
                    alert('Error deleting lecturer. Please try again.');
                }
            });
        }
    });

    // function updateLecturerTable(response) {
    //     document.getElementById('lecturerName').value = '';
    //     document.getElementById('editLecturerName').value = '';
    //     dataTable.clear();
    
    //     // Add new rows without clearing existing ones
    //     response.lecturers.forEach(function (lecturer) {
    //         dataTable.row.add([
    //             lecturer.id,
    //             lecturer.name,
    //             '<a class="btnSCH" href="#" data-lecturer-id="' + lecturer.id + '"><i class="fas fa-book" title="Schedule"></i></a>' +
    //             '<a class="btnEdit" href="#" data-lecturer-id="' + lecturer.id + '"><i class="fas fa-pencil-alt" title="Edit"></i></a>' +
    //             '<a class="btnDelete" href="#" data-lecturer-id="' + lecturer.id + '"><i class="fas fa-trash-alt" title="Delete"></i></a>'
    //         ]);
    //     });
    
    //     dataTable.draw(false);
    // }

    addLecturerModal._element.addEventListener('hidden.bs.modal', function () {
        document.getElementById('lecturerName').value = '';
    });
    $('#datatable').on('click', '.btnEdit', function (e) {
        e.preventDefault();
        var row = $(this).closest('tr');
        var lecturerId = row.find('td:first-child').text();
        var currentLecturerName = row.find('td:nth-child(2)').text();
        var currentLecturerDepartment = row.find('td:nth-child(3)').text();
        var currentLecturerSchool = row.find('td:nth-child(4)').text();

        showEditModal(lecturerId, currentLecturerName, currentLecturerDepartment, currentLecturerSchool);
    });

    $('#datatable').on('click', '.btnSCH', function (e) {
        e.preventDefault();
        var row = $(this).closest('tr');
        var lecturerId = row.find('td:first-child').text();
        var currentLecturerName = row.find('td:nth-child(2)').text();

        window.location.href = '/manage-lecturer-schedule/' + lecturerId;
    });


    function showEditModal(lecturerId, currentLecturerName, currentLecturerDepartment, currentLecturerSchool) {
        var editLecturerModal = new bootstrap.Modal(document.getElementById('editLecturerModal'));
        var editLecturerForm = document.getElementById('editLecturerForm');
        var editLecturerNameInput = document.getElementById('editLecturerName');
        var editLecturerDepartmentInput = document.getElementById('editLecturerDepartment');
        var editLecturerSchoolInput = document.getElementById('editLecturerSchool');


        if (!editLecturerForm || !editLecturerNameInput) {
            console.error('Required elements not found.');
            return;
        }

        editLecturerNameInput.value = currentLecturerName;
        editLecturerDepartmentInput.value = currentLecturerDepartment;
        editLecturerSchoolInput.value = currentLecturerSchool;

        editLecturerModal.show();

        editLecturerForm.addEventListener('submit', function (event) {
            event.preventDefault();

        var editedLecturerName = document.getElementById('editLecturerName').value;
        var editedLecturerDepartment = document.getElementById('editLecturerDepartment').value;
        var editedLecturerSchool = document.getElementById('editLecturerSchool').value;

                $.ajax({
                    url: '/update-lecturer/' + lecturerId,
                    method: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        name: editedLecturerName,
                        department: editedLecturerDepartment,
                        school: editedLecturerSchool
                    }),
                    success: function (response) {
                        if (response.success) {
                            editLecturerModal.hide();
                            alert('Edit Lecturer succeed.');
                            console.log(response.message);
                            // updateLecturerTable(response);
                            location.reload();
                        } else {
                            alert(response.message);
                        }
                    },
                    error: function (error) {
                        console.log('Error');
                        alert('Lecturer name already exists.');
                    }
                });
        });

        document.getElementById('cancelEditLecturerBtn').addEventListener('click', function () {
            editLecturerModal.hide();
            location.reload();
        });
    }
    
});
