package org.jetbrains.plugins.feature.suggester.suggesters.lang

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.descendantsOfType
import com.jetbrains.python.psi.PyAssignmentStatement
import com.jetbrains.python.psi.PyClass
import com.jetbrains.python.psi.PyExpression
import com.jetbrains.python.psi.PyExpressionStatement
import com.jetbrains.python.psi.PyForStatement
import com.jetbrains.python.psi.PyFunction
import com.jetbrains.python.psi.PyIfStatement
import com.jetbrains.python.psi.PyStatement
import com.jetbrains.python.psi.PyStatementList
import com.jetbrains.python.psi.PyStringLiteralExpression
import com.jetbrains.python.psi.PyTargetExpression
import com.jetbrains.python.psi.PyWhileStatement
import org.jetbrains.plugins.feature.suggester.getParentByPredicate
import org.jetbrains.plugins.feature.suggester.getParentOfType

class PythonLanguageSupport : LanguageSupport {

    override fun isIfStatement(element: PsiElement): Boolean {
        return element is PyIfStatement
    }

    override fun isForStatement(element: PsiElement): Boolean {
        return element is PyForStatement
    }

    override fun isWhileStatement(element: PsiElement): Boolean {
        return element is PyWhileStatement
    }

    override fun isCodeBlock(element: PsiElement): Boolean {
        return element is PyStatementList
    }

    override fun getCodeBlock(element: PsiElement): PsiElement? {
        return element.descendantsOfType<PyStatementList>().firstOrNull()
    }

    override fun getContainingCodeBlock(element: PsiElement): PsiElement? {
        return element.getParentOfType<PyStatementList>()
    }

    override fun getParentStatementOfBlock(element: PsiElement): PsiElement? {
        return element.parent?.parent
    }

    override fun getStatements(element: PsiElement): List<PsiElement> {
        return if (element is PyStatementList) {
            element.statements.toList()
        } else {
            emptyList()
        }
    }

    override fun getTopmostStatementWithText(psiElement: PsiElement, text: String): PsiElement? {
        return psiElement.getParentByPredicate {
            isSupportedStatementToIntroduceVariable(it) && it.text.contains(text) && it.text != text
        }
    }

    override fun isSupportedStatementToIntroduceVariable(element: PsiElement): Boolean {
        return element is PyStatement
    }

    override fun isPartOfExpression(element: PsiElement): Boolean {
        return element.getParentOfType<PyExpression>() != null
    }

    override fun isExpressionStatement(element: PsiElement): Boolean {
        return element is PyExpressionStatement
    }

    override fun isVariableDeclaration(element: PsiElement): Boolean {
        return element is PyAssignmentStatement
    }

    override fun getVariableName(element: PsiElement): String? {
        return if (element is PyAssignmentStatement) {
            element.targets.firstOrNull()?.name
        } else {
            null
        }
    }

    override fun isFileStructureElement(element: PsiElement): Boolean {
        return (element is PyTargetExpression && element.getParentOfType<PyFunction>() == null) ||
            element is PyFunction || element is PyClass
    }

    override fun isIdentifier(element: PsiElement): Boolean {
        return element is LeafPsiElement && element.elementType.toString() == "Py:IDENTIFIER"
    }

    override fun isLiteralExpression(element: PsiElement): Boolean {
        return element is PyStringLiteralExpression
    }
}
