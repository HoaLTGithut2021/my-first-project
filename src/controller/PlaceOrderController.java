package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import entity.cart.Cart;
import entity.cart.CartMedia;
import common.exception.InvalidDeliveryInfoException;
import entity.invoice.Invoice;
import entity.order.Order;
import entity.order.OrderMedia;
import views.screen.popup.PopupScreen;

public class PlaceOrderController extends BaseController{

    private static Logger LOGGER = utils.Utils.getLogger(PlaceOrderController.class.getName());

    public void placeOrder() throws SQLException{
        Cart.getCart().checkAvailabilityOfProduct();
    }

    public Order createOrder() throws SQLException{
        Order order = new Order();
        for (Object object : Cart.getCart().getListMedia()) {
            CartMedia cartMedia = (CartMedia) object;
            OrderMedia orderMedia = new OrderMedia(cartMedia.getMedia(), 
                                                   cartMedia.getQuantity(), 
                                                   cartMedia.getPrice());    
            order.getlstOrderMedia().add(orderMedia);
        }
        return order;
    }

    public Invoice createInvoice(Order order) {
        return new Invoice(order);
    }

    public void processDeliveryInfo(HashMap info) throws InterruptedException, IOException{
        LOGGER.info("Process Delivery Info");
        LOGGER.info(info.toString());
        validateDeliveryInfo(info);
    }

    public void validateDeliveryInfo(HashMap<String, String> info) throws InterruptedException, IOException{

        LOGGER.info("Validate Delivery Info");
        String message = "";
        if (info.get("name") == "") message = "The name must not be empty and must be characters";
        else if (info.get("province") == "") message = "The province must not be empty";
        else if (!(info.get("phone").length() == 10)) message = "The phone must be number and 10 digits";
        else if (info.get("address") == "") message = "The address must not be empty";
        else{
            try {
                Thread.sleep(500); // simulate validate delivery info
                LOGGER.info("Validate Done");
                Integer.parseInt(info.get("phone"));
            } catch (NumberFormatException e) {
                message = "The phone must be number and 10 digits";
            }
        }
        if (message != ""){
            PopupScreen.error(message);
            throw new InvalidDeliveryInfoException(message);
        }
        
    } 

    public int calculateShippingFee(Order order){
        Random rand = new Random();
        int fees = (int)( ( (rand.nextFloat()*10)/100 ) * order.getAmount() );
        LOGGER.info("Order Amount: " + order.getAmount() + " -- Shipping Fees: " + fees);
        return fees;
    }

    public List getListCartMedia(){
        return Cart.getCart().getListMedia();
    }
}
