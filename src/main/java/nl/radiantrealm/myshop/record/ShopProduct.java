package nl.radiantrealm.myshop.record;

import com.google.gson.JsonObject;
import nl.radiantrealm.library.utils.DataObject;
import org.bukkit.Material;

import java.util.Map;

public record ShopProduct(String productKey, String productName, Material productIcon, String productCategory, Map<String, Integer> materials) implements DataObject {

    @Override
    public JsonObject toJson() throws IllegalStateException {
        JsonObject result = new JsonObject();
        result.addProperty("product_key", productKey);
        result.addProperty("product_name", productName);
        result.addProperty("product_icon", productIcon.name());
        result.addProperty("product_category", productCategory);

        JsonObject object = new JsonObject();

        for (Map.Entry<String, Integer> entry : materials.entrySet()) {
            object.addProperty(entry.getKey(), entry.getValue());
        }

        result.add("materials", object);

        return result;
    }
}
