            /*<![CDATA[*/
            var userdata = /*[[${userdata}]]*/ 'default';
            var mycourse = /*[[${mycourse}]]*/ 'default';
            var semester = /*[[${semester}]]*/ 'default';

          /*]]>*/
          var CurrentSemester;
          function getSemester(NOW) {
              for (const idx in semester) {
                  const DATE_OBJECT = semester[idx]
                  const DATE_START = new Date(DATE_OBJECT.dateStart)
                  const DATE_FINISH = new Date(DATE_OBJECT.dateFinish)
                  if ((NOW.getTime() >= DATE_START.getTime()) && (NOW.getTime() <= DATE_FINISH.getTime())) {
                      return DATE_OBJECT;
                  }
              }
              return null;
          }
          var PROXY = ObservableSlim.create({}, true, (changes) => {
            changes.forEach(LOG => {
                const EVENT = LISTENER[LOG.property]
                if (EVENT) {
                    EVENT(LOG)
                }
            });
        });
        const LISTENER = {
            SelectDate: function(SYNC) {
                const newValue = SYNC.newValue;
                const previousValue = SYNC.previousValue;
                // data sync
                PROXY.CalendarNow = new Date(newValue); // math floor to sunday
                CurrentSemester = getSemester(newValue);
                var container = document.getElementById("courseList");
                if (CurrentSemester) {
                    mycourse.forEach(subject => {
                        if (subject.semester) {
                            if (subject.semester.id == CurrentSemester.id) { // or check getTime is in range but lazy xd
                                let datePeriod = convertStringToDate(subject.period);
                                if (datePeriod.startDate.getDay() == newValue.getDay()) { // check if select date is daylabel? (sun - sat)
                                    addCourse(subject);
                                }
                            }
                        }
                    });
                }
                var scanButtons = document.getElementsByClassName("scanButton");
                Array.from(scanButtons).forEach(function(button) {
                    button.addEventListener('click', function() {
                        window.location.href = "/scan";
                    });
                });
            },
        }
        PROXY.SelectDate = new Date();