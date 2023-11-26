document.addEventListener('DOMContentLoaded', function () {

    var addStudentModal = new bootstrap.Modal(document.getElementById('addStudentModal'));
    var dataTable = $('#datatable').DataTable();

    document.getElementById('addStudentBtn').addEventListener('click', function () {
        addStudentModal.show();
    });

    document.getElementById('addStudentForm').addEventListener('submit', function (event) {
        event.preventDefault();

        var studentPassword = document.getElementById('studentPassword').value;
        var studentName = document.getElementById('studentName').value;
        var studentProgram = document.getElementById('studentProgram').value;
        var studentDepartment = document.getElementById('studentDepartment').value;
        var studentSchool = document.getElementById('studentSchool').value;

        // AJAX request
        $.ajax({
            url: '/add-student',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                password: studentPassword,
                name: studentName,
                program: studentProgram,
                department: studentDepartment,
                school: studentSchool
            }),
            success: function (response) {
                if (response.success) {
                    addStudentModal.hide();
                    alert('Add Student succeed.');
                    location.reload();
                    // Clear the form fields
                    document.getElementById('studentName').value = '';
                    document.getElementById('studentProgram').value = '';
                    document.getElementById('studentPassword').value = '';
                    document.getElementById('studentDepartment').value = '';
                    document.getElementById('studentSchool').value = '';
                } else {
                    console.error('Error adding student:', response.message);
                    alert('Student with the same name already exists.');
                    // Clear the form fields
                    document.getElementById('studentName').value = '';
                    document.getElementById('studentProgram').value = '';
                    document.getElementById('studentPassword').value = '';
                    document.getElementById('studentDepartment').value = '';
                    document.getElementById('studentSchool').value = '';
                }
            },
            error: function (error) {
                console.error('Error adding student:', error);
                alert('An error occurred while adding the student.');
            }
        });
    });

    $('#datatable').on('click', '.btnDelete', function (e) {
        e.preventDefault();
    
        var confirmation = confirm("Are you sure you want to delete this student?");
    
        if (confirmation) {
            var row = $(this).closest('tr');
            var studentId = row.find('td:first-child').text();
    
            $.ajax({
                url: '/delete-student/' + studentId,
                method: 'GET',
                success: function (response) {
                    if (response.success) {
                        dataTable.row(row).remove().draw(false);
                        alert('Delete Student succeed.');
                        // updateLecturerTable(response);
                        location.reload();
                    } else {
                        alert(response.message);
                    }
                },
                error: function (error) {
                    console.error('Error deleting student:', error);
                    alert('Error deleting student. Please try again.');
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

    addStudentModal._element.addEventListener('hidden.bs.modal', function () {
        document.getElementById('studentName').value = '';
    });
    $('#datatable').on('click', '.btnEdit', function (e) {
        e.preventDefault();
        var row = $(this).closest('tr');
        var studentId = row.find('td:first-child').text();
        var currentStudentName = row.find('td:nth-child(2)').text();
        var currentStudentDepartment = row.find('td:nth-child(3)').text();
        var currentStudentSchool = row.find('td:nth-child(4)').text();

        showEditModal(studentId, currentStudentName, currentStudentDepartment, currentStudentSchool);
    });

    $('#datatable').on('click', '.btnSCH', function (e) {
        e.preventDefault();
        var row = $(this).closest('tr');
        var studentId = row.find('td:first-child').text();
        var currentStudentName = row.find('td:nth-child(2)').text();

        window.location.href = '/manage-student/' + studentId + '/schedule';
    });


    function showEditModal(studentId, currentStudentName, currentStudentDepartment, currentStudentSchool) {
        var editStudentModal = new bootstrap.Modal(document.getElementById('editStudentModal'));
        var editStudentForm = document.getElementById('editStudentForm');
        var editStudentNameInput = document.getElementById('editStudentName');
        var editStudentDepartmentInput = document.getElementById('editStudentDepartment');
        var editStudentSchoolInput = document.getElementById('editStudentSchool');


        if (!editStudentForm || !editStudentNameInput) {
            console.error('Required elements not found.');
            return;
        }

        editStudentNameInput.value = currentStudentName;
        editStudentDepartmentInput.value = currentStudentDepartment;
        editStudentSchoolInput.value = currentStudentSchool;

        editStudentModal.show();

        editStudentForm.addEventListener('submit', function (event) {
            event.preventDefault();

        var editedStudentName = document.getElementById('editStudentName').value;
        var editedStudentDepartment = document.getElementById('editStudentDepartment').value;
        var editedStudentSchool = document.getElementById('editStudentSchool').value;

                $.ajax({
                    url: '/update-student/' + studentId,
                    method: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        name: editedStudentName,
                        department: editedStudentDepartment,
                        school: editedStudentSchool
                    }),
                    success: function (response) {
                        if (response.success) {
                            editStudentModal.hide();
                            alert('Edit Student succeed.');
                            console.log(response.message);
                            location.reload();
                        } else {
                            alert(response.message);
                        }
                    },
                    error: function (error) {
                        console.log('Error');
                        alert('Student name already exists.');
                    }
                });
        });

        document.getElementById('cancelEditStudentBtn').addEventListener('click', function () {
            editStudentModal.hide();
            location.reload();
        });
    }
    
});
