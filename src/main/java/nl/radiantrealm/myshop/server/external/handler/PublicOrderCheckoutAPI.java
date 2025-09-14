package nl.radiantrealm.myshop.server.external.handler;

import com.google.gson.JsonObject;
import nl.radiantrealm.library.http.HttpRequest;
import nl.radiantrealm.library.http.StatusCode;
import nl.radiantrealm.library.utils.JsonUtils;
import nl.radiantrealm.myshop.Main;
import nl.radiantrealm.myshop.cache.*;
import nl.radiantrealm.myshop.record.ShopProduct;
import nl.radiantrealm.myshop.server.external.PublicRequestHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class PublicOrderCheckoutAPI implements PublicRequestHandler {
    private final ProductPriceCache productPriceCache = Main.productPriceCache;
    private final ProductStockCache productStockCache = Main.productStockCache;
    private final ShopProductCache shopProductCache = Main.shopProductCache;

    @Override
    public void handle(HttpRequest request, UUID playerUUID, JsonObject object) throws Exception {
        String action = JsonUtils.getJsonString(object, "action");

        switch (action) {
            case "create_checkout" -> createCheckout(request, playerUUID, object);
            case "accept_checkout" -> acceptCheckout(request, playerUUID);
            case "decline_checkout" -> declineCheckout(request, playerUUID);

            default -> request.sendStatusResponse(StatusCode.BAD_REQUEST, "Unknown action.");
        }
    }

    private void createCheckout(HttpRequest request, UUID playerUUID, JsonObject object) throws Exception {
        if (CheckoutSessionCache.hasCheckoutSession(playerUUID)) {
            request.sendStatusResponse(StatusCode.BAD_REQUEST, "Already checked in.");
            return;
        }

        Map<String, Integer> requestedProducts = JsonUtils.getJsonMap(JsonUtils.getJsonObject(object, "products"), (key, value) -> new AbstractMap.SimpleImmutableEntry<>(
                key,
                value.getAsInt()
        ));

        Map<String, ShopProduct> productMap = shopProductCache.get(requestedProducts.keySet());
        Map<String, Integer> checkoutProducts = new HashMap<>(productMap.size());

        JsonObject availableProducts = new JsonObject();
        BigDecimal checkoutPrice = BigDecimal.ZERO;

        for (ShopProduct product : productMap.values()) {
            String key = product.productKey();
            int requested = requestedProducts.get(key);
            int available = productStockCache.bookProduct(key, requested);

            checkoutProducts.put(key, available);
            availableProducts.addProperty(key, available);
            BigDecimal productPrice = productPriceCache.get(key);
            checkoutPrice = checkoutPrice.add(
                    productPrice.multiply(BigDecimal.valueOf(available)).setScale(2, RoundingMode.HALF_UP)
            );
        }

        UUID sessionUUID = CheckoutSessionCache.createSessionUUID(playerUUID, checkoutProducts);

        JsonObject responseBody = new JsonObject();
        responseBody.addProperty("checkout_price", checkoutPrice);
        responseBody.addProperty("session_uuid", sessionUUID.toString());
        responseBody.add("available_products", availableProducts);
        request.sendResponse(StatusCode.OK, responseBody);
    }

    private void acceptCheckout(HttpRequest request, UUID playerUUID) throws Exception {
        Map<String, Integer> productsMap = CheckoutSessionCache.getProducts(playerUUID);

        if (productsMap == null) {
            request.sendStatusResponse(StatusCode.BAD_REQUEST, "No checkout session.");
            return;
        }

        //Send request to processor and await result.
    }

    private void declineCheckout(HttpRequest request, UUID playerUUID) throws Exception {
        CheckoutSessionCache.destroySessionUUID(playerUUID);
        request.sendStatusResponse(StatusCode.OK);
    }
}
