package org.example.network;

import com.google.gson.Gson;
import org.example.models.Message;
import org.example.models.Packet;
import org.example.models.Product;
import org.example.services.ProductService;
import org.example.utils.EncryptUtil;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.List;

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

        switch(operation) {
            case "GET_AMOUNT":
                int quantity = productService.getProduct(Integer.parseInt(parts[1])).getAmount();
                return "Quantity of product " + parts[1] + ": " + quantity;
            case "DEDUCT_AMOUNT":
                Product deductProduct = productService.getProduct(Integer.parseInt(parts[1]));
                int newAmount = deductProduct.getAmount() - Integer.parseInt(parts[2]);
                deductProduct.setAmount(newAmount);
                productService.updateProduct(deductProduct);
                return "Amount deducted. New amount of product " + parts[1] + ": " + newAmount;
            case "ADD_AMOUNT":
                Product addProduct = productService.getProduct(Integer.parseInt(parts[1]));
                int updAmount = addProduct.getAmount() + Integer.parseInt(parts[2]);
                addProduct.setAmount(updAmount);
                productService.updateProduct(addProduct);
                return "Amount added. New amount of product " + parts[1] + ": " + updAmount;
            case "ADD_GROUP":
                String groupName = parts[1];
                String groupDesc = parts[2];
                productService.createGroup(groupName, groupDesc);
                return "Group added: " + groupName;
            case "ADD_PRODUCT_TO_GROUP":
                int productID = Integer.parseInt(parts[1]);
                int groupID = Integer.parseInt(parts[2]);
                productService.assignProductToGroup(productID, groupID);
                return "Product " + productID + " added to group " + groupID;
            case "SET_PRICE":
                Product priceProduct = productService.getProduct(Integer.parseInt(parts[1]));
                priceProduct.setPrice(Double.parseDouble(parts[2]));
                productService.updateProduct(priceProduct);
                return "Price set for product " + parts[1] + ": " + parts[2];
            case "LIST_PRODUCTS_BY_CRITERIA":
                String criteria = parts[1];
                String searchQuery = parts.length > 2 ? parts[2].trim() : "";
                List<Product> products = productService.listProducts(criteria, searchQuery);
                Gson gson = new Gson();
                return gson.toJson(products);
            default:
                return "Unknown operation";
        }
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
