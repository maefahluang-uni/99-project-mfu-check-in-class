--// ID: SEMESTER_ID
INSERT INTO SEMESTER (ID, ACADEMIC_YEAR, TERM, START_DATE, FINISH_DATE) VALUES
(6666, 2023, 1, '8/16/2023', '12/16/2023'), -- MM/dd/yyyy
(6667, 2023, 2, '1/16/2024', '4/16/2024');


--// ID: COURSE_ID
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
--// ID: SECTION_ID
INSERT INTO COURSE_SECTION (ID, COURSE_ID, SECTION, LOCATION, PERIOD) VALUES 
(10000, 1305217, 'LECT 1', 'C3 102', 'Sun, 21:00 - 22:50'),
(10001, 1305217, 'LAB', 'C3 102', 'Mon, 01:00 - 11:50'),
(10002, 1305218, 'Section 1', 'C3 102', 'Tue, 00:01 - 18:50'),
(10003, 1305218, 'Section 2', 'C3 102', 'Wed, 00:02 - 17:50'),
(10004, 1305244, 'LECTURE', 'C3 102', 'Thu, 09:00 - 11:50'),
(10005, 1305244, 'LAB', 'C3 102', 'Fri, 02:00 - 15:50'),
(10006, 1305245, 'LECT 1', 'C3 102', 'Sat, 01:30 - 19:00'),
(10007, 1305232, 'LECTURE', 'C3 102', 'Sun, 02:30 - 11:50'),
(10008, 1305232, 'LECTURE', 'C3 102', 'Fri, 00:10 - 15:50');
INSERT INTO COURSE_SECTION_ON_SEMESTER (SECTION_ID, SEMESTER_ID) VALUES -- (type: RELATION TABLE)
(10000, 6666),
(10001, 6666),
(10002, 6666),
(10003, 6666),
(10004, 6666),
(10005, 6666),
(10006, 6666),
(10007, 6666),
(10008, 6666);


--// ID: LECTURER_ID
INSERT INTO LECTURER (ID, PASSWORD, NAME, SCHOOL, DEPARTMENT) VALUES
(1150, '$2a$10$p8a7OPzheAE3PzocvvICROgDtfKzdVDXmWVzXQ63vgA4lHhxG7M/S', 'Dr.Sujitra Arwatchananukul', 'School of Information Technology', 'Software Engineering'); -- password = 1122
INSERT INTO LECTURER_SCHEDULE (LECTURER_ID, SECTION_ID) VALUES -- (type: RELATION TABLE)
(1150, 10000),
(1150, 10001),
(1150, 10002),
(1150, 10003),
(1150, 10004),
(1150, 10005),
(1150, 10006),
(1150, 10007),
(1150, 10008);
INSERT INTO LECTURER (ID, PASSWORD, NAME, SCHOOL, DEPARTMENT) VALUES
(1151, '$2a$10$p8a7OPzheAE3PzocvvICROgDtfKzdVDXmWVzXQ63vgA4lHhxG7M/S', 'XD', 'School of Information Technology', 'Software Engineering'); -- password = 1122

--// ID: STUDENT_ID
INSERT INTO STUDENT (ID, PASSWORD, NAME, PROGRAM, SCHOOL, DEPARTMENT, LECTURER_ID) VALUES
(6531503070, '$2a$10$cIHFOlNlTF5jC7VwShnKNuUmdEdHthL9aEJmRTqO6v7KJqZD1OY5u', 'Ratchanon Suwatsiriphol', 'Bachelor of Engineering', 'School of Information Technology', 'Software Engineering', 1150); -- password = basbas122zaza
INSERT INTO STUDENT_SCHEDULE (STUDENT_ID, SECTION_ID) VALUES -- (type: RELATION TABLE) (NON_ENTITY)
(6531503070, 10000),
(6531503070, 10001),
(6531503070, 10002),
(6531503070, 10003),
(6531503070, 10004),
(6531503070, 10005),
(6531503070, 10006),
(6531503070, 10007),
(6531503070, 10008);
-- INSERT INTO STUDENT (ID, PASSWORD, NAME, PROGRAM, SCHOOL, DEPARTMENT, LECTURER_ID) VALUES
-- (6531503078, '$2a$10$bV4JQxgiI.HEz7H/poPqjOKR45yV3d5vRmZJEYTKnyhNn5SVD8bKG', 'Sonthaya Phureesawat', 'Bachelor of Engineering', 'School of Information Technology', 'Software Engineering', 1150); -- password = 254704son
-- INSERT INTO STUDENT (ID, PASSWORD, NAME, PROGRAM, SCHOOL, DEPARTMENT, LECTURER_ID) VALUES
-- (6531503074, '$2a$10$F4QPRAmwNXrMQxem93ZcK.n495FzHLmoEXAWD6EzP7s7JbC7zwQu6', 'Vilaseenlapa Limthanadeatch-anun', 'Bachelor of Engineering', 'School of Information Technology', 'Software Engineering', 1150); -- password = arty1692003
-- INSERT INTO STUDENT (ID, PASSWORD, NAME, PROGRAM, SCHOOL, DEPARTMENT, LECTURER_ID) VALUES
-- (6531503086, '$2a$10$FTc/EPQoypUZ3CaYZoY8YeMAO1qg2Q9.wPgDeg1J7URLxrvNL9tBS', 'Suranan Chirachatchai', 'Bachelor of Engineering', 'School of Information Technology', 'Software Engineering', 1150); -- password = 0854059672zz


INSERT INTO ADMIN (ID, PASSWORD, NAME) VALUES
(666, '$2a$10$YOPb8WPRguT.OhIkRe/5p.QhbYerqibhivR00EpGDTXP2EQQ.WU82', 'MOS888'); -- password = 1234