package com.github.sahsenvar.mvibundlegenerator.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileFactory

class GenerateMviBundleAction : AnAction("Zad MVI Bundle") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val view = e.getData(LangDataKeys.IDE_VIEW) ?: return
        val directory = view.orChooseDirectory ?: return

        val prefix = Messages.showInputDialog(
            project,
            "Enter prefix:",
            "Zad MVI Bundle",
            Messages.getQuestionIcon()
        ) ?: return

        createFile(project, directory, "${prefix}UiState.kt", "data class ${prefix}UiState")
        createFile(project, directory, "${prefix}UiIntent.kt", "sealed class ${prefix}UiIntent")
        createFile(project, directory, "${prefix}Screen.kt", "interface ${prefix}Screen")
        createFile(project, directory, "${prefix}ViewModel.kt", "class ${prefix}ViewModel")
    }

    override fun update(e: AnActionEvent) {
        val view = e.getData(LangDataKeys.IDE_VIEW)
        e.presentation.isEnabledAndVisible = view?.directories?.isNotEmpty() == true
    }

    private fun createFile(project: Project, directory: PsiDirectory, fileName: String, declaration: String) {
        val pkg = JavaDirectoryService.getInstance().getPackage(directory)?.qualifiedName
        val content = buildString {
            if (!pkg.isNullOrEmpty()) {
                append("package $pkg\n\n")
            }
            append(declaration)
            append('\n')
        }
        val fileType = FileTypeManager.getInstance().getFileTypeByExtension("kt")
        val file = PsiFileFactory.getInstance(project).createFileFromText(fileName, fileType, content)
        directory.add(file)
    }
}
