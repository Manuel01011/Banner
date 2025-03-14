package com.example.banner

import CareerController
import CicloController
import CourseController
import EnrollmentController
import Grupo
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
    val studentController = StudentController()
    val teacherController = TeacherController()
    val grupoController = GrupoController()
    //val enrollmentController = EnrollmentController()

    try {
        // Suponiendo que tienes un studentId (por ejemplo, 1 para Alice)
//        val studentId = 1
//
//        // Crear una instancia del controlador que maneja los cursos
//        val enrollmentController = EnrollmentController()
//
//        // Llamar al método para obtener los cursos activos del ciclo
//        val courses = enrollmentController.getActiveCycleCourses(1)
//
//        // Imprimir los resultados
//        if (enrollmentController.getActiveCycleCourses(1).isNotEmpty()) {
//            println("Cursos activos para el estudiante con ID $studentId:")
//            enrollmentController.getActiveCycleCourses(1).forEach { course ->
//                println("Código: ${course.cod}, Nombre: ${course.name}, Créditos: ${course.credits}, Horas: ${course.hours}")
//            }
//        } else {
//            println("No hay cursos activos para el estudiante con ID $studentId.")
//        }
      //println(courseController.searchCourses(null,null,1)) //#Funcionalidad 1

//      println(careerController.getCareerByNameAndCode(null,1)) //#Funcionalidad 2
//       val success = careerController.editCareer(       //     #Funcionalidad 2
//            cod = 1,
//            name = "Ingeniería en Sistemas",
//            title = "Bachiller en  Ingeniería en Sistemas",
//            coursesToAdd = listOf(201),
//            coursesToRemove = listOf(2)
//        )
//
//        if (success) {
//            println("Carrera actualizada con éxito")
//        } else {
//            println("Error al actualizar la carrera")
//        }
//
//        println(careerController.getCareerByNameAndCode(null,1)) //#Funcionalidad 2


        // println(teacherController.buscar_profesor(null,1));#Funcionalidad 3

       //print(studentController.buscar_alumno("Alice",1,null))#Funcionalidad 4
        /*
        val success = studentController.buscar_alumno("Bob",null,null)
        println(success)
            if(success!=null){
                println("¿Quieres ver el historial académico del estudiante? 1 = sí, 0 = no")
                val respuesta = readLine()?.toIntOrNull() ?: -1
                if(respuesta == 1){
                    println("Es necesario el el id del estudiante")
                    val respuesta2 = readLine()?.toIntOrNull() ?: -1
                    println(studentController.alumno_historial(respuesta2))
                }
            }

         */
        // println(cicloController.ciclo_anio(2025))#Funcionalidad 5
   //     println(cicloController.getActiveCiclo()) //#Funcionalidad 5
 //       val success = cicloController.setDisActiveCiclo(3)  // Activar el ciclo con ID 3
//        val success = cicloController.setDisActiveCiclo(3) // desactivar el ciclo con ID 3
//        if (success) {
//            println("Ciclo 3 desactivado correctamente")
//        } else {
//            println("Error al desactivar el ciclo")
//        }

        //println(courseController.getCoursesByCareerAndCycle(1,1))//funcionalidad #6
        //println("Ingrese el id del curso que desea ver todos los grupos disponibles")
        //val respuesta = readLine()?.toIntOrNull() ?: -1
       // println(grupoController.getGroupsByCourse(respuesta))

        //el id en 0 si queremos ingresar un grupo nuevo #Funcionalidad 6
       // grupoController.addOrUpdateGrupo(0, 12, 2024, "Mar-Vier 7:00-8:40", 102, 1)
        //id del grupo existente que queremos agregar
       // grupoController.addOrUpdateGrupo(2, 22, 2025, "Lunes 2:00-3:40", 101, 1)


    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}



