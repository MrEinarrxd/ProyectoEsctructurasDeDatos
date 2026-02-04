package domain;

public class RateTree {
    class RateNode {
        String category;
        double price;
        RateNode left, right;
    }
    RateNode root;

    public void add(String category, double price) {
        root = addRec(root, category, price);
    }

    private RateNode addRec(RateNode node, String category, double price) {
        if (node == null) {
            node = new RateNode();
            node.category = category;
            node.price = price;
            return node;
        }

        if (category.compareTo(node.category) < 0) {
            node.left = addRec(node.left, category, price);
        } else if (category.compareTo(node.category) > 0) {
            node.right = addRec(node.right, category, price);
        }
        return node;
    }

    public double search(String category) {
        return searchRec(root, category);
    }

    private double searchRec(RateNode node, String category) {
        if (node == null) return 0.0;
        if (category.equals(node.category)) return node.price;
        if (category.compareTo(node.category) < 0) {
            return searchRec(node.left, category);
        }
        return searchRec(node.right, category);
    }
}
