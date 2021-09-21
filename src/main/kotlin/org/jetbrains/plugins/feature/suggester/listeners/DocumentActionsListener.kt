package org.jetbrains.plugins.feature.suggester.listeners

import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.BulkAwareDocumentListener
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.guessProjectForFile
import org.jetbrains.plugins.feature.suggester.TextFragment
import org.jetbrains.plugins.feature.suggester.actions.Action
import org.jetbrains.plugins.feature.suggester.actions.BeforeEditorTextInsertedAction
import org.jetbrains.plugins.feature.suggester.actions.BeforeEditorTextRemovedAction
import org.jetbrains.plugins.feature.suggester.actions.EditorTextInsertedAction
import org.jetbrains.plugins.feature.suggester.actions.EditorTextRemovedAction
import org.jetbrains.plugins.feature.suggester.handleAction
import java.lang.ref.WeakReference

class DocumentActionsListener : BulkAwareDocumentListener {

    override fun beforeDocumentChangeNonBulk(event: DocumentEvent) {
        // Store in a weak reference, otherwise PsiDocumentManagerImplTest.testDoNotLeakForgottenUncommittedDocument will fail
        val eventRef = WeakReference(event)
        runInEdt {
            handleDocumentAction(
                eventRef = eventRef,
                textInsertedActionConstructor = ::BeforeEditorTextInsertedAction,
                textRemovedActionConstructor = ::BeforeEditorTextRemovedAction
            )
        }
    }

    override fun documentChangedNonBulk(event: DocumentEvent) {
        // Store in a weak reference, otherwise PsiDocumentManagerImplTest.testDoNotLeakForgottenUncommittedDocument will fail
        val eventRef = WeakReference(event)
        runInEdt {
            handleDocumentAction(
                eventRef = eventRef,
                textInsertedActionConstructor = ::EditorTextInsertedAction,
                textRemovedActionConstructor = ::EditorTextRemovedAction
            )
        }
    }

    private inline fun <T : Action> handleDocumentAction(
        eventRef: WeakReference<DocumentEvent>,
        textInsertedActionConstructor: (String, Int, Editor, Long) -> T,
        textRemovedActionConstructor: (TextFragment, Int, Editor, Long) -> T
    ) {
        val event = eventRef.get() ?: return
        val document = event.document
        val virtualFile = FileDocumentManager.getInstance().getFile(document) ?: return
        val project = guessProjectForFile(virtualFile) ?: return
        val editor = FileEditorManager.getInstance(project).getAllEditors(virtualFile)
            .mapNotNull { (it as? TextEditor)?.editor }.find { it.project == project } ?: return
        if (event.newFragment != "" && event.oldFragment == "") {
            handleAction(
                project,
                textInsertedActionConstructor(
                    event.newFragment.toString(),
                    event.offset,
                    editor,
                    System.currentTimeMillis()
                )
            )
        } else if (event.oldFragment != "" && event.newFragment == "") {
            handleAction(
                project,
                textRemovedActionConstructor(
                    TextFragment(
                        event.offset,
                        event.offset + event.oldLength,
                        event.oldFragment.toString()
                    ),
                    event.offset,
                    editor,
                    System.currentTimeMillis()
                )
            )
        }
    }
}
