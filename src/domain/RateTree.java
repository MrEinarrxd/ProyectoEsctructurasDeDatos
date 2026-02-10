package domain;

/**
 * Árbol Binario de Búsqueda (BST) para almacenar tarifas.
 * Organiza categorías de tarifas en orden alfabético para búsqueda eficiente.
 * Operaciones:
 * - add: O(log n) promedio, O(n) peor caso
 * - search: O(log n) promedio, O(n) peor caso
 */
public class RateTree {
    /**
     * Nodo del árbol de tarifas.
     * Contiene categoría (clave) y precio (valor).
     */
    static class RateNode {
        String category;
        double price;
        RateNode left, right;
    }
    private RateNode root;

    /**
     * Agrega una nueva tarifa al árbol.
     */
    public void add(String category, double price) {
        root = addRec(root, category, price);
    }

    /**
     * Método recursivo para agregar nodo en posición correcta.
     * Mantiene propiedad BST: izquierda < nodo < derecha (orden alfabético).
     */
    private RateNode addRec(RateNode node, String category, double price) {
        if (node == null) {
            node = new RateNode();
            node.category = category;
            node.price = price;
            return node;
        }

        // Insertar en subárbol izquierdo si es menor alfabéticamente
        if (category.compareTo(node.category) < 0) {
            node.left = addRec(node.left, category, price);
        } else if (category.compareTo(node.category) > 0) {
            // Insertar en subárbol derecho si es mayor alfabéticamente
            node.right = addRec(node.right, category, price);
        }
        // Si son iguales, no hacer nada (no permite duplicados)
        return node;
    }

    /**
     * Busca el precio de una categoría específica.
     */
    public double search(String category) {
        return searchRec(root, category);
    }

    /**
     * Búsqueda recursiva en el árbol.
     * Aprovecha propiedad BST para búsqueda eficiente.
     */
    private double searchRec(RateNode node, String category) {
        if (node == null) return 0.0; // No encontrado
        if (category.equals(node.category)) return node.price; // Encontrado
        if (category.compareTo(node.category) < 0) {
            return searchRec(node.left, category); // Buscar en izquierda
        }
        return searchRec(node.right, category); // Buscar en derecha
    }
}
