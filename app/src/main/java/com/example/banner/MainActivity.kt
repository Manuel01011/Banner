package com.example.banner

import CareerController
import CicloController
import CourseController
import EnrollmentController
import GrupoController
import StudentController
import TeacherController
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.banner.ui.theme.BannerTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BannerTheme {
        Greeting("Android")
    }
}

fun main() {
    val careerController = CareerController()
    val cicloController = CicloController()
    val courseController = CourseController()
    val studentController = StudentController()  // Controlador para los estudiantes
    val teacherController = TeacherController()  // Controlador para los profesores
    val grupoController = GrupoController()  // Controlador para los grupos
    val enrollmentController = EnrollmentController()  // Controlador para las inscripciones

    try {
        // Obtener todas las carreras antes de la inserción
        println(careerController.getAllCareers())

        // Insertar una nueva carrera
        careerController.insertCareer(1, "Derecho", "Derecho Gornamental")
        println(careerController.getAllCareers())

        // Obtener todos los ciclos antes de la inserción
        println(cicloController.getAllCiclos())

        // Insertar un nuevo ciclo
        cicloController.insertCiclo(1, 2025, 1, "2025-01-01", "2025-12-31")
        println(cicloController.getAllCiclos())

        // Insertar un curso solo si el ciclo con id=1 existe
        val cicloIdExistente = cicloController.getAllCiclos().any { it.id == 1 }
        if (cicloIdExistente) {
            courseController.insertCourse(1, "Matemáticas", 3, 4, 1, 1)
            println(courseController.getAllCourses())
        } else {
            println("El ciclo con id=1 no existe, no se puede insertar el curso.")
        }

        // Insertar un estudiante
        studentController.insertStudent(1, "Juan Pérez", 123456789, "juan.perez@email.com", "2000-05-15", 1)
        println("Estudiantes después de la inserción:")
        println(studentController.getAllStudents())

        // Insertar un nuevo profesor
        teacherController.insertTeacher(1, "Profesor Juan", 987654321, "juan.profesor@email.com")
        println("Profesores después de la inserción:")
        println(teacherController.getAllTeachers())

        // Insertar un nuevo grupo
        grupoController.insertGrupo(1, 101, 2025, "08:00-10:00", 1, 1)
        println("Grupos después de la inserción:")
        println(grupoController.getAllGrupos())

        // Insertar una inscripción solo si el estudiante y el grupo existen
        val studentExistente = studentController.getAllStudents().any { it.id == 1 }
        val grupoExistente = grupoController.getAllGrupos().any { it.id == 1 }
        if (studentExistente && grupoExistente) {
            enrollmentController.insertEnrollment(1, 1, 9.5)
            println("Inscripciones después de la inserción:")
            println(enrollmentController.getAllEnrollments())
        } else {
            println("El estudiante o el grupo no existen, no se puede insertar la inscripción.")
        }

        // Eliminar la inscripción solo si existe
        val enrollmentExistente = enrollmentController.getAllEnrollments().any { it.studentId == 1 && it.grupoId == 1 }
        if (enrollmentExistente) {
            enrollmentController.deleteEnrollment(1, 1)
            println("Inscripciones después de la eliminación:")
            println(enrollmentController.getAllEnrollments())
        } else {
            println("La inscripción no existe para el estudiante 1 en el grupo 101.")
        }

        // Eliminar el grupo
        grupoController.deleteGrupo(1)
        println("Grupos después de la eliminación:")
        println(grupoController.getAllGrupos())

        // Eliminar el profesor
        teacherController.deleteTeacher(1)
        println("Profesores después de la eliminación:")
        println(teacherController.getAllTeachers())

        // Eliminar el curso
        courseController.deleteCourse(1)
        println(courseController.getAllCourses())

        // Eliminar el estudiante
        studentController.deleteStudent(1)
        println("Estudiantes después de la eliminación:")
        println(studentController.getAllStudents())

        // Eliminar la carrera
        careerController.deleteCareer(1)
        println(careerController.getAllCareers())

        // Eliminar el ciclo
        cicloController.deleteCiclo(1)
        println(cicloController.getAllCiclos())

    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}



