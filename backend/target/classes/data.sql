-- 楼栋
INSERT INTO dormitories (name, floors, total_rooms, capacity, type) VALUES ('A栋', 6, 72, 288, '男寝');
INSERT INTO dormitories (name, floors, total_rooms, capacity, type) VALUES ('B栋', 6, 72, 288, '女寝');
INSERT INTO dormitories (name, floors, total_rooms, capacity, type) VALUES ('C栋', 5, 60, 240, '男寝');

-- 房间 (dormitory_id 1=A栋, 2=B栋, 3=C栋)
INSERT INTO rooms (dormitory_id, floor, number, capacity, occupied) VALUES (1, 3, '301', 4, 3);
INSERT INTO rooms (dormitory_id, floor, number, capacity, occupied) VALUES (1, 3, '302', 4, 4);
INSERT INTO rooms (dormitory_id, floor, number, capacity, occupied) VALUES (2, 2, '201', 4, 2);
INSERT INTO rooms (dormitory_id, floor, number, capacity, occupied) VALUES (3, 1, '101', 4, 0);

-- 学生 (room_id 1=A栋301, 2=A栋302, 3=B栋201, 4=C栋101)
INSERT INTO students (name, student_id, gender, college, major, grade, room_id) VALUES ('张三', '2021001', '男', '计算机学院', '软件工程', '2021', NULL);
INSERT INTO students (name, student_id, gender, college, major, grade, room_id) VALUES ('李四', '2021002', '男', '计算机学院', '计算机科学', '2021', 1);
INSERT INTO students (name, student_id, gender, college, major, grade, room_id) VALUES ('王五', '2021003', '男', '电子学院', '电子信息', '2021', 1);
INSERT INTO students (name, student_id, gender, college, major, grade, room_id) VALUES ('赵六', '2022001', '女', '文学院', '汉语言文学', '2022', 3);
INSERT INTO students (name, student_id, gender, college, major, grade, room_id) VALUES ('钱七', '2022002', '女', '经管学院', '金融学', '2022', 3);
