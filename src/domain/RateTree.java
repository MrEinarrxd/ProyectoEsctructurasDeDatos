package domain;

// Árbol AVL para almacenar tarifas de transporte
public class RateTree {
    private Node root;
    private int size;

    private static class Node {
        String key;
        double value;
        Node left, right;
        int height;

        Node(String key, double value) {
            this.key = key;
            this.value = value;
            this.height = 1;
        }
    }

    public RateTree() {
        this.root = null;
        this.size = 0;
    }

    // Obtener altura del nodo
    private int getHeight(Node node) {
        return node == null ? 0 : node.height;
    }

    // Obtener factor de balance
    private int getBalance(Node node) {
        return node == null ? 0 : getHeight(node.left) - getHeight(node.right);
    }

    // Actualizar altura del nodo
    private void updateHeight(Node node) {
        if (node != null) {
            node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
        }
    }

    // Rotación a la derecha
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    // Rotación a la izquierda
    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    // Insertar categoría y precio
    public void add(String category, double price) {
        root = insertNode(root, category, price);
    }

    private Node insertNode(Node node, String key, double value) {
        if (node == null) {
            size++;
            return new Node(key, value);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insertNode(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insertNode(node.right, key, value);
        } else {
            node.value = value;
            return node;
        }

        updateHeight(node);
        int balance = getBalance(node);

        // Caso izquierda-izquierda
        if (balance > 1 && key.compareTo(node.left.key) < 0) {
            return rotateRight(node);
        }

        // Caso derecha-derecha
        if (balance < -1 && key.compareTo(node.right.key) > 0) {
            return rotateLeft(node);
        }

        // Caso izquierda-derecha
        if (balance > 1 && key.compareTo(node.left.key) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Caso derecha-izquierda
        if (balance < -1 && key.compareTo(node.right.key) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    // Buscar precio por categoría
    public double search(String category) {
        Node node = searchNode(root, category);
        return node != null ? node.value : 0.0;
    }

    private Node searchNode(Node node, String key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return searchNode(node.left, key);
        } else if (cmp > 0) {
            return searchNode(node.right, key);
        } else {
            return node;
        }
    }

    // Verificar si existe una categoría
    public boolean contains(String category) {
        return searchNode(root, category) != null;
    }

    // Eliminar categoría
    public void remove(String category) {
        if (contains(category)) {
            root = removeNode(root, category);
            size--;
        }
    }

    private Node removeNode(Node node, String key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = removeNode(node.left, key);
        } else if (cmp > 0) {
            node.right = removeNode(node.right, key);
        } else {
            // Nodo sin hijos (hoja)
            if (node.left == null && node.right == null) {
                return null;
            }
            // Nodo con un hijo
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            // Nodo con dos hijos
            Node minRight = findMin(node.right);
            node.key = minRight.key;
            node.value = minRight.value;
            node.right = removeNode(node.right, minRight.key);
        }

        updateHeight(node);
        int balance = getBalance(node);

        // Caso izquierda-izquierda
        if (balance > 1 && getBalance(node.left) >= 0) {
            return rotateRight(node);
        }

        // Caso izquierda-derecha
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Caso derecha-derecha
        if (balance < -1 && getBalance(node.right) <= 0) {
            return rotateLeft(node);
        }

        // Caso derecha-izquierda
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    // Encontrar nodo mínimo
    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // Obtener cantidad de elementos
    public int size() {
        return size;
    }
}
