use  banner;

CREATE TABLE Career (
  cod INTEGER PRIMARY KEY,
  name TEXT,
  title TEXT
);

CREATE TABLE Ciclo (
  id INTEGER PRIMARY KEY,
  year INTEGER,
  number INTEGER,
  date_start TEXT,
  date_finish TEXT
);

-- debe correr esto para agregar una columna a ciclo
ALTER TABLE Ciclo ADD COLUMN is_active BOOLEAN DEFAULT FALSE;

CREATE TABLE Course (
  cod INTEGER PRIMARY KEY,
  name TEXT,
  credits INTEGER,
  hours INTEGER,
  ciclo_id INTEGER,
  career_cod INTEGER,
  FOREIGN KEY (ciclo_id) REFERENCES Ciclo(id),
  FOREIGN KEY (career_cod) REFERENCES Career(cod)
);

CREATE TABLE Student (
  id INTEGER PRIMARY KEY,
  name TEXT,
  tel_number INTEGER,
  email TEXT,
  born_date TEXT,
  career_cod INTEGER,
  FOREIGN KEY (career_cod) REFERENCES Career(cod)
);

CREATE TABLE Teacher (
  id INTEGER PRIMARY KEY,
  name TEXT,
  tel_number INTEGER,
  email TEXT
);

CREATE TABLE Grupo (
  id INTEGER AUTO_INCREMENT PRIMARY KEY,
  number_group INTEGER,
  year INTEGER,
  horario TEXT,
  course_cod INTEGER,
  teacher_id INTEGER,
  FOREIGN KEY (course_cod) REFERENCES Course(cod),
  FOREIGN KEY (teacher_id) REFERENCES Teacher(id)
);

CREATE TABLE Enrollment (
  student_id INTEGER,
  grupo_id INTEGER,
  grade REAL,
  PRIMARY KEY (student_id, grupo_id),
  FOREIGN KEY (student_id) REFERENCES Student(id),
  FOREIGN KEY (grupo_id) REFERENCES Grupo(id)
);

CREATE TABLE Usuario (
  id INTEGER PRIMARY KEY,
  password TEXT,
  role TEXT CHECK (role IN ('admin', 'matriculador', 'profesor', 'alumno'))
);

-- Insert data into Career
INSERT INTO Career (cod, name, title) VALUES
(1, 'Computer Science', 'BSc in Computer Science'),
(2, 'Business Administration', 'BBA in Business Administration');

-- Insert data into Ciclo
INSERT INTO Ciclo (id, year, number, date_start, date_finish) VALUES
(1, 2024, 1, '2024-01-15', '2024-06-15'),
(2, 2024, 2, '2024-07-01', '2024-12-15');
INSERT INTO Ciclo (id, year, number, date_start, date_finish) VALUES (3, 2025, 10, '2025-02-17', '2025-06-30');



-- Insert data into Course
INSERT INTO Course (cod, name, credits, hours, ciclo_id, career_cod) VALUES
(101, 'Algorithms', 4, 60, 1, 1),
(102, 'Database Systems', 4, 60, 1, 1),
(201, 'Marketing Strategies', 3, 45, 2, 2);

-- Insert data into Student
INSERT INTO Student (id, name, tel_number, email, born_date, career_cod) VALUES
(1, 'Alice Johnson', 123456789, 'alice@example.com', '2002-05-10', 1),
(2, 'Bob Smith', 987654321, 'bob@example.com', '2001-09-20', 2);

-- Insert data into Teacher
INSERT INTO Teacher (id, name, tel_number, email) VALUES
(1, 'Dr. Emily Brown', 555123456, 'emily@example.com'),
(2, 'Prof. Michael Green', 555654321, 'michael@example.com');

-- Insert data into Grupo
INSERT INTO Grupo (id, number_group, year, horario, course_cod, teacher_id) VALUES
(1, 1, 2024, 'Mon-Wed 10:00-12:00', 101, 1),
(2, 2, 2024, 'Tue-Thu 14:00-16:00', 102, 2);

-- Insert data into Enrollment
INSERT INTO Enrollment (student_id, grupo_id, grade) VALUES
(1, 1, 89.5),
(2, 2, 92.0);

