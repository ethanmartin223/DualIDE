import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PythonSyntaxHighlighter extends DocumentFilter {

    private final JTextPane textPane;

    private final String currentWord;
    private final int lastWordEndPos;
    private final int currentPos;


    String[] SYNTAX_TYPES = {"int", "str", "list", "set", "dict", "tuple", "bool", "float", "bytes",
            "memoryview", "bytearray", "object"};

    String[] SYNTAX_KEYWORDS = {"and", "exec", "not", "assert", "finally",
            "or", "break", "for", "pass", "from", "in",
            "print", "continue", "global", "raise", "def",
            "if", "return", "del", "import", "try", "is",
            "match", "case", "async", "while"};

    String[] SYNTAX_FUNCTIONS = {"abs", "aiter", "all", "any", "anext", "ascii",
            "breakpoint", "bin", "callable", "chr", "classmethod",
            "compile", "complex", "delattr", "dir", "divmod", "enumerate", "eval",
            "exec", "exit", "filter", "format", "frozenset", "getattr", "globals",
            "hasattr", "hash", "help", "hex", "id", "input", "isinstance",
            "issubclass", "iter", "len", "locals", "map", "max",
            "min", "next", "oct", "open", "ord", "pow", "print", "property",
            "range", "repr", "reversed", "round", "setattr", "slice", "sorted",
            "staticmethod", "sum", "super", "type", "vars", "zip"};

    String[] SYNTAX_OPERATIONS = {"\\=", "\\+", "\\-", "\\/", "\\/\\/", "\\%", "\\*", "\\*\\*", "\\>", "\\<", "\\!\\=", "\\&", "\\^", "\\|"};

    String[] SYNTAX_COMMENT = {"\\#"};

    String[] SYNTAX_BOOL = {"True", "False", "None", "NotImplemented"};

    String[] SYNTAX_CLASS = {"class"};

    String[] SYNTAX_SELF = {"self"};

    String[] SYNTAX_DUNDER_METHODS = {"__abs__", "__add__", "__and__", "__call__", "__class__", "__cmp__",
            "__coerce__", "__complex__", "__contains__", "__del__", "__delattr__",
            "__delete__", "__delitem__", "__delslice__", "__dict__", "__div__",
            "__divmod__", "__eq__", "__float__", "__floordiv__", "__ge__", "__get__",
            "__getattr__", "__getattribute__", "__getitem__", "__getslice__", "__gt__",
            "__hash__", "__hex__", "__iadd__", "__iand__", "__idiv__", "__ifloordiv__",
            "__ilshift__", "__imod__", "__imul__", "__index__", "__init__",
            "__instancecheck__", "__int__", "__invert__", "__ior__", "__ipow__",
            "__irshift__", "__isub__", "__iter__", "__itruediv__", "__ixor__", "__le__",
            "__len__", "__long__", "__lshift__", "__lt__", "__metaclass__", "__mod__",
            "__mro__", "__mul__", "__ne__", "__neg__", "__new__", "__nonzero__",
            "__oct__", "__or__", "__pos__", "__pow__", "__radd__", "__rand__", "__rcmp__",
            "__rdiv__", "__rdivmod__", "__repr__", "__reversed__", "__rfloordiv__", "__rlshift__",
            "__rmod__", "__rmul__", "__ror__", "__rpow__", "__rrshift__", "__rshift__", "__rsub__",
            "__rtruediv__", "__rxor__", "__set__", "__setattr__", "__setitem__",
            "__setslice__", "__slots__", "__str__", "__sub__", "__subclasscheck__", "__truediv__",
            "__unicode__", "__weakref__", "__xor__"};

    String[] SYNTAX_EXCEPTIONS = {"Exception", "StopIteration", "SystemExit", "StandardError", "ArithmeticError",
            "OverflowError", "FloatingPointError", "ZeroDivisionError", "AssertionError",
            "AttributeError", "EOFError", "ImportError", "KeyboardInterrupt", "LookupError",
            "IndexError", "KeyError", "NameError", "UnboundLocalError", "EnvironmentError",
            "IOError", "OSError", "SyntaxError", "IndentationError", "SystemError", "SystemExit", "TypeError",
            "ValueError", "RuntimeError", "NotImplementedError"};

    String[] SYNTAX_STRINGS = {"\"", "'"};

    private final Style highlighter;
    private final Style clear;
    private final CodeEditor editorParent;

    private final Color SYNTAX_TYPE_COLOR = Color.decode("#845692");
    private final Color SYNTAX_KEYWORDS_COLOR = Color.decode("#0079f2");
    private final Color SYNTAX_FUNCTIONS_COLOR = Color.decode("#c9c236");
    private final Color SYNTAX_OPERATIONS_COLOR = Color.decode("#9fc2e5");
    private final Color SYNTAX_COMMENT_COLOR = Color.decode("#0c6324");
    private final Color SYNTAX_BOOL_COLOR = Color.decode("#9fc2e5");
    private final Color SYNTAX_CLASS_COLOR = Color.decode("#ff3154");
    private final Color SYNTAX_SELF_COLOR = Color.decode("#ff004c");
    private final Color SYNTAX_STRING_COLOR = Color.decode("#dea670");
    private final Color SYNTAX_ERROR_COLOR = Color.decode("#ff004c");
    private final Color EDITOR_TEXT_COLOR = Color.decode("#BCBEC4");

    public PythonSyntaxHighlighter(CodeEditor parent, JTextPane textPane) {

        editorParent = parent;
        highlighter = textPane.addStyle("syntaxHighlighter", null);

        this.textPane = textPane;
        lastWordEndPos = 0;
        currentPos = 0;
        currentWord = "";

        clear = textPane.addStyle("syntaxClear", null);
        StyleConstants.setForeground(clear, EDITOR_TEXT_COLOR);
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
        super.insertString(fb, offset, text, attr);
        syntaxHighlight();
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        super.remove(fb, offset, length);
        syntaxHighlight();
    }

    public static void setTabs(int charactersPerTab, JTextPane textpane) {
        charactersPerTab--;

        FontMetrics fm = textpane.getFontMetrics(textpane.getFont());
        int charWidth = fm.charWidth('w');
        int tabWidth = charWidth * charactersPerTab;

        TabStop[] tabs = new TabStop[10];

        for (int j = 0; j < tabs.length; j++) {
            int tab = j + 1;
            tabs[j] = new TabStop(tab * tabWidth);
        }

        TabSet tabSet = new TabSet(tabs);
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setTabSet(attributes, tabSet);
        int length = textpane.getDocument().getLength();
        textpane.getStyledDocument().setParagraphAttributes(0, length,
                attributes, false);
    }


    public void highlightPattern(String pat, Color highlight) throws BadLocationException {
        Pattern pattern = Pattern.compile(pat);
        Document doc = textPane.getDocument();
        Matcher matcher = pattern.matcher(doc.getText(0,doc.getLength()));
        while(matcher.find()){
            int start = matcher.start();
            int end = matcher.end();
            StyleConstants.setForeground(highlighter, highlight);
            textPane.getStyledDocument().setCharacterAttributes(start, (end-start), highlighter, true);
        }
    }

    public void syntaxHighlight() throws BadLocationException {
        long sTime = System.currentTimeMillis();
        MutableAttributeSet mas = textPane.getInputAttributes();
        mas.removeAttributes(mas);
        textPane.removeStyle("syntaxHighlighter");
        textPane.getStyledDocument().setCharacterAttributes(0, textPane.getText().length(), clear, true);

        for (String s : SYNTAX_TYPES) highlightPattern("\\b"+s+"\\b", SYNTAX_TYPE_COLOR);
        for (String s : SYNTAX_BOOL) highlightPattern("\\b"+s+"\\b", SYNTAX_BOOL_COLOR);
        for (String s : SYNTAX_KEYWORDS) highlightPattern("\\b"+s+"\\b", SYNTAX_KEYWORDS_COLOR);
        for (String s : SYNTAX_CLASS) highlightPattern("\\b"+s+"\\b", SYNTAX_CLASS_COLOR);
        for (String s : SYNTAX_COMMENT) highlightPattern("\\b"+s+"\\b", SYNTAX_COMMENT_COLOR);
        for (String s : SYNTAX_EXCEPTIONS) highlightPattern("\\b"+s+"\\b", SYNTAX_ERROR_COLOR);
        for (String s : SYNTAX_OPERATIONS) highlightPattern(s,SYNTAX_OPERATIONS_COLOR);
        for (String s : SYNTAX_SELF) highlightPattern("\\b"+s+"\\b", SYNTAX_SELF_COLOR);
        for (String s : SYNTAX_FUNCTIONS) highlightPattern("\\b"+s+"\\b",SYNTAX_FUNCTIONS_COLOR);

        highlightPattern("\\#.*?$", SYNTAX_COMMENT_COLOR);
        highlightPattern("\"[^\"]*?(\"|\\z)", SYNTAX_STRING_COLOR);

        long eTime = System.currentTimeMillis();
        editorParent.TESTING_updatePrefMon("Last Update: "+(eTime-sTime)+"ms");
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        super.replace(fb,offset,length,text,attrs);
        syntaxHighlight();
        setTabs(3,textPane);
    }
}
