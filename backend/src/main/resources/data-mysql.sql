INSERT INTO dormitories (name, floors, total_rooms, capacity, type)
SELECT 'A栋', 6, 72, 288, '男寝'
WHERE NOT EXISTS (SELECT 1 FROM dormitories WHERE name = 'A栋');

INSERT INTO dormitories (name, floors, total_rooms, capacity, type)
SELECT 'B栋', 6, 72, 288, '女寝'
WHERE NOT EXISTS (SELECT 1 FROM dormitories WHERE name = 'B栋');

INSERT INTO dormitories (name, floors, total_rooms, capacity, type)
SELECT 'C栋', 5, 60, 240, '男寝'
WHERE NOT EXISTS (SELECT 1 FROM dormitories WHERE name = 'C栋');

INSERT INTO rooms (dormitory_id, floor, number, capacity, occupied)
SELECT d.id, 3, '301', 4, 3
FROM dormitories d
WHERE d.name = 'A栋'
  AND NOT EXISTS (
    SELECT 1 FROM rooms r WHERE r.dormitory_id = d.id AND r.number = '301'
  );

INSERT INTO rooms (dormitory_id, floor, number, capacity, occupied)
SELECT d.id, 3, '302', 4, 4
FROM dormitories d
WHERE d.name = 'A栋'
  AND NOT EXISTS (
    SELECT 1 FROM rooms r WHERE r.dormitory_id = d.id AND r.number = '302'
  );

INSERT INTO rooms (dormitory_id, floor, number, capacity, occupied)
SELECT d.id, 2, '201', 4, 2
FROM dormitories d
WHERE d.name = 'B栋'
  AND NOT EXISTS (
    SELECT 1 FROM rooms r WHERE r.dormitory_id = d.id AND r.number = '201'
  );

INSERT INTO rooms (dormitory_id, floor, number, capacity, occupied)
SELECT d.id, 1, '101', 4, 0
FROM dormitories d
WHERE d.name = 'C栋'
  AND NOT EXISTS (
    SELECT 1 FROM rooms r WHERE r.dormitory_id = d.id AND r.number = '101'
  );
