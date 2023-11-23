document.addEventListener('DOMContentLoaded', function () {

    var addCourseModal = new bootstrap.Modal(document.getElementById('addCourseModal'));

    document.getElementById('addCourseBtn').addEventListener('click', function () {
        addCourseModal.show();
    });

document.getElementById('addCourseForm').addEventListener('submit', function (event) {
    event.preventDefault();

    var courseNameInput = document.getElementById('courseName');
    var courseName = document.getElementById('courseName').value.trim();

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


function updateCourseTable(response) {

    var dataTable = $('#datatable').DataTable();

    dataTable.clear();

    response.courses.forEach(function (course) {
        dataTable.row.add([
            course.id,
            course.name,
            '<a class="btnEdit" href="#"><i class="fas fa-pencil-alt" title="Edit"></i></a>' +
            '<a class="btnDelete" href="#"><i class="fas fa-trash-alt" title="Delete"></i></a>'
        ]).draw(false);
    });
    }
    addCourseModal._element.addEventListener('hidden.bs.modal', function () {
        document.getElementById('courseName').value = '';
    });
});
