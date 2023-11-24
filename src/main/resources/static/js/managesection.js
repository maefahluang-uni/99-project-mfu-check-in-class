document.addEventListener('DOMContentLoaded', function () {

    var addSectionModal = new bootstrap.Modal(document.getElementById('addSectionModal'));
    var dataTable = $('#datatable').DataTable();

    document.getElementById('addSectionBtn').addEventListener('click', function () {
        addSectionModal.show();
    });

    document.getElementById('addSectionForm').addEventListener('submit', function (event) {
        event.preventDefault();

        var sectionInput = document.getElementById('section');
        var locationInput = document.getElementById('location');
        var periodInput = document.getElementById('period');

        var courseId = extractCourseIdFromUrl();
        var section = sectionInput.value.trim();
        var location = locationInput.value.trim();
        var period = periodInput.value.trim();

        if (section !== '' && location !== '' && period !== '') {
            
            var duplicate = Array.from(document.querySelectorAll('#datatable tbody tr'))
            .some(tr => {
                var locationCell = tr.children[2];
                var periodCell = tr.children[3];
        
                var existingLocation = locationCell ? locationCell.textContent.trim().toLowerCase() : '';
                var existingPeriod = periodCell ? periodCell.textContent.trim().toLowerCase() : '';
        
                return existingLocation === location.toLowerCase() && existingPeriod === period.toLowerCase();
            });

            if (!duplicate) {
                console.log('Adding section to the database...');

                $.ajax({
                    url: `/manage-course-section/${courseId}/add-section`,
                    method: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        section: section,
                        location: location,
                        period: period
                    }),
                    success: function (response) {
                        if (response.success) {
                            addSectionModal.hide();
                            alert('Add Section succeed.');
                            console.log(response.message);
                            sectionInput.value = '';
                            locationInput.value = '';
                            periodInput.value = '';
                            window.location.reload();
                            // updateSectionTable(response);
                        } else {
                            alert(response.message);
                        }
                    },
                    error: function (error) {
                        console.error('Error adding section:', error);
                        alert('Error adding section. Please try again.');
                        sectionInput.value = '';
                        locationInput.value = '';
                        periodInput.value = '';
                    }
                });
            } else {
                alert('Section with the same location and period already exists.');
            }
        } else {
            alert('Please fill in all fields.');
        }
    });

    $('#datatable').on('click', '.btnDelete', function (e) {
        e.preventDefault();
    
        var confirmation = confirm("Are you sure you want to delete this section?");
    
        if (confirmation) {
            var row = $(this).closest('tr');
            var sectionId = row.find('td:first-child').text();
        
            $.ajax({
                url: '/delete-section/' + sectionId, // Ensure the correct URL
                method: 'DELETE',
                success: function (response) {
                    if (response.success) {
                        dataTable.row(row).remove().draw(false);
                        alert('Delete section succeed.');
                        // updateSectionTable(response);
                    } else {
                        alert(response.message);
                    }
                },
                error: function (xhr, status, error) {
                    if (xhr.status === 404) {
                        alert('Section not found.');
                    } else {
                        console.error('Error deleting section:', error);
                        alert('Error deleting section. Please try again.');
                    }
                }
            });
        }
    });

    function updateSectionTable(response) {
        dataTable.clear();
    
        response.sections.forEach(function (section) {
            dataTable.row.add([
                section.id,
                section.section,
                section.location,
                section.period,
                '<a class="btnEdit" href="#" data-section-id="' + section.id + '"><i class="fas fa-pencil-alt" title="Edit"></i></a>' +
                '<a class="btnDelete" href="#" data-section-id="' + section.id + '"><i class="fas fa-trash-alt" title="Delete"></i></a>'
            ]);
        });
    
        dataTable.draw(false);
    }

    addSectionModal._element.addEventListener('hidden.bs.modal', function () {
        document.getElementById('section').value = '';
        document.getElementById('location').value = '';
        document.getElementById('period').value = '';
    });
    var editSectionModal = new bootstrap.Modal(document.getElementById('editSectionModal'));

    $('#datatable').on('click', '.btnEdit', function (e) {
        e.preventDefault();
        var courseId = extractCourseIdFromUrl();
        var row = $(this).closest('tr');
        var sectionId = row.find('td:first-child').text();
        var currentSection = {
            section: row.find('td:nth-child(2)').text(),
            location: row.find('td:nth-child(3)').text(),
            period: row.find('td:nth-child(4)').text()
        };

        showEditModal(courseId, sectionId, currentSection);
    });

    function showEditModal(courseId, sectionId, currentSection) {
        var editSectionForm = document.getElementById('editSectionForm');
        var editLocationInput = document.getElementById('editLocation');
        var editPeriodInput = document.getElementById('editPeriod');

        if (!editSectionForm || !editLocationInput || !editPeriodInput) {
            console.error('Required elements not found.');
            return;
        }

        editSectionModal.show();

        // Set initial values in the edit form
        editLocationInput.value = currentSection.location;
        editPeriodInput.value = currentSection.period;

        editSectionForm.addEventListener('submit', function (event) {
            event.preventDefault();

            var editLocationValue = $('#editLocation').val().trim();
            var editPeriodValue = $('#editPeriod').val().trim();
        
            $.ajax({
                url: '/manage-course-section/'+ courseId +'/update-section/'+ sectionId,
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    location: editLocationValue,
                    period: editPeriodValue
                }),
                success: function (response) {
                    if (response.success) {
                        editSectionModal.hide();
                        alert('Edit Section succeed.');
                        console.log(response.message);
                        updateSectionTable(response);
                        location.reload(); // You may not need to reload the page; it depends on your application flow
                    } else {
                        alert(response.message);
                    }
                },
                error: function (error) {
                    console.log('Error updating section:', error);
                    alert('Error updating section. Please try again.');
                }
            });
        });

        document.getElementById('cancelEditSectionBtn').addEventListener('click', function () {
            editSectionModal.hide();
        });
    }
    function extractCourseIdFromUrl() {
        // Example URL: http://example.com/manage-course-section/1305219
        var pathArray = window.location.pathname.split('/');
        var courseIdIndex = pathArray.indexOf('manage-course-section') + 1;

        if (courseIdIndex < pathArray.length) {
            return pathArray[courseIdIndex];
        }

        // Default value or handle error as needed
        return null;
    }
    
});