-- Insert data into Usuario
INSERT INTO Usuario (id, password, role) VALUES
(1, 'adminpass', 'admin'),
(2, 'matriculador123', 'matriculador'),
(3, 'profesor456', 'profesor'),
(4, 'alumno789', 'alumno');




-- procedimiento para agregar un curso
DELIMITER //
CREATE PROCEDURE insert_course(IN Course_cod INT, IN Course_name VARCHAR(255), IN Course_credits INT, IN Course_hours INT, IN Course_ciclo_id INT, IN Course_career_cod INT)
BEGIN
    INSERT INTO Course (cod, name, credits, hours, ciclo_id, career_cod) VALUES (Course_cod, Course_name, Course_credits, Course_hours, Course_ciclo_id, Course_career_cod);
END //
DELIMITER ;
-- Stored Procedure: Mantenimiento de cursos (búsqueda por nombre, código y carrera) en MySQL
DELIMITER $$
CREATE PROCEDURE BuscarCurso(
    IN p_nombre VARCHAR(255),
    IN p_codigo INT,
    IN p_carrera_cod INT
)
BEGIN
    SELECT * FROM Course
    WHERE (p_nombre IS NULL OR name LIKE CONCAT('%', p_nombre, '%'))
    AND (p_codigo IS NULL OR cod = p_codigo)
    AND (p_carrera_cod IS NULL OR career_cod = p_carrera_cod);
END $$
DELIMITER ;

-- procedimeinto para insertar una carrera
DELIMITER //
CREATE PROCEDURE insert_career(IN career_cod INT, IN career_name VARCHAR(255), IN career_title VARCHAR(255))
BEGIN
    INSERT INTO Career (cod, name, title) VALUES (career_cod, career_name, career_title);
END //
DELIMITER ;

-- Procedimiento para insertar un usuario:
DELIMITER //
CREATE PROCEDURE insert_user(IN user_id INT, IN user_password VARCHAR(255), IN user_role VARCHAR(255))
BEGIN
    INSERT INTO Usuario (id, password, role) VALUES (user_id, user_password, user_role);
END //
DELIMITER ;

-- eliminar usuario
DELIMITER //

CREATE PROCEDURE delete_user(IN user_id INT)
BEGIN
    DELETE FROM Usuario WHERE id = user_id;
END //

DELIMITER ;



-- Procedimiento para buscar carrera por nombre y codigo
DELIMITER $$
CREATE PROCEDURE buscar_carrera(
    IN p_nombre VARCHAR(255), 
    IN p_cod INT
)
BEGIN
    SELECT * FROM Career 
    WHERE (p_nombre IS NULL OR name LIKE CONCAT('%', p_nombre, '%'))
    AND (p_cod IS NULL OR cod = p_cod);
END $$
DELIMITER ;

-- procedimiento para editar una carrera
DELIMITER //
CREATE PROCEDURE edit_career(
    IN p_cod INT,
    IN p_name VARCHAR(255),
    IN p_title VARCHAR(255)
)
BEGIN
    UPDATE Career
    SET name = p_name, title = p_title
    WHERE cod = p_cod;
END //
DELIMITER ;

-- procedimiento para añadir un curso a una carrera
DELIMITER //
CREATE PROCEDURE add_course_to_career(
    IN p_career_id INT,
    IN p_course_id INT
)
BEGIN
    UPDATE Course 
    SET career_cod = p_career_id
    WHERE cod = p_course_id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE remove_course_from_career(
    IN p_course_id INT
)
BEGIN
    UPDATE Course 
    SET career_cod = NULL
    WHERE cod = p_course_id;
END //
DELIMITER ;

-- procedimiento para buscar profesor por nombre y cedula
DELIMITER $$
CREATE PROCEDURE buscar_profesor(
    IN T_nombre VARCHAR(255), 
    IN T_cod INT
)
BEGIN
    SELECT * FROM Teacher 
    WHERE (T_nombre IS NULL OR name LIKE CONCAT('%', T_nombre, '%'))
    AND (T_cod IS NULL OR id = T_cod);
