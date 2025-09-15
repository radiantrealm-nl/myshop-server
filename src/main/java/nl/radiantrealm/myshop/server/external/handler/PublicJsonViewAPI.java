package nl.radiantrealm.myshop.server.external.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import nl.radiantrealm.library.http.HttpRequest;
import nl.radiantrealm.library.http.StatusCode;
import nl.radiantrealm.library.utils.JsonUtils;
import nl.radiantrealm.library.utils.Result;
import nl.radiantrealm.myshop.Main;
import nl.radiantrealm.myshop.cache.JsonViewBuilderCache;
import nl.radiantrealm.myshop.server.external.PublicRequestHandler;

import java.util.UUID;

public class PublicJsonViewAPI implements PublicRequestHandler {

    @Override
    public void handle(HttpRequest request, UUID playerUUID, JsonObject object) throws Exception {
        String requestedView = Result.nullFunction(() -> JsonUtils.getJsonString(object, "view"));

        if (requestedView == null) {
            request.sendStatusResponse(StatusCode.BAD_REQUEST, "Missing views body.");
            return;
        }

        JsonObject responseBody = new JsonObject();

        switch (requestedView) {
            case "get-category-products" -> {
                String category = Result.nullFunction(() -> JsonUtils.getJsonString(object, "category"));

                if (category == null) {
                    request.sendStatusResponse(StatusCode.BAD_REQUEST, "Missing category.");
                    return;
                }

                JsonArray array = new JsonArray();

                for (String string : Main.productCategoryCache.get(category)) {
                    array.add(JsonViewBuilderCache.productOverview.get(string));
                }

                responseBody.add("products", array);
                request.sendResponse(StatusCode.OK, responseBody);
            }

            default -> request.sendStatusResponse(StatusCode.BAD_REQUEST, "Unknown view type.");
        }
    }

    private JsonObject addJsonError(String error) {
        JsonObject object = new JsonObject();
        object.addProperty("error", error);
        return object;
    }
}
