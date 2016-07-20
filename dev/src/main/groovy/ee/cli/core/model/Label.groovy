package ee.cli.core.model

class Label implements Serializable {
    String category
    String name


    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Label label = (Label) o

        if (category != label.category) return false
        if (name != label.name) return false

        return true
    }

    int hashCode() {
        int result
        result = (category != null ? category.hashCode() : 0)
        result = 31 * result + (name != null ? name.hashCode() : 0)
        return result
    }

    @Override
    public String toString() {
        name
    }
}
