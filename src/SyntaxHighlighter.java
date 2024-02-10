import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.DefaultHighlighter.*;
import java.awt.*;
import java.util.Arrays;
import java.util.regex.*;

public class SyntaxHighlighter extends DocumentFilter {

    private DefaultHighlightPainter highlightPainter = new DefaultHighlightPainter(Color.YELLOW);
    private JTextPane textPane;
    private SimpleAttributeSet background;

    private String currentWord;
    private int lastWordEndPos;
    private int currentPos;

    private Color SYNTAX_TYPE_COLOR, SYNTAX_KEYWORDS_COLOR, SYNTAX_FUNCTIONS_COLOR, SYNTAX_OPERATIONS_COLOR,SYNTAX_COMMENT_COLOR,
            SYNTAX_BOOL_COLOR, SYNTAX_CLASS_COLOR, SYNTAX_SELF_COLOR, SYNTAX_STRING_COLOR, SYNTAX_ERROR_COLOR, EDITOR_TEXT_COLOR;

    public SyntaxHighlighter(JTextPane textPane) {
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
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        System.out.println("remove");
        super.remove(fb, offset, length);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

        super.replace(fb,offset,length,text,attrs);
        String[] content = textPane.getText().split(" ");

        for (int i=0; i<content.length; i++) {
            for content[i]
        }

        // i was making so if you press space you have to reload the doc and add highlighting. just go through all of the owrds and do a check if they dhould be highlighted

//        StyleConstants.setForeground((MutableAttributeSet) attrs, EDITOR_TEXT_COLOR);
//        textPane.getStyledDocument().insertString(offset,text,attrs);

    }
}
