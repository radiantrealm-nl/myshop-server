package nl.radiantrealm.myshop.record;

import com.google.gson.JsonObject;
import nl.radiantrealm.library.utils.DataObject;

public record ShopCategory(String categoryKey, String categoryName) implements DataObject {

    @Override
    public JsonObject toJson() throws IllegalStateException {
        JsonObject object = new JsonObject();
        object.addProperty("category_key", categoryKey);
        object.addProperty("category_name", categoryName);
        return object;
    }
}
