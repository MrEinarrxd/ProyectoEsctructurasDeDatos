package domain.List;

public class StringNode extends Node {
    public StringNode(String str) {
        super(str);
    }

    public String getString() {
        return (String) data;
    }
}
