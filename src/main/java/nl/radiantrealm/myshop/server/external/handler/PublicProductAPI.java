package nl.radiantrealm.myshop.server.external.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import nl.radiantrealm.library.http.HttpRequest;
import nl.radiantrealm.library.http.RequestHandler;
import nl.radiantrealm.library.http.StatusCode;
import nl.radiantrealm.myshop.Main;
import nl.radiantrealm.myshop.cache.JsonViewBuilderCache;

import java.util.List;
import java.util.Map;

public class PublicProductAPI implements RequestHandler {

    @Override
    public void handle(HttpRequest request) throws Exception {
        String[] url = request.getRequestURI().split("/");

        if (url.length < 5) {
            request.sendStatusResponse(StatusCode.BAD_REQUEST, "Provide a valid argument.");
            return;
        }

        switch (url[3]) {
            case "product" -> getProduct(request, url[4]);
            case "products" -> getProducts(request, url[5]);
            default -> request.sendStatusResponse(StatusCode.BAD_REQUEST);
        }
    }

    private void getProduct(HttpRequest request, String identifier) throws Exception {
        JsonObject object = JsonViewBuilderCache.productOverview.get(identifier);

        if (object == null) {
            request.sendStatusResponse(StatusCode.BAD_REQUEST, "Product not found.");
            return;
        }

        request.sendResponse(StatusCode.OK, object);
    }

    private void getProducts(HttpRequest request, String identifier) throws Exception {
        List<String> list = Main.productCategoryCache.get(identifier);

        if (list == null) {
            request.sendStatusResponse(StatusCode.NOT_FOUND, "Category not found.");
            return;
        }

        Map<String, JsonObject> map = JsonViewBuilderCache.productOverview.get(list);

        JsonArray array = new JsonArray();
        map.forEach((key, value) -> array.add(value));
        JsonObject object = new JsonObject();
        object.add("products", array);
        request.sendResponse(StatusCode.OK, object);
    }
}
