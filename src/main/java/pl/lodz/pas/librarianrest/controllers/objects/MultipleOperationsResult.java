package pl.lodz.pas.librarianrest.controllers.objects;

public class MultipleOperationsResult {
    private int affectedItems;

    public MultipleOperationsResult(int count) {
        this.affectedItems = count;
    }

    public int getAffectedItems() {
        return affectedItems;
    }

    public void setAffectedItems(int affectedItems) {
        this.affectedItems = affectedItems;
    }
}
