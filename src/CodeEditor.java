import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DefaultStyledDocument;
import java.awt.*;

public class CodeEditor {
    private JTextPane editorPane1;
    private JPanel codeEditorContainer;
    private JPanel editorLineCounterContainer;
    private JList<Integer> lineCounter;
    private JToolBar.Separator lineCounterSeparator;
    private JScrollPane editorScrollPane;
    private JList list1;
    private JLabel prefMonLabel;

    public CodeEditor() {
        editorPane1.setDocument(new DefaultStyledDocument());
        ((AbstractDocument) editorPane1.getDocument()).setDocumentFilter(new PythonSyntaxHighlighter(this, editorPane1));
        editorPane1.setFont(new Font("Consolas", Font.PLAIN, 16));

    }

    void TESTING_updatePrefMon(String text){
        prefMonLabel.setText(text);
    }
}
