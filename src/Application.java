/* Name: Samuel Tofexis
Course: CNT 4714 – Fall 2023
Assignment title: Project 1 – Event-driven Enterprise Simulation
Date: Sunday September 11, 2023
*/
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Application extends JFrame implements ActionListener {

    Item currentItem;
    int cartQuantity = 0;
    float total = 0;
    Boolean checkedOut = false; // Variable only used so onFieldUpdate doesn't let the
                        // user add another item after they checked out
    ArrayList<Item> cart = new ArrayList<>();
    HashMap<String, Item> cartHash = new HashMap<>();       // Used so we can update the database
                                                            // linearly without sorting the cart
    int currentDetails = 1; // The counter to update what item number is displayed for item details

    Controller controller = new Controller();

    JTextField fieldID;
    JTextField fieldQuantity;
    JTextField fieldDetails;
    JTextField fieldSubtotal;
    JLabel labelSubtotal;
    JLabel labelID;
    JLabel labelQuantity;
    JLabel labelDetails;

    JButton buttonFind;
    JButton buttonView;
    JButton buttonAdd;
    JButton buttonCheckout;
    JButton buttonEmpty;
    JButton buttonExit;


    Application() {
        initGUI();
    }

    // ActionListener requires this method to be in this class
    public void actionPerformed(ActionEvent e){
    }

    void initGUI(){
        // Using two separate panels for top and bottom, makes it easier to layout
        JPanel mainPanel = new JPanel();
        JPanel topPanel = new JPanel();
        JPanel bottomPanel = new JPanel();

        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onFieldUpdate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onFieldUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onFieldUpdate();
            }
        };

        buttonFind = new JButton("Find Item #" + (cart.size()+1));
        buttonView = new JButton("View Cart");
        buttonAdd = new JButton("Add Item #"+(cart.size()+1)+" To Cart");
        buttonCheckout = new JButton("Check Out");
        buttonEmpty = new JButton("Empty Cart - Start A New Order");
        buttonExit = new JButton("Exit (Close App)");

        // Using lambda functions to allow us to create a separate method for each button
        buttonFind.addActionListener(e -> buttonFindPressed());
        buttonView.addActionListener(e -> buttonViewPressed());
        buttonAdd.addActionListener(e -> buttonAddPressed());
        buttonCheckout.addActionListener(e -> buttonCheckoutPressed());
        buttonEmpty.addActionListener(e -> buttonEmptyPressed());
        buttonExit.addActionListener(e -> buttonExitPressed());

        buttonAdd.setEnabled(false);
        buttonView.setEnabled(false);
        buttonCheckout.setEnabled(false);

        labelID = new JLabel("Enter ID for Item #"+(cart.size()+1)+": ");
        labelQuantity = new JLabel("Enter quantity for Item #"+(cart.size()+1)+": ");
        labelDetails = new JLabel("Details for Item #"+currentDetails+": ");


        labelSubtotal = new JLabel("Order subtotal for "+0+" item(s): ");

        // Document listener is used here to make find button reappear
        fieldID = new JTextField();
        fieldID.getDocument().addDocumentListener(listener);
        fieldQuantity = new JTextField();
        fieldQuantity.getDocument().addDocumentListener(listener);

        fieldDetails = new JTextField();
        fieldDetails.setEditable(false);
        fieldSubtotal = new JTextField();
        fieldSubtotal.setEditable(false);

        EmptyBorder border = new EmptyBorder(10, 10, 10, 10);
        mainPanel.setBorder(border);
        topPanel.setBorder(border);
        bottomPanel.setBorder(border);

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        topPanel.setLayout(new GridLayout(4,1));
        bottomPanel.setLayout(new GridLayout(3,2));
        mainPanel.add(topPanel);
        mainPanel.add(bottomPanel);

        bottomPanel.add(buttonFind);
        bottomPanel.add(buttonAdd);
        bottomPanel.add(buttonView);
        bottomPanel.add(buttonCheckout);
        bottomPanel.add(buttonEmpty);
        bottomPanel.add(buttonExit);

        topPanel.add(labelID);
        topPanel.add(fieldID);
        topPanel.add(labelQuantity);
        topPanel.add(fieldQuantity);
        topPanel.add(labelDetails);
        topPanel.add(fieldDetails);
        topPanel.add(labelSubtotal);
        topPanel.add(fieldSubtotal);

        setContentPane(mainPanel);
        setTitle("NILE DOT COM - FALL 2023");
        setSize(900, 300);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void onFieldUpdate() {
        if (!checkedOut){
            buttonFind.setEnabled(true);
        }
    }

    private void buttonEmptyPressed() {
        checkedOut = false;
        cart = new ArrayList<>();
        cartHash = new HashMap<>();
        currentItem = null;
        currentDetails = 1;
        updateElements();
        fieldDetails.setText("");
        fieldID.setText("");
        fieldSubtotal.setText("");
        buttonFind.setEnabled(true);
        buttonCheckout.setEnabled(true);
        buttonView.setEnabled(true);
        buttonAdd.setEnabled(true);
    }

    private void buttonCheckoutPressed() {
        controller.updateQuantities(cartHash, cart);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM d, yyyy, hh:mm:ssa z");
        ZonedDateTime now = ZonedDateTime.now();
        String message = "";
        float subtotal = 0;
        message += "Date: " + dtf.format(now)+"\n\n";
        message += "Number of line items: "+ cart.size()+"\n\n";
        message += "Item# / ID / Title / Price / Qty / Disc % / Subtotal:"+"\n\n";
        for (int i = 0; i < cart.size(); i++){
            message += (""+(i+1)+". "+cart.get(i).toString()+"\n");
        }
        message += "Order subtotal:\t$"+
                String.format("%.2f", subtotal)+"\n\n";
        message += "Tax rate:\t6%"+"\n\n";
        message += "Tax amount:\t"+String.format("%.2f",(total*0.06f))+"\n\n";
        message += "ORDER TOTAL:\t$"+String.format("%.2f", total+(total*0.06f))+"\n\n";
        message += "Thanks for shopping at Nile Dot Com!";
        JOptionPane.showMessageDialog(null, message, "FINAL INVOICE", JOptionPane.INFORMATION_MESSAGE);

        // Grey out options until user makes new order
        checkedOut = true;
        buttonFind.setEnabled(false);
        buttonCheckout.setEnabled(false);
        buttonView.setEnabled(false);
        buttonAdd.setEnabled(false);
    }

    private void buttonFindPressed() {
        Response response = controller.find(fieldID.getText(), fieldQuantity.getText());
        System.out.println(response.toString());
        if (response.getStatus() == 500){
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            if (response.getMessage() == Consts.errorStock){
                fieldQuantity.setText("");
                fieldID.setText("");
            }
            return;
        }
        currentItem = response.getItem();
        // If item is already in cart
        if (cartHash.containsKey(currentItem.getID())){
            System.out.println("Item in hash");
            System.out.println("Item in hash");
            System.out.println("Current: " + cartHash.get(currentItem.getID()).getQuantity());
            System.out.println("Addto: " + currentItem.getQuantity());
            if ((cartHash.get(currentItem.getID()).getQuantity() + currentItem.getQuantity()) > currentItem.getStock()){
                JOptionPane.showMessageDialog(null, Consts.errorStock, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        // UPDATE HASH VALUE

        System.out.println("Stock: "+currentItem.getStock());
        // Update the text entry fields
        fieldDetails.setText(response.getItem().toString());
        int currentQuantity = 0;
        if (currentItem != null){
            currentQuantity = currentItem.getQuantity();
        }
        currentDetails = (cart.size()+1);
        labelDetails.setText("Details for Item #"+currentDetails+": ");

        buttonAdd.setEnabled(true);
        buttonFind.setEnabled(false);
    }

    private void buttonViewPressed() {
        String message = "";
        for (int i = 0; i < cart.size(); i++){
            message += (""+(i+1)+". "+cart.get(i).toString()+"\n");
        }
        JOptionPane.showMessageDialog(null, message, "Current Shopping Cart Status", JOptionPane.INFORMATION_MESSAGE);
    }

    private void buttonAddPressed() {
        if (currentItem != null){
            currentItem.setLocation(cart.size());
            if (cartHash.containsKey(currentItem.getID())){  // If item is already in cart
                currentItem.setQuantity((cartHash.get(currentItem.getID()).getQuantity() + currentItem.getQuantity()));
                System.out.println("Location: "+cartHash.get(currentItem.getID()).getLocation());
                cart.set(cartHash.get(currentItem.getID()).getLocation(),currentItem);
                JOptionPane.showMessageDialog(null, " Updated quantity of Item #"+currentDetails+" in cart", "Item Confirmed", JOptionPane.INFORMATION_MESSAGE);
            }
            else{ // Add item regularly
                cart.add(currentItem);
                JOptionPane.showMessageDialog(null, "Item #"+currentDetails+" accepted", "Item Confirmed", JOptionPane.INFORMATION_MESSAGE);
            }
            cartHash.put(currentItem.getID(),currentItem);
            updateElements();
            cartQuantity += currentItem.getQuantity();
            total += currentItem.getTotal();
            labelSubtotal.setText("Order subtotal for "+cartQuantity+" item(s): ");
            fieldSubtotal.setText("$"+String.format("%.2f", total));
            fieldID.setText("");

            // Do not update database when added, only when checkout occurs
            // Double check quantity database at moment of transaction
            // Since we are updating the quantity of the entry in the database anyway, this is not hard

            // For duplicate items, check to see if item exists in cart
            // Add duplicate quantity to current (HASHMAP ITEM ID)
            // replace entry
            currentItem = null;
            buttonAdd.setEnabled(false);
            buttonFind.setEnabled(true);
            buttonView.setEnabled(true);
            buttonCheckout.setEnabled(true);
        }
    }

    private void buttonExitPressed() {
        this.dispose();
    }

    private void updateElements(){
        fieldSubtotal.setText("");
        fieldQuantity.setText("");

        buttonFind.setText("Find Item #" + (cart.size()+1));
        buttonAdd.setText("Add Item #"+(cart.size()+1)+" To Cart");

        labelID.setText("Enter ID for Item #"+(cart.size()+1)+": ");
        labelQuantity.setText("Enter quantity for Item #"+(cart.size()+1)+": ");
        labelSubtotal.setText("Order subtotal for "+0+" item(s): ");
    }

    public static void main(String[] args) {
        new Application();
    }

}
