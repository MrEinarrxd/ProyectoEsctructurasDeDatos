package domain.List;

public class HistoryStack extends BaseLinkedList {

    public void push(String item) {
        addFirst(item);
    }

    public String pop() {
        if (isEmpty()) {
            return null;
        }
        String data = (String) head.data;
        head = head.next;
        size--;
        return data;
    }

    public String peek() {
        return isEmpty() ? null : (String) head.data;
    }

    public StringList getAll() {
        StringList list = new StringList();
        Node current = head;
        while (current != null) {
            list.add((String) current.data);
            current = current.next;
        }
        return list;
    }
}
