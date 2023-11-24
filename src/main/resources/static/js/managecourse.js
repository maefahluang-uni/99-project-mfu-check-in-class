document.addEventListener('DOMContentLoaded', function () {

    var addCourseModal = new bootstrap.Modal(document.getElementById('addCourseModal'));
    var dataTable = $('#datatable').DataTable();

    document.getElementById('addCourseBtn').addEventListener('click', function () {
        addCourseModal.show();
    });

    document.getElementById('addCourseForm').addEventListener('submit', function (event) {
        event.preventDefault();

        var courseNameInput = document.getElementById('courseName');
        var courseName = courseNameInput.value.trim();

        if (courseName !== '') {

            var duplicate = Array.from(document.querySelectorAll('#datatable tbody tr td:nth-child(2)'))
                .map(td => td.textContent.trim().toLowerCase())
                .includes(courseName.toLowerCase());

            if (!duplicate) {
                console.log('Adding course to the database...');

                $.ajax({
                    url: '/add-course',
                    method: 'POST',
                    data: { courseName: courseName },
                    success: function (response) {
                        updateCourseTable(response);
                        addCourseModal.hide();
                        alert('Add Course succeed.');
                        courseNameInput.value = '';
                    },
                    error: function (error) {
                        console.error('Error adding course:', error);
                        alert('Course with the same name already exists.');
                        courseNameInput.value = '';
                    }
                });
            } else {
                alert('Course with the same name already exists.');
                courseNameInput.value = '';
            }
        } else {
            alert('Please enter a course name.');
        }
    });

    $('#datatable').on('click', '.btnDelete', function (e) {
        e.preventDefault();
    
        var confirmation = confirm("Are you sure you want to delete this course?");
    
        if (confirmation) {
            var row = $(this).closest('tr');
            var courseId = row.find('td:first-child').text();
    
            $.ajax({
                url: '/delete-course/' + courseId,
                method: 'GET',
                success: function (response) {
                    if (response.success) {
                        dataTable.row(row).remove().draw(false);
                        alert('Delete Course succeed.');
                        updateCourseTable(response);
                    } else {
                        alert(response.message);
                    }
                },
                error: function (error) {
                    console.error('Error deleting course:', error);
                    alert('Error deleting course. Please try again.');
                }
            });
        }
    });

    function updateCourseTable(response) {
        document.getElementById('courseName').value = '';
        document.getElementById('editCourseName').value = '';
        dataTable.clear();
    
        // Add new rows without clearing existing ones
        response.courses.forEach(function (course) {
            dataTable.row.add([
                course.id,
                course.name,
                '<a class="btnSEC" th:href="@{/manage-course-section/{courseId}(courseId=${course.getID()})}" data-course-id="' + course.id + '"><i class="fas fa-book" title="Section"></i></a>' +
                '<a class="btnEdit" href="#" data-course-id="' + course.id + '"><i class="fas fa-pencil-alt" title="Edit"></i></a>' +
                '<a class="btnDelete" href="#" data-course-id="' + course.id + '"><i class="fas fa-trash-alt" title="Delete"></i></a>'
            ]);
        });
    
        dataTable.draw(false);
    }

    addCourseModal._element.addEventListener('hidden.bs.modal', function () {
        document.getElementById('courseName').value = '';
    });
    $('#datatable').on('click', '.btnEdit', function (e) {
        e.preventDefault();
        var row = $(this).closest('tr');
        var courseId = row.find('td:first-child').text();
        var currentCourseName = row.find('td:nth-child(2)').text();

        showEditModal(courseId, currentCourseName);
    });

    $('#datatable').on('click', '.btnSEC', function (e) {
        e.preventDefault();
        var row = $(this).closest('tr');
        var courseId = row.find('td:first-child').text();
        var currentCourseName = row.find('td:nth-child(2)').text();

        window.location.href = '/manage-course-section/' + courseId;
    });


    function showEditModal(courseId, currentCourseName) {
        var editCourseModal = new bootstrap.Modal(document.getElementById('editCourseModal'));
        var editCourseForm = document.getElementById('editCourseForm');
        var editCourseNameInput = document.getElementById('editCourseName');

        if (!editCourseForm || !editCourseNameInput) {
            console.error('Required elements not found.');
            return;
        }

        editCourseNameInput.value = currentCourseName;

        editCourseModal.show();

        editCourseForm.addEventListener('submit', function (event) {
            event.preventDefault();

            var editedCourseName = editCourseNameInput.value.trim();

                $.ajax({
                    url: '/update-course/' + courseId,
                    method: 'POST',
                    data: { editedCourseName: editedCourseName },
                    success: function (response) {
                        if (response.success) {
                            editCourseModal.hide();
                            alert('Edit Course succeed.');
                            console.log(response.message);
                            updateCourseTable(response);
                            location.reload();
                        } else {
                            alert(response.message);
                        }
                    },
                    error: function (error) {
                        console.log('Error');
                        alert('Course name already exists.');
                    }
                });
        });

        document.getElementById('cancelEditCourseBtn').addEventListener('click', function () {
            editCourseModal.hide();
            location.reload();
        });
    }
    
});
