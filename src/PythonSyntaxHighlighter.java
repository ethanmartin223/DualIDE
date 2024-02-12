import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.DefaultHighlighter.*;
import java.awt.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PythonSyntaxHighlighter extends DocumentFilter {

    private DefaultHighlightPainter highlightPainter = new DefaultHighlightPainter(Color.YELLOW);
    private JTextPane textPane;
    private SimpleAttributeSet background;

    private String currentWord;
    private int lastWordEndPos;
    private int currentPos;


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

    private Style highlighter;


    private Color SYNTAX_TYPE_COLOR, SYNTAX_KEYWORDS_COLOR, SYNTAX_FUNCTIONS_COLOR, SYNTAX_OPERATIONS_COLOR,SYNTAX_COMMENT_COLOR,
            SYNTAX_BOOL_COLOR, SYNTAX_CLASS_COLOR, SYNTAX_SELF_COLOR, SYNTAX_STRING_COLOR, SYNTAX_ERROR_COLOR, EDITOR_TEXT_COLOR;

    public PythonSyntaxHighlighter(JTextPane textPane) {

        highlighter = textPane.addStyle("syntaxHighlighter", null);

        this.textPane = textPane;
        lastWordEndPos = 0;
        currentPos = 0;
        currentWord = "";

        EDITOR_TEXT_COLOR = Color.BLACK;
        SYNTAX_KEYWORDS_COLOR = Color.decode("#0079f2");
        SYNTAX_FUNCTIONS_COLOR = Color.decode("#c9c236");
        SYNTAX_OPERATIONS_COLOR = Color.decode("#9fc2e5");
        SYNTAX_CLASS_COLOR = Color.decode("#ff3154");
        SYNTAX_SELF_COLOR = Color.decode("#ff004c");
        SYNTAX_ERROR_COLOR = Color.decode("#4437a6");
        SYNTAX_TYPE_COLOR = Color.decode("#845692");
        SYNTAX_COMMENT_COLOR = Color.decode("#0c6324");
        SYNTAX_BOOL_COLOR = Color.decode("#9fc2e5");
        SYNTAX_STRING_COLOR = Color.decode("#dea670");
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


    public void highLightWord(String pat, Color highlight) throws BadLocationException {
        Pattern pattern = Pattern.compile("\\b"+pat+"\\b");
        Matcher matcher = pattern.matcher(textPane.getText());
        while(matcher.find()){
            int start = matcher.start();
            int end = matcher.end();
            StyleConstants.setForeground(highlighter, highlight);
            textPane.getStyledDocument().setCharacterAttributes(start, end-start, highlighter, true);
            //textPane.getHighlighter().addHighlight(start, end, (Highlighter.HighlightPainter) highlighted);
        }

    }

    public void syntaxHighlight() throws BadLocationException {
        MutableAttributeSet mas = textPane.getInputAttributes();
        mas.removeAttributes(mas);
        textPane.removeStyle("syntaxHighlighter");
        for (String s : SYNTAX_TYPES) highLightWord(s, SYNTAX_TYPE_COLOR);
        for (String s : SYNTAX_BOOL) highLightWord(s, SYNTAX_BOOL_COLOR);
        for (String s : SYNTAX_KEYWORDS) highLightWord(s, SYNTAX_KEYWORDS_COLOR);
        for (String s : SYNTAX_CLASS) highLightWord(s, SYNTAX_CLASS_COLOR);
        for (String s : SYNTAX_COMMENT) highLightWord(s, SYNTAX_COMMENT_COLOR);
        for (String s : SYNTAX_EXCEPTIONS) highLightWord(s, SYNTAX_ERROR_COLOR);
        for (String s : SYNTAX_OPERATIONS) highLightWord(s,SYNTAX_OPERATIONS_COLOR);
        for (String s : SYNTAX_SELF) highLightWord(s, SYNTAX_SELF_COLOR);
        for (String s : SYNTAX_FUNCTIONS) highLightWord(s,SYNTAX_FUNCTIONS_COLOR);
//        highLightWord("\"[^\"]*?\"", SYNTAX_STRING_COLOR);

        Pattern pattern = Pattern.compile("\"[^\"]*?(\"|\\z)");
        Matcher matcher = pattern.matcher(textPane.getText());
        while(matcher.find()){
            int start = matcher.start();
            int end = matcher.end();
            StyleConstants.setForeground(highlighter, SYNTAX_STRING_COLOR);
            textPane.getStyledDocument().setCharacterAttributes(start, end-start, highlighter, true);
            //textPane.getHighlighter().addHighlight(start, end, (Highlighter.HighlightPainter) highlighted);
        }
    }


    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

        super.replace(fb,offset,length,text,attrs);
        syntaxHighlight();
        setTabs(3,textPane);
    }
}
