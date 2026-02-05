package domain;

import java.util.HashMap;
import java.util.Map;

public class RateTree {
    private Map<String, Double> rates;

    public RateTree() {
        this.rates = new HashMap<>();
    }

    public void add(String category, double price) {
        rates.put(category, price);
    }

    public double search(String category) {
        return rates.getOrDefault(category, 0.0);
    }

    public boolean contains(String category) {
        return rates.containsKey(category);
    }

    public void remove(String category) {
        rates.remove(category);
    }

    public int size() {
        return rates.size();
    }
}
