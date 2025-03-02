package it.luciorizzi.scacchi.model.movement;

public record Position(int row, int column) {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return row == position.row && column == position.column;
    }

    @Override
    public String toString() {
        return "Position{" + "row=" + row + ", column=" + column + '}';
    }

    @Override
    public int hashCode() {
        return 31 * row + column;
    }
}
