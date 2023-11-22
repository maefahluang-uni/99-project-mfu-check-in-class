-- ID: SEMESTER_ID
INSERT INTO SEMESTER (ID, ACADEMIC_YEAR, TERM, START_DATE, FINISH_DATE) VALUES
(6666, 2023, 1, '8/16/2023', '12/16/2023'), -- MM/dd/yyyy
(6667, 2023, 2, '1/16/2024', '4/16/2024');


-- ID: COURSE_ID
INSERT INTO COURSE (ID, NAME) VALUES
(1305217, 'Database Systems'),
(1305218, 'Web Development'),
(1305219, 'Data Science'),
(1305220, 'Software Engineering'),
(1305221, 'Computer Networks'),
(1305222, 'Artificial Intelligence'),
(1305223, 'Cybersecurity'),
(1305224, 'Mobile App Development'),
(1305225, 'Human-Computer Interaction'),
(1305226, 'Machine Learning'),
(1305227, 'Cloud Computing'),
(1305228, 'Information Retrieval'),
(1305229, 'Computer Graphics'),
(1305230, 'Embedded Systems'),
(1305231, 'Natural Language Processing'),
(1305232, 'Mobile Game Development'),
(1305233, 'Network Security'),
(1305234, 'Parallel Computing'),
(1305235, 'Distributed Systems'),
(1305236, 'Big Data Analytics'),
(1305237, 'Quantum Computing'),
(1305238, 'Robotics'),
(1305239, 'Virtual Reality'),
(1305240, 'Internet of Things'),
(1305241, 'Blockchain Technology'),
(1305242, 'Digital Signal Processing'),
(1305243, 'Computer Vision'),
(1305244, 'Game Design and Development'),
(1305245, 'Information Systems Management');
-- ID: SECTION_ID
INSERT INTO COURSE_SECTION (ID, COURSE_ID, SECTION, LOCATION, PERIOD) VALUES 
(10000, 1305217, 'LECT 1', 'C3 102', 'Tue, 01:00 - 11:50'),
(10001, 1305217, 'LAB', 'C3 102', 'Fri, 13:00 - 14:50');
INSERT INTO COURSE_SECTION_ON_SEMESTER (SECTION_ID, SEMESTER_ID) VALUES -- (type: RELATION TABLE)
(10000, 6666),
(10001, 6666);


-- ID: STUDENT_ID
INSERT INTO STUDENT (ID, PASSWORD, NAME, PROGRAM, SCHOOL, DEPARTMENT, jAdvisors) VALUES
(6531503070, 'basbas122zaza', 'basbas1234', 'Bachelor of Engineering', 'School of Information Technology', 'Software Engineering', '[1150]');
INSERT INTO STUDENT (ID, PASSWORD, NAME, PROGRAM, SCHOOL, DEPARTMENT, jAdvisors) VALUES
(6531503074, 'a4412', 'artinwza007', 'Bachelor of Engineering', 'School of Information Technology', 'Software Engineering', '[1150]');
INSERT INTO STUDENT_SCHEDULE (STUDENT_ID, SECTION_ID) VALUES -- (type: RELATION TABLE) (NON_ENTITY)
(6531503070, 10000),
(6531503070, 10001);


-- ID: LECTURER_ID
INSERT INTO LECTURER (ID, PASSWORD, NAME, SCHOOL, DEPARTMENT) VALUES
(1150, '1122', 'Dr.Sujitra Arwatchananukul', 'School of Information Technology', 'Software Engineering');
INSERT INTO LECTURER_SCHEDULE (LECTURER_ID, SECTION_ID) VALUES -- (type: RELATION TABLE)
(1150, 10000);


INSERT INTO ADMIN (ID, PASSWORD, NAME) VALUES
(666, '1234', 'MOS888');