END $$
DELIMITER ;

-- procedimiento para buscar alumno por nombre, cedula y carrera
DELIMITER $$
CREATE PROCEDURE buscar_alumno(
    IN A_nombre VARCHAR(255), 
    IN A_cod INT,
	IN A_carre INT
)
BEGIN
    SELECT * FROM Student 
    WHERE (A_nombre IS NULL OR name LIKE CONCAT('%', A_nombre, '%'))
    AND (A_cod IS NULL OR id = A_cod)
	AND (A_carre IS NULL OR career_cod = A_carre);
END $$
DELIMITER ;

-- procedimiento para ver historial del alumno
DELIMITER $$
CREATE PROCEDURE alumno_historial(
    IN A_id VARCHAR(255)
)
BEGIN
    SELECT * FROM Enrollment 
    WHERE student_id = A_id;
END $$
DELIMITER ;

-- procedimiento de busqueda de ciclo por anio
DELIMITER $$
CREATE PROCEDURE ciclo_anio(
    IN C_year INT
)
BEGIN
    SELECT * FROM Ciclo WHERE year = C_year;
END $$
DELIMITER ;

-- procedimiento almacenado para marcar un ciclo como activo
DELIMITER $$
CREATE PROCEDURE set_active_ciclo(IN ciclo_id INT)
BEGIN
    -- Desactivar todos los ciclos
    UPDATE Ciclo SET is_active = FALSE;

    -- Activar el ciclo seleccionado
    UPDATE Ciclo SET is_active = TRUE WHERE id = ciclo_id;
END $$
DELIMITER ;

-- procedimiento almacenado para marcar un ciclo como desactivo
DELIMITER $$
CREATE PROCEDURE set_disactive_ciclo(IN ciclo_id INT)
BEGIN
    -- Desactivar todos los ciclos
    UPDATE Ciclo SET is_active = FALSE;
END $$
DELIMITER ;

-- procedimeinto para ver el ciclo activo
DELIMITER $$
CREATE PROCEDURE get_active_ciclo()
BEGIN
    SELECT * FROM Ciclo WHERE is_active = TRUE LIMIT 1;
END $$
DELIMITER ;

-- metood para obtener los cursos de una carre y ciclo en particular
DELIMITER $$
CREATE PROCEDURE get_courses_by_career_and_cycle(
    IN p_career_cod INTEGER,
    IN p_ciclo_id INTEGER
)
BEGIN
    SELECT c.cod, c.name, c.credits, c.hours
    FROM Course c
    WHERE c.career_cod = p_career_cod AND c.ciclo_id = p_ciclo_id;
END $$
DELIMITER ;

-- procedimiento para obtener los grupos que existen de un curso
DELIMITER $$
CREATE PROCEDURE get_groups_by_course(IN course_id INT)
BEGIN
    SELECT id, number_group, year, horario, course_cod, teacher_id
    FROM Grupo
    WHERE course_cod = course_id;
END $$
DELIMITER ;DELIMITER $$
CREATE PROCEDURE get_groups_by_course(IN course_id INT)
BEGIN
    SELECT id, number_group, year, horario, course_cod, teacher_id
    FROM Grupo
    WHERE course_cod = course_id;
END $$
DELIMITER ;

-- procedimiento para anadir un nuevo curso o editar
DELIMITER $$
CREATE PROCEDURE add_or_update_group(
    IN p_groupId INT,
    IN p_number_group INT,    
    IN p_year INT,
    IN p_schedule VARCHAR(255),
    IN p_course_cod INT,
    IN p_teacher_id INT
)
BEGIN
    IF p_groupId = 0 THEN
        -- Insertar nuevo grupo si el id del grupo es 0
        INSERT INTO Grupo (number_group, year, horario, course_cod, teacher_id)
        VALUES (p_number_group, p_year, p_schedule, p_course_cod, p_teacher_id);
        
    ELSE
        -- Actualizar grupo existente
        UPDATE Grupo
        SET number_group = p_number_group, year = p_year, horario = p_schedule, course_cod = p_course_cod, teacher_id = p_teacher_id
        WHERE id = p_groupId;  -- Cambié 'groupId' por 'id' ya que es el nombre correcto en la tabla
    END IF;
