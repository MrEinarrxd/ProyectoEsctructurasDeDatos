package domain.List;

import java.util.ArrayList;
import java.util.List;

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

    public List<String> getAll() {
        List<String> list = new ArrayList<>();
        Node current = head;
        while (current != null) {
            list.add((String) current.data);
            current = current.next;
        }
        return list;
    }
}
