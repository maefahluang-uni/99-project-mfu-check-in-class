    // Add a click event listener to all buttons with class 'link-course-btn'
    document.querySelectorAll('.link-course-btn').forEach(function(button) {
        button.addEventListener('click', function() {
            // Get the lecturer ID from the data attribute
            var lecturerId = button.getAttribute('data-lecturer-id');

            // Construct the URL
            var url = '/manage-lecturer/' + lecturerId + '/schedule/link-course';

            // Redirect to the URL
            window.location.href = url;
        });
    });