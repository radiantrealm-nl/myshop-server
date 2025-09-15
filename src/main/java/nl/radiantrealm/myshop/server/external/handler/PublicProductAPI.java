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
            request.sendStatusResponse(StatusCode.BAD_REQUEST, "Provide valid argument.");
            return;
        }

        String resource = url[4];

        if (resource == null) {
            request.sendStatusResponse(StatusCode.BAD_REQUEST, "Provide a valid resource. Use 'product' or 'products'.");
            return;
        }

        String identifier = url[5];

        if (identifier == null) {
            request.sendStatusResponse(StatusCode.BAD_REQUEST, String.format("Provide a product identifier after '%s'.", resource));
            return;
        }

        switch (resource) {
            case "product" -> {
                JsonObject object = JsonViewBuilderCache.productOverview.get(identifier);

                if (object == null) {
                    request.sendStatusResponse(StatusCode.NOT_FOUND, "Product not found.");
                    return;
                }

                request.sendResponse(StatusCode.OK, object);
            }

            case "products" -> {
                List<String> list = Main.productCategoryCache.get(url[4]);

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

            default -> request.sendStatusResponse(StatusCode.BAD_REQUEST, "Provide a valid resource. Use 'product' or 'products'.");
        }
    }
}
