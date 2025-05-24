package com.example.banner.frontend.views.career

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.backend_banner.backend.Models.Course_
import com.example.banner.R

class CourseAdapter(
    private val context: Context,
    private val courses: MutableList<Course_>,
    private val onRemoveClick: (Course_) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = courses.size

    override fun getItem(position: Int): Course_ = courses[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.course_list_item, parent, false)

        val courseNameTextView = view.findViewById<TextView>(R.id.courseNameTextView)
        val removeButton = view.findViewById<Button>(R.id.removeCourseButton)

        val course = getItem(position)
        courseNameTextView.text = course.name

        removeButton.setOnClickListener {
            onRemoveClick(course)
        }

        return view
    }
}