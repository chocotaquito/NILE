import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Service {
    // Service is used to extract the data from the database
    Response fetchItem(String ID, int quantity){
        File file = new File("./src/database/inventory.csv");
        int counter = 0;
        Item item = new Item();
        try{
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter(", |\\n");
            String currentString;
            Boolean idFound = false;
            while (scanner.hasNext()) {
                currentString = scanner.next();
                if (!idFound){
                    if (counter == 0){
                        if (ID.equals(currentString)){
                            item.setID(currentString);
                            idFound = true;
                        }
                    }
                }
                else{
                    switch (counter){
                        case 1:
                            item.setName(currentString);
                            break;
                        case 2:
                            // "in stock" boolean
                            if (!Boolean.parseBoolean(currentString)){
                                scanner.close();
                                return new Response(500, Consts.errorStock);
                            }
                            break;
                        case 3:
                            if (quantity > Integer.parseInt(currentString)){
                                scanner.close();
                                return new Response(500, "Insufficient stock. Only "+Integer.parseInt(currentString)+" on hand. Please reduce the quantity.");
                            }
                            item.setQuantity(quantity);
                            item.setStock(Integer.parseInt(currentString));
                            break;
                        case 4:
                            item.setPrice(Float.parseFloat(currentString));
                            break;
                        default:
                            scanner.close();
                            return new Response(200, "", item);
                    }
                }
                counter++;
                if (counter > 4){
                    counter = 0;
                }
            }
            scanner.close();
        }
        catch (FileNotFoundException e){
            return new Response(500, Consts.errorFile);
        }
        return new Response(500, "Item ID "+ ID + " not in file");
    }
    Response updateItem(HashMap<String, Item> cartHash){
        File file = new File("./src/database/inventory.csv");
        int counter = 0;
        Item item = new Item();
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter("./src/database/temp.csv"));
            try{
                Scanner scanner = new Scanner(file);
                scanner.useDelimiter(", |\\n");
                String currentString;
                String currentID = null;
                Boolean idFound = false;
                counter = 0;
                while (scanner.hasNext()) {
                    currentString = scanner.next();
                    if (counter == 0){
                        if (cartHash.containsKey(currentString)){
                            currentID = currentString;
                            idFound = true;
                        }
                        writer.write(currentString);
                    }
                    else if (counter == 2 && idFound){
                        // Skip over this line because we will be rewriting it
                        counter++;
                        continue;
                    }
                    else if (counter == 3 && idFound){
                        int quant = Integer.parseInt(currentString) - cartHash.get(currentID).getQuantity();
                        if (quant > 0){
                            writer.write("true");
                        }
                        if (quant <= 0){
                            writer.write("false");
                        }
                        writer.write(", ");
                        writer.write(""+quant);
                    }
                    else{
                        writer.write(currentString);
                    }
                    counter++;
                    if (counter > 4){
                        writer.write("\n");
                        idFound = false;
                        counter = 0;
                    }
                    else{
                        writer.write(", ");
                    }
                }
                writer.close();
                scanner.close();
                copyFiles(new File("./src/database/temp.csv"), file);
            }
            catch (FileNotFoundException e){
                return new Response(500, Consts.errorFile);
            }
        }
        catch(IOException e){
            return new Response(500, Consts.errorIO);
        }
        return new Response(500, "Item ID " + " not in file");
    }
    private static void copyFiles(File source, File dest) throws IOException {
        Files.move(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    Response updateTransactions(ArrayList<Item> cart){
        try{
            FileWriter fw = new FileWriter("./src/database/transactions.csv", true);
            BufferedWriter bw = new BufferedWriter(fw);
            DateTimeFormatter dtf;
            ZonedDateTime now = ZonedDateTime.now();
            for (int i = 0; i < cart.size(); i++){
                dtf =  DateTimeFormatter.ofPattern("DDMMYYYYHHMMSS");
                bw.write(dtf.format(now)+(      // toString isn't used here because
                                                    // the quotations around the name don't display correctly
                        "" + cart.get(i).getID() + " " +
                                cart.get(i).getName() +
                                " $" + String.format("%.2f", cart.get(i).getPrice()) +
                                " " + cart.get(i).getQuantity() +
                                " " + cart.get(i).getDiscount() +
                                "% $" + String.format("%.2f", cart.get(i).getTotal())+ " "
                ));
                dtf =  DateTimeFormatter.ofPattern("MMMM d, yyyy, hh:mm:ssa z");
                bw.write(dtf.format(now));
                bw.newLine();
            }
            bw.close();
        }
        catch(IOException e){
            return new Response(500, Consts.errorID);
        }
        return new Response(200, "");
    }
}
