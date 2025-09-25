package finalproject.model;

public enum SeatLayoutType {
    BUS_2_1   ("2+1",    3, new int[]{2,1}), // Two seats on one side, one on the other
    BUS_2_2   ("2+2",    4, new int[]{2,2}), // Two seats on both sides
    PLANE_3_3 ("3+3",    6, new int[]{3,3}), // Three seats, aisle, three seats
    PLANE_2_4_2("2+4+2", 8, new int[]{2,4,2});  // Two seats, aisle, four seats, aisle, two seats

    private final String label;
    private final int columns;
    private final int[] blocks; // Array representing seat blocks separated by aisles

    SeatLayoutType(String label, int columns, int[] blocks) {
        this.label   = label;
        this.columns = columns;
        this.blocks  = blocks;
    }

    public int   getColumns() { return columns; }
    public int[] getBlocks()  { return blocks; }   
    @Override public String toString() { return label; }
}
