README - Sistema de Gestión Académica Universitaria
📌 Descripción del Proyecto
Este proyecto consiste en un Sistema de Gestión Académica para una universidad, diseñado para administrar:

Carreras, cursos, profesores, alumnos, ciclos lectivos y grupos.

Procesos clave como programación de ciclos, matrículas, registro de notas y consultas de historial académico.

El sistema está en desarrollo y se implementa en fases, siguiendo un modelo de base de datos relacional y programación modular.

🛠️ Funcionalidades Principales
1. Mantenimientos Básicos
📚 Carreras: CRUD + búsqueda por nombre/código.

🏫 Cursos: CRUD + búsqueda por nombre/código/carrera.

👨‍🏫 Profesores: CRUD + búsqueda por nombre/cédula.

🎓 Alumnos: CRUD + búsqueda por nombre/cédula/carrera + historial académico.

📅 Ciclos Lectivos: CRUD + selección de ciclo activo.

2. Procesos Académicos
🗂 Oferta Académica: Programación de cursos/grupos por ciclo y carrera.

✏️ Matrículas: Registro de alumnos en grupos (solo para su carrera).

📊 Registro de Notas: Profesores asignan notas a estudiantes en sus grupos.

3. Consultas
🔍 Historial Académico: Alumnos visualizan sus cursos y calificaciones.

4. Seguridad
🔑 Perfiles de Usuario:

Administrador: Acceso total (excepto registro de notas).

Matriculador: Solo matrículas.

Profesor: Solo registro de notas.

Alumno: Solo consulta de historial.