END $$
DELIMITER ;


-- Para insertar un nuevo grupo
CALL add_or_update_group(0, 90, 2026, 'Domingos 12:00 AM', 101, 1);

-- Para actualizar un grupo existente
CALL add_or_update_group(11, 40, 2025, 'Sabdos 8:00-10:40', 102, 1);

select * from Course;
select * from grupo;
select * from teacher;


-- crud

DELIMITER //
CREATE PROCEDURE delete_career(IN career_cod INT)
BEGIN
    DELETE FROM Career WHERE cod = career_cod;
END
//
DELIMITER ;


DELIMITER //
CREATE PROCEDURE insert_ciclo(
    IN p_id INT,
    IN p_year INT,
    IN p_number INT,
    IN p_date_start DATE,
    IN p_date_finish DATE
)
BEGIN
    INSERT INTO Ciclo (id, year, number, date_start, date_finish) 
    VALUES (p_id, p_year, p_number, p_date_start, p_date_finish);
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE delete_ciclo(IN ciclo_id INT)
BEGIN
    DELETE FROM Ciclo WHERE id = ciclo_id;
END//
DELIMITER ;



DELIMITER //
CREATE PROCEDURE delete_course(IN p_cod INT)
BEGIN
   DELETE FROM Course WHERE cod = p_cod;
END //
DELIMITER ;





DELIMITER //
CREATE PROCEDURE insert_student(
    IN p_id INT,
    IN p_name VARCHAR(100),
    IN p_tel_number INT,
    IN p_email VARCHAR(100),
    IN p_born_date DATE,
    IN p_career_cod INT
)
BEGIN
    INSERT INTO Student (id, name, tel_number, email, born_date, career_cod) 
    VALUES (p_id, p_name, p_tel_number, p_email, p_born_date, p_career_cod);
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE delete_student(IN student_id INT)
BEGIN
    DELETE FROM Student WHERE id = student_id;
END //
DELIMITER ;


DELIMITER //
CREATE PROCEDURE insert_teacher(
    IN p_id INT,
    IN p_name VARCHAR(100),
    IN p_tel_number INT,
    IN p_email VARCHAR(100)
)
BEGIN
    INSERT INTO Teacher (id, name, tel_number, email) 
    VALUES (p_id, p_name, p_tel_number, p_email);
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE delete_teacher(IN teacher_id INT)
BEGIN
    DELETE FROM Teacher WHERE id = teacher_id;
END//
DELIMITER ;


DELIMITER //
CREATE PROCEDURE insert_grupo(
    IN p_id INT,
    IN p_number_group INT,
    IN p_year INT,
    IN p_horario VARCHAR(50),
    IN p_course_cod INT,
    IN p_teacher_id INT
)
BEGIN
    INSERT INTO Grupo (id, number_group, year, horario, course_cod, teacher_id) 
    VALUES (p_id, p_number_group, p_year, p_horario, p_course_cod, p_teacher_id);
END //
DELIMITER ;

CALL insert_grupo(3, 2, 2025, 'Lunes y Miércoles 8:00-10:00', 101, 1);


DELIMITER //
CREATE PROCEDURE delete_grupo(IN grupo_id INT)
BEGIN
    DELETE FROM Grupo
    WHERE id = grupo_id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE insert_enrollment(
    IN p_student_id INT,
    IN p_grupo_id INT,
    IN p_grade DOUBLE
)
BEGIN
    INSERT INTO Enrollment (student_id, grupo_id, grade) 
    VALUES (p_student_id, p_grupo_id, p_grade);
END //
DELIMITER ;


DELIMITER //
CREATE PROCEDURE delete_enrollment(IN studentId INT, IN grupoId INT)
BEGIN
    DELETE FROM Enrollment
    WHERE student_id = studentId
    AND grupo_id = grupoId;
END//
DELIMITER ;



-- 7  Victor

DELIMITER $$

