package dev.gelo.branchview

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryChangeListener

/** Перерисовывает Project View при смене ветки/состояния репозитория. */
class BranchRepoChangeListener(private val project: Project) : GitRepositoryChangeListener {

    override fun repositoryChanged(repository: GitRepository) {
        // Событие может прийти не на EDT — refresh обязан идти на UI-потоке.
        ApplicationManager.getApplication().invokeLater {
            if (!project.isDisposed) {
                ProjectView.getInstance(project).refresh()
            }
        }
    }
}
