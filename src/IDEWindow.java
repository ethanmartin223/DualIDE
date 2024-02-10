import javax.swing.*;

public class IDEWindow extends JFrame {
    private JPanel mainWindowContainer;
    private JPanel mainEditorContainer;
    private JPanel mainEditorToolbarContainer;
    private JTree fileTree;
    private JTabbedPane editorTabPanel;
    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu navigateMenu;
    private JMenu codeMenu;
    private JMenu runMenu;
    private JMenu helpMenu;
    private JMenuBar mainEditorToolbar;
    private CodeEditor activeCodeEditor;
    private JPanel testFileTab;
    private JSplitPane editorDragSplitter;
    private JSplitPane runDragSplitter;
    private JPanel mainRunContainer;
    private JTextArea runOutputText;
    private JButton runButton;
    private JPanel treeContainer;

    public IDEWindow() {
        setTitle("DualIDE");
        setContentPane(mainWindowContainer);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);

    }

    public static void main(String[] args) {
        new IDEWindow();
    }

}
