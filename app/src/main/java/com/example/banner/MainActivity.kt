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
      // println(courseController.searchCourses(null,null,2)) #Funcionalidad 1

      //println(careerController.getCareerByNameAndCode(null,1)) #Funcionalidad 2
       /* val success = careerController.editCareer(            #Funcionalidad 2
            cod = 1,
            name = "Ingeniería en Software",
            title = "Licenciatura en Software",
            coursesToAdd = listOf(201),
            coursesToRemove = listOf(2)
        )

        if (success) {
            println("Carrera actualizada con éxito")
        } else {
            println("Error al actualizar la carrera")
        }

        */

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
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}



