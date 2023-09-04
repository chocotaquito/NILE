import java.util.ArrayList;
import java.util.HashMap;

public class Controller {
    // This file acts as a handler for the GUI events
    Service service = new Service();
    Response find(String ID, String quantity){
        int quant;
        // Checking for valid input
        if (ID.isEmpty()){
            return new Response(500, Consts.errorID);
        }
        try{
            quant = Integer.parseInt(quantity);
        }
        catch(NumberFormatException e){
            String message = "'"+quantity + "' is not a valid quantity";
            if (quantity.isEmpty()){
                message = "Quantity field cannot be empty";
            }
            return new Response(500, message);
        }
        //Get data from our database
        return service.fetchItem(ID, quant);
    }
    Response updateQuantities(HashMap<String, Item> cartHash, ArrayList<Item> cart){
        service.updateItem(cartHash);
        service.updateTransactions(cart);
        return new Response(200, "");
    }
}
