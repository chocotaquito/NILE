public class Item {
    // Item entry for user's cart
    private String ID;
    private String name;
    private float price;
    private int quantity;
    private int stock;
    private int location; // Keeps track of place in cart, used for updating quantity when adding duplicate of same item

    public int getLocation() {
        return location;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return
                "" + ID + " " +
                '\"' + name + '\"' +
                " $" + String.format("%.2f", price) +
                " " + quantity +
                " " + getDiscount() +
                "% $" + String.format("%.2f", getTotal())
                ;
    }

    public Item(String ID, String name, float price, int quantity) {
        this.ID = ID;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    public Item() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getDiscount() {
        int quantity = getQuantity();
        if (quantity >= 15){
            return 20;
        }
        if (quantity > 9){
            return 15;
        }
        if (quantity > 4){
            return 10;
        }
        return 0;
    }
    public float getTotal() {
       // return quantity*price;
        float val = quantity*price;
        val -= val/100;
        return (float)((int)Math.round((val*100)))/100;
    }
}
