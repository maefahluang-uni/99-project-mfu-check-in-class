// document.addEventListener('DOMContentLoaded', function () {

//     var addCourseModal = new bootstrap.Modal(document.getElementById('addCourseModal'));

//     document.getElementById('addCourseBtn').addEventListener('click', function () {
//         addCourseModal.show();
//     });

// document.getElementById('addCourseForm').addEventListener('submit', function (event) {
//     event.preventDefault();

//     var courseNameInput = document.getElementById('courseName');
//     var courseName = document.getElementById('courseName').value.trim();

//     if (courseName !== '') {

//         var duplicate = Array.from(document.querySelectorAll('#datatable tbody tr td:nth-child(2)'))
//             .map(td => td.textContent.trim().toLowerCase())
//             .includes(courseName.toLowerCase());

//         if (!duplicate) {
//             console.log('Adding course to the database...');

//             $.ajax({
//                 url: '/add-course',
//                 method: 'POST',
//                 data: { courseName: courseName },
//                 success: function (response) {

//                     updateCourseTable(response);
                    
   
//                     addCourseModal.hide();
//                     alert('Add Course succeed.');
//                     courseNameInput.value = '';
//                 },
//                 error: function (error) {
//                     console.error('Error adding course:', error);

//                     alert('Course with the same name already exists.');
//                     courseNameInput.value = '';
//                 }
//             });
//         } else {

//             alert('Course with the same name already exists.');
//             courseNameInput.value = '';
//         }
//     } else {

//         alert('Please enter a course name.');
//     }
// });


// function updateCourseTable(response) {

//     var dataTable = $('#datatable').DataTable();

//     dataTable.clear();

//     response.courses.forEach(function (course) {
//         dataTable.row.add([
//             course.id,
//             course.name,
//             '<a class="btnEdit" href="#"><i class="fas fa-pencil-alt" title="Edit"></i></a>' +
//             '<a class="btnDelete" th:href="@{/delete-course/{courseId}(courseId=${course.getID()})}"><i class="fas fa-trash-alt" title="Delete"></i></a>'
//         ]).draw(false);
//     });
//     }
//     addCourseModal._element.addEventListener('hidden.bs.modal', function () {
//         document.getElementById('courseName').value = '';
//     });
// });


// new

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

    // Event delegation for delete button
    $('#datatable').on('click', '.btnDelete', function (e) {
        e.preventDefault();
    
        // Show a confirmation dialog
        var confirmation = confirm("Are you sure you want to delete this course?");
    
        if (confirmation) {
            var row = $(this).closest('tr');
            var courseId = row.find('td:first-child').text();
    
            // Make an AJAX call to delete the course
            $.ajax({
                url: '/delete-course/' + courseId,
                method: 'GET',
                success: function (response) {
                    if (response.success) {
                        // Use DataTable API to remove the row
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
        // Clear the input field
        document.getElementById('courseName').value = '';
        dataTable.clear();
    
        // Add new rows without clearing existing ones
        response.courses.forEach(function (course) {
            dataTable.row.add([
                course.id,
                course.name,
                '<a class="btnEdit" href="#"><i class="fas fa-pencil-alt" title="Edit"></i></a>' +
                '<a class="btnDelete" href="#" data-course-id="' + course.id + '"><i class="fas fa-trash-alt" title="Delete"></i></a>'
            ]);
        });
    
        // Redraw the DataTable
        dataTable.draw(false);
    }

    addCourseModal._element.addEventListener('hidden.bs.modal', function () {
        document.getElementById('courseName').value = '';
    });
});
