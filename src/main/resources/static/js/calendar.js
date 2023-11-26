document.addEventListener('DOMContentLoaded', function() {
    // Function to update the date and time
    function updateDateTime() {
        var currentDate = new Date();
        var dateString = currentDate.toLocaleDateString('en-US');
        // Customize time options for Thailand format
        var timeOptions = { 
            hour12: false,
            timeZone: 'Asia/Bangkok' 
        };
        var timeString = currentDate.toLocaleTimeString('en-US', timeOptions);
        var dateElement = document.getElementById('currentDate');
        dateElement.innerHTML = 'Current Date: ' + dateString + ' ' + timeString;
    }
    // Initial update
    updateDateTime();
    // Update every second (1000 milliseconds)
    setInterval(updateDateTime, 1000);
});

var now = new Date();
var selectedYear = now.getFullYear();
var selectedMonth = now.getMonth();
var selectedDay = null;

// Function to toggle the visibility of the calendar
function toggleCalendar() {
    var calendarContainer = document.getElementById('calendar-container');
    calendarContainer.style.display = (calendarContainer.style.display === 'block') ? 'none' : 'block';
    updateCalendar(); // Update the calendar when it is displayed
}

// Function to close the calendar
function closeCalendar() {
    document.getElementById('calendar-container').style.display = 'none';
}

// Function to update the calendar with the current month
function updateCalendar() {
    var month = now.getMonth();
    var year = now.getFullYear();
    var monthYearElement = document.getElementById('month-year');
    monthYearElement.innerText = new Intl.DateTimeFormat('en-US', { month: 'long', year: 'numeric' }).format(now);
    var daysContainer = document.getElementById('calendar-days');
    daysContainer.innerHTML = '';
    var daysInMonth = new Date(year, month + 1, 0).getDate();
    for (var i = 1; i <= daysInMonth; i++) {
        var dayElement = document.createElement('div');
        dayElement.classList.add('calendar-day');
        dayElement.innerText = i;
        dayElement.onclick = function () {
            //..............................
            //..............................

            // Remove the highlight from the previously selected date
            var selectedDateElement = document.querySelector('.selected-date');
            if (selectedDateElement) {
                selectedDateElement.classList.remove('selected-date');
            }
            // Highlight the selected date
            this.classList.add('selected-date');
            // Store the selected date in global variables
            selectedYear = year;
            selectedMonth = month;
            selectedDay = parseInt(this.innerText);
            // You can send the selected date to the system here
            var selectedDate = new Date(selectedYear, selectedMonth, selectedDay);
            console.log('Selected Date:', selectedDate.getTime());
            var SelectedElement = document.getElementById('SelectedElement');
            // var dateOptions = { 
            //     weekday: 'short',
            //     day: 'numeric',
            //     month: 'short',
            //     year: 'numeric'
            // };
            SelectedElement.innerHTML = 'Selected Date: ' + selectedDate.toLocaleDateString('en-US');
            closeCalendar(); // Close the calendar after selecting a date
        };
        // Highlight the previously selected date
        if (selectedYear === year && selectedMonth === month && i === selectedDay) {
            dayElement.classList.add('selected-date');
        }
        daysContainer.appendChild(dayElement);
    }
}

// Function to change the displayed month
function changeMonth(delta) {
    now = now || new Date();
    now.setMonth(now.getMonth() + delta);
    updateCalendar();
}

// Initial calendar update
updateCalendar();

























var object = {
    _a: 1,
    get a() {
        return this._a;
    },
    set a(NEW_VALUE) {
        let OLD_VALUE = this._a;
        this._a = NEW_VALUE;
        if (this.Changed) {
            this.Changed(OLD_VALUE, NEW_VALUE);
        }
    }
};
object.Changed = function(PreviousValue, NewValue) {
    //UpdateSide(NewValue);
    //UpdateCalendar(NewValue);
};
  
function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}
async function x() {
    while (true) {
        object.a += 1;
        await sleep(2000);
    }
}
x()