CREATE PROCEDURE change_cycle(IN student_id INT, IN new_cycle_id INT)
BEGIN
    -- Eliminar las inscripciones actuales del estudiante
    DELETE FROM Enrollment WHERE student_id = student_id;

    -- Agregar al estudiante a los grupos del nuevo ciclo (debes implementar cómo obtener los grupos del nuevo ciclo)
    INSERT INTO Enrollment (student_id, grupo_id)
    SELECT student_id, grupo_id FROM Grupo WHERE ciclo_id = new_cycle_id;
END $$

DELIMITER ;




DELIMITER $$

CREATE PROCEDURE get_active_cycle_courses(IN studentId INT)
BEGIN
    SELECT c.cod, c.name, c.credits, c.hours, ci.year, ci.id AS ciclo_id
    FROM Course c
    JOIN Grupo g ON c.cod = g.course_cod
    JOIN Enrollment e ON g.id = e.grupo_id
    JOIN Ciclo ci ON c.ciclo_id = ci.id
    WHERE e.student_id = studentId AND ci.is_active = TRUE;
END$$

DELIMITER ;


-- 8
DROP PROCEDURE IF EXISTS get_professor_courses;


DELIMITER $$

CREATE PROCEDURE get_professor_courses(IN professor_id INT)
BEGIN
    SELECT g.id AS grupo_id, c.cod AS course_cod, c.name AS course_name
    FROM Grupo g
    JOIN Course c ON g.course_cod = c.cod
    JOIN Ciclo ci ON c.ciclo_id = ci.id
    WHERE g.teacher_id = professor_id AND ci.is_active = TRUE;
END $$

DELIMITER ;


DELIMITER $$

CREATE PROCEDURE update_student_grade(IN student_id INT, IN grupo_id INT, IN new_grade DECIMAL(5,2))
BEGIN
    UPDATE Enrollment 
    SET grade = new_grade
    WHERE student_id = student_id AND grupo_id = grupo_id;
END $$

DELIMITER ;


-- 9

DELIMITER $$

CREATE PROCEDURE get_student_history(IN student_id INT)
BEGIN
    SELECT 
        e.student_id,
        c.cod AS course_cod,
        c.name AS course_name,
        e.grade,
        g.year,
        g.number_group AS group_number,
        ci.year AS cycle_year
    FROM Enrollment e
    JOIN Grupo g ON e.grupo_id = g.id
    JOIN Course c ON g.course_cod = c.cod
    JOIN Ciclo ci ON c.ciclo_id = ci.id
    WHERE e.student_id = student_id
    ORDER BY ci.year DESC, g.year DESC, g.number_group;
END $$

DELIMITER ;

use banner;



DELIMITER $$

CREATE PROCEDURE update_ciclo(
    IN p_id INT,
    IN p_year INT,
    IN p_number INT,
    IN p_dateStart DATE,
    IN p_dateFinish DATE,
    IN p_isActive boolean
)
BEGIN
    UPDATE Ciclo
    SET year = p_year, 
        number = p_number, 
        date_start = p_dateStart, 
        date_finish = p_dateFinish,
        is_active = p_isActive
    WHERE id = p_id;
END $$ 

DELIMITER ;



DELIMITER $$

CREATE PROCEDURE update_career(
    IN p_id INT,
    IN p_name VARCHAR(255),
    IN p_facultyId INT
)
BEGIN
    UPDATE Career
    SET name = p_name, 
        faculty_id = p_facultyId
    WHERE id = p_id;
END $$

DELIMITER ;


DELIMITER $$

CREATE PROCEDURE update_course(
    IN p_cod INT,
    IN p_name VARCHAR(255),
    IN p_credits INT,
    IN p_hours INT,
    IN p_cicloId INT,
    IN p_careerCod INT
)
BEGIN
    UPDATE Course
    SET name = p_name, 
        credits = p_credits, 
        hours = p_hours, 
        ciclo_id = p_cicloId, 
        career_cod = p_careerCod
    WHERE cod = p_cod;
END $$

DELIMITER ;



DELIMITER $$

CREATE PROCEDURE update_enrollment(
    IN p_student_id INT,
    IN p_grupo_id INT,
    IN p_grade DECIMAL(5,2)
)
BEGIN
    UPDATE Enrollment
    SET grade = p_grade
    WHERE student_id = p_student_id AND grupo_id = p_grupo_id;
