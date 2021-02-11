
package python;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PythonLexer {
    private final String input;
    private final int length;
    private StringBuilder word;
    private int index;
    public Token token;

    public static class Token {
        static Token START = new Token(Rule.START, "");
        static Token EOF = new Token(Rule.EOF, "");

        public final Rule rule;
        public final String text;

        public Token(final Rule rule, final String text) {
            this.rule = rule;
            this.text = text;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "rule=" + rule +
                    ", text='" + text + '\'' +
                    '}';
        }
    }
    
    private static class TokenRule {
        private final Rule rule;
        private final Pattern pattern;
        
        private TokenRule(final Rule rule, final Pattern pattern) {
            this.rule = rule;
            this.pattern = pattern;
        }
    }
    
    public static class LexicalException extends RuntimeException {
        public LexicalException(final String message) {
            super(message);
        }
    }
    
    private final Pattern allPatterns = Pattern.compile("([a-zA-Z]+)|(@)|(,)|(\\()|(\\))|(def)|([a-zA-Z]+)|([\\s\\n\\t])");
    
    private final List<TokenRule> tokenRules = Arrays.asList(
            new TokenRule(Rule.AT, Pattern.compile("@")),
            new TokenRule(Rule.COMMA, Pattern.compile(",")),
            new TokenRule(Rule.L_BRACE, Pattern.compile("\\(")),
            new TokenRule(Rule.R_BRACE, Pattern.compile("\\)")),
            new TokenRule(Rule.DEF, Pattern.compile("def")),
            new TokenRule(Rule.ID, Pattern.compile("[a-zA-Z]+")),
            new TokenRule(Rule.WS, Pattern.compile("[\\s\\n\\t]")),
            new TokenRule(Rule.ANY_ID, Pattern.compile("[a-zA-Z]+"))    
    );
    
    private final Set<Rule> skipTokens = Set.of(
            Rule.WS
    );
    
    private Rule findTokenRule(final String regex) {
        for (final TokenRule tokenRule : tokenRules) {
            if (tokenRule.pattern.matcher(regex).matches()) {
                return tokenRule.rule;
            }
        }
        return Rule.START;
    }
    
    public PythonLexer(final String input) {
        this.input = input;
        this.word = new StringBuilder();
        this.token = Token.START;
        this.index = 0;
        this.length = input.length();
    }
    
    public int getPosition() {
        return index;
    }
    
    public void nextToken() {
        word = new StringBuilder();
        if (index == length) {
            token = Token.EOF;
            return;
        }
        char c = input.charAt(index);
        index++;
        word.append(c);
        String text = word.toString();
        Rule foundRule = findTokenRule(text);
        Matcher m = allPatterns.matcher(text);
        if (m.matches()) {
            while (index < length) {
                c = input.charAt(index);
                index++;
                word.append(c);
                text = word.toString();
                foundRule = findTokenRule(text);
                m = allPatterns.matcher(text);
                if (!m.matches()) {
                    index--;
                    text = text.substring(0, text.length() - 1);
                    foundRule = findTokenRule(text);
                    if (skipTokens.contains(foundRule)) {
                        nextToken();
                        return;
                    }
                    break;
                }
            }
            if (foundRule == Rule.ANY_ID) {
                throw new LexicalException("Unexpected token " + word.toString());
            }
            token = new Token(foundRule, text);
        } else {
            throw new LexicalException("Unexpected token " + word.toString());
        }
    }
}
            