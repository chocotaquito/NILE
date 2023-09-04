public class Response {
    // This class is meant to simulate JSON response packets
    // Having this class makes it a little easier to handle the response in our GUI

    // JavaEE does have included support for actual json objects, but since we aren't using that, I would have to
    //  create my own data structure from scratch which seems really overkill for this project
    // So creating this fake JSON response with all the values that we're using already defined is good enough
    private int status;
    private String message;
    private Item item;

    @Override
    public String toString() {
        if (item != null){
            return "Response{" +
                    "status=" + status +
                    ", message='" + message + '\'' +
                    ", item=(" + item.toString() +
                    ")}";
        }
        return "Response{" +
                "status=" + status +
                ", message='" + message + '\'' +
                "}";
    }

    //Require Both a status value and message
    public Response(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public Response(int status, String message, Item item) {
        this.status = status;
        this.message = message;
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