END $$

DELIMITER ;


DELIMITER $$

CREATE PROCEDURE update_grupo(
    IN p_id INT,
    IN p_number_group INT,
    IN p_year INT,
    IN p_horario VARCHAR(50),
    IN p_course_cod INT,
    IN p_teacher_id INT
)
BEGIN
    UPDATE Grupo
    SET number_group = p_number_group, 
        year = p_year, 
        horario = p_horario, 
        course_cod = p_course_cod, 
        teacher_id = p_teacher_id
    WHERE id = p_id;
END $$

DELIMITER ;


DELIMITER $$

CREATE PROCEDURE update_student(
    IN p_id INT,
    IN p_name VARCHAR(100),
    IN p_tel_number INT,
    IN p_email VARCHAR(100),
    IN p_born_date DATE,
    IN p_career_cod INT
)
BEGIN
    UPDATE Student
    SET name = p_name, 
        tel_number = p_tel_number, 
        email = p_email, 
        born_date = p_born_date, 
        career_cod = p_career_cod
    WHERE id = p_id;
END $$

DELIMITER ;


DELIMITER $$

CREATE PROCEDURE update_teacher(
    IN p_id INT,
    IN p_name VARCHAR(100),
    IN p_tel_number INT,
    IN p_email VARCHAR(100)
)
BEGIN
    UPDATE Teacher
    SET name = p_name, 
        tel_number = p_tel_number, 
        email = p_email
    WHERE id = p_id;
END $$

DELIMITER ;


DELIMITER $$

CREATE PROCEDURE update_user(
    IN p_id INT,
    IN p_password VARCHAR(255),
    IN p_role VARCHAR(50)
)
BEGIN
    UPDATE Usuario
    SET password = p_password, 
        role = p_role
    WHERE id = p_id;
END $$

DELIMITER ;

-- procedimiento del login
DELIMITER //
CREATE PROCEDURE sp_LoginUsuario(
    IN p_id INT,
    IN p_password TEXT
)
BEGIN
    DECLARE stored_password TEXT;
    DECLARE p_resultado BOOLEAN DEFAULT FALSE;

    -- Obtener la contraseña almacenada
    SELECT password INTO stored_password FROM Usuario WHERE id = p_id;

    -- Comparar contraseñas
    IF stored_password = p_password THEN
        SET p_resultado = TRUE;
    END IF;

    -- Devolver el resultado
    SELECT p_resultado AS p_resultado;
END //
DELIMITER ;

-- procedieminto para todas las carreas
DELIMITER //
CREATE PROCEDURE GetAllCareers()
BEGIN
    SELECT * FROM Career;
END;
//DELIMITER ;

-- procedieminto para todos los ciclos
DELIMITER //
CREATE PROCEDURE GetAllCiclos()
BEGIN
    SELECT * FROM Ciclo;
END;
//DELIMITER 

-- procedieminto para todos los cursos
DELIMITER //
CREATE PROCEDURE GetAllCourses()
BEGIN
    SELECT * FROM Course;
END;
//DELIMITER 

-- procedieminto para todas las matriculas
DELIMITER //
CREATE PROCEDURE GetAllEnrollments()
BEGIN
    SELECT * FROM Enrollment;
END;
//DELIMITER 

-- procedieminto para todos los grupos
DELIMITER //
CREATE PROCEDURE GetAllGrups()
BEGIN
    SELECT * FROM Grupo;
END;
//DELIMITER 

-- procedieminto para todos los estudiantes
DELIMITER //
CREATE PROCEDURE GetAllStudents()
BEGIN
    SELECT * FROM Student;
END;
//DELIMITER 

-- procedieminto para todos los profesores
DELIMITER //
CREATE PROCEDURE GetAllTeachers()
BEGIN
    SELECT * FROM Teacher;
END;
//DELIMITER 

-- procedieminto para todos los usuarios
DELIMITER //
CREATE PROCEDURE GetAllUsuarios()
BEGIN
    SELECT * FROM Usuario;
END;
//DELIMITER 
