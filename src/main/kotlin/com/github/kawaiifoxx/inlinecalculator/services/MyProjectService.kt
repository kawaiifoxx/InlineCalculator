package com.github.kawaiifoxx.inlinecalculator.services

import com.intellij.openapi.project.Project
import com.github.kawaiifoxx.inlinecalculator.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
