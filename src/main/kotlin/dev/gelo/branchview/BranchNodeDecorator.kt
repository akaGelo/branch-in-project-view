package dev.gelo.branchview

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import com.intellij.ui.SimpleTextAttributes
import git4idea.repo.GitRepositoryManager

/** Ветки, которые считаем «основными» и не подсвечиваем. */
private val MAIN_BRANCHES = setOf("main", "master", "develop")

/** Дописывает текущую git-ветку к узлам Project View, совпадающим с корнем репозитория. */
class BranchNodeDecorator : ProjectViewNodeDecorator {

    override fun decorate(node: ProjectViewNode<*>, data: PresentationData) {
        val project = node.project ?: return
        if (project.isDisposed) return

        val dir = node.repositoryRootDir() ?: return
        // getRepositoryForRootQuick + поля репозитория — чтение кэша, без вызова git-процесса.
        val repo = GitRepositoryManager.getInstance(project).getRepositoryForRootQuick(dir) ?: return
        if (repo.root != dir) return // помечаем только сам VCS-рут, а не вложенные папки

        val branchName = repo.currentBranchName
        val label = branchName ?: repo.currentRevision?.take(7) ?: return // detached HEAD → короткий хэш
        val isMain = branchName != null && branchName in MAIN_BRANCHES

        val attrs = if (isMain) {
            SimpleTextAttributes.GRAYED_ATTRIBUTES
        } else {
            SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD, JBColor.ORANGE)
        }

        // Как только добавляем цветной фрагмент, имя узла надо вернуть руками — иначе оно пропадёт.
        if (data.coloredText.isEmpty()) {
            data.addText(data.presentableText ?: node.name ?: "", SimpleTextAttributes.REGULAR_ATTRIBUTES)
        }
        data.addText("  [$label]", attrs)
    }

    /** Папка узла: корень проекта или директория в дереве. */
    private fun ProjectViewNode<*>.repositoryRootDir(): VirtualFile? = when (val v = value) {
        is Project -> v.guessProjectDir()
        else -> (this as? PsiDirectoryNode)?.virtualFile
    }
}
