package view;

public class ComboItem {
    private final int id;
    private final String label;

    public ComboItem(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ComboItem other = (ComboItem) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
