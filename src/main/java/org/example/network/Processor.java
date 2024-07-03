package org.example.network;

import com.google.gson.Gson;
import org.example.models.Message;
import org.example.models.Packet;
import org.example.models.Product;
import org.example.models.ProductGroup;
import org.example.services.ProductService;
import org.example.utils.EncryptUtil;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Processor {
    private final EncryptUtil encryptor;
    private final Sender sender;
    private final ProductService productService;

    public Processor(EncryptUtil encryptor, Sender sender) {
        this.encryptor = encryptor;
        this.sender = sender;
        this.productService = new ProductService();
    }

    public void process(Message message, ObjectOutputStream out, InetAddress address, int port) {
        // process message
        String command = new String(message.getMessage());
        System.out.println("Processing command: " + command);

        // Formulated answer
        String response;
        try {
            response = handleCommand(command);
        } catch (Exception e) {
            response = "Error " + e.getMessage();
        }

        handleResponse(response, message, out, address, port);
    }

    private String handleCommand(String command) throws Exception {
        String[] parts = command.split(":");
        String operation = parts[0].toUpperCase();

        return switch (operation) {
            case "GET_AMOUNT" -> getAmount(parts);
            case "ADD_AMOUNT" -> procureProduct(parts);
            case "DEDUCT_AMOUNT" -> distributeProduct(parts);
            case "CREATE_GROUP" -> createGroup(parts);
            case "EDIT_GROUP" -> editGroup(parts);
            case "DELETE_GROUP" -> deleteGroup(Integer.parseInt(parts[1]));
            case "CREATE_PRODUCT" -> createProduct(parts);
            case "EDIT_PRODUCT" -> editProduct(parts);
            case "DELETE_PRODUCT" -> deleteProduct(Integer.parseInt(parts[1]));
            case "CALCULATE_TOTAL_COST" -> calculateTotalCost();
            case "CALCULATE_TOTAL_COST_PER_GROUP" -> calculateTotalCostPerGroup();
            case "LIST_PRODUCTS_BY_CRITERIA" -> listAllProductsByCriteria(parts);
            default -> "Unknown operation: " + command;
        };
    }

    private String getAmount(String[] parts) throws SQLException {
        int quantity = productService.getProduct(Integer.parseInt(parts[1])).getAmount();
        return "Quantity of product " + parts[1] + ": " + quantity;
    }

    private String procureProduct(String[] parts) throws SQLException {
        Product addProduct = productService.getProduct(Integer.parseInt(parts[1]));
        int updAmount = addProduct.getAmount() + Integer.parseInt(parts[2]);
        addProduct.setAmount(updAmount);
        productService.updateProduct(addProduct);
        return "Product procured successfully. New amount of product " + parts[1] + ": " + updAmount;
    }

    private String distributeProduct(String[] parts) throws SQLException {
        Product deductProduct = productService.getProduct(Integer.parseInt(parts[1]));
        int newAmount = deductProduct.getAmount() - Integer.parseInt(parts[2]);
        deductProduct.setAmount(newAmount);
        productService.updateProduct(deductProduct);
        return "Product distributed. New amount of product " + parts[1] + ": " + newAmount;
    }

    private String createProduct(String[] parts) throws SQLException {
        String name = parts[1];
        String description = parts[2];
        String producer = parts[3];
        int amount = Integer.parseInt(parts[4]);
        double price = Double.parseDouble(parts[5]);
        int groupId = Integer.parseInt(parts[6]);

        Product product = new Product(0, name, description, producer, amount, price, groupId);
        productService.createProduct(product);

        return "Product created successfully.";
    }

    private String editProduct(String[] parts) {
        try {
            int productId = Integer.parseInt(parts[1]);
            String name = parts[2];
            String description = parts[3];
            String producer = parts[4];
            int amount = Integer.parseInt(parts[5]);
            double price = Double.parseDouble(parts[6]);
            int groupId = Integer.parseInt(parts[7]);

            Product product = new Product(productId, name, description, producer, amount, price, groupId);
            productService.updateProduct(product);

            return "Product edited successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error editing product: " + e.getMessage();
        }
    }

    private String deleteProduct(int productId) throws SQLException {
        productService.deleteProduct(productId);
        return "Product deleted successfully.";
    }

    private String createGroup(String[] parts) throws SQLException {
        String groupName = parts[1];
        String groupDesc = parts[2];
        productService.createGroup(groupName, groupDesc);
        return "Group added: " + groupName;
    }

    private String editGroup(String[] parts) {
        try {
            int groupID = Integer.parseInt(parts[1]);
            String name = parts[2];
            String description = parts[3];

            ProductGroup group = new ProductGroup(groupID, name, description);
            productService.updateGroup(group);

            return "Group edited successfully.";
        } catch (Exception e) {
            return "Error editing group: " + e.getMessage();
        }
    }

    private String deleteGroup(int groupId) throws SQLException {
        productService.deleteGroup(groupId);
        return "Group deleted successfully.";
    }

    private String calculateTotalCost() throws SQLException {
        double totalCost = productService.calculateTotalCost();
        return "Total cost calculated: " + totalCost;
    }

    private String calculateTotalCostPerGroup() throws SQLException {
        Map<String, Double> totalCostPerGroup = productService.calculateTotalCostPerGroup();
        Gson gson = new Gson();
        return gson.toJson(totalCostPerGroup);
    }

    private String listAllProductsByCriteria(String[] parts) throws SQLException {
        String criteria = parts[1];
        String searchQuery = parts.length > 2 ? parts[2].trim() : "";
        List<Product> products = productService.listProducts(criteria, searchQuery);
        Gson gson = new Gson();
        return gson.toJson(products);
    }

    private void handleResponse(String res, Message message, ObjectOutputStream out, InetAddress address, int port) {
        new Thread(() -> {
            try {
                if(message.isUDP()) {
                    String udpRes = "acknowledged;" + res;
                    Message resMsgUDP = new Message(1, message.getbUserId(), udpRes.getBytes(), message.isUDP());
                    Packet resPacketUDP = new Packet((byte) 0x13, (byte) message.getbUserId(), System.currentTimeMillis(), resMsgUDP.getMessage().length, resMsgUDP.getMessage());
                    byte[] packetBytesUDP = sender.getPacketHandler().constructPacketBytes(resPacketUDP);
                    sender.sendMessageUDP(packetBytesUDP, address, port);
                } else {
                    Message resMsg = new Message(1, message.getbUserId(), res.getBytes(), message.isUDP());
                    Packet resPacket = new Packet((byte) 0x13, (byte) message.getbUserId(), System.currentTimeMillis(), resMsg.getMessage().length, resMsg.getMessage());
                    byte[] packetBytes = sender.getPacketHandler().constructPacketBytes(resPacket);
                    sender.sendMessageTCP(packetBytes, out);
                }
            } catch (Exception e) {
                System.err.println("Error in Processor's handleResponse!");
            }
        }).start();
    }
}
