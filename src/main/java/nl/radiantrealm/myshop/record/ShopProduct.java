package nl.radiantrealm.myshop.record;

import com.google.gson.JsonObject;
import nl.radiantrealm.library.utils.DataObject;
import org.bukkit.Material;

import java.util.Map;

public record ShopProduct(String productKey, String productName, String category, Map<Material, Integer> materials) implements DataObject {

    @Override
    public JsonObject toJson() throws IllegalStateException {
        JsonObject result = new JsonObject();
        result.addProperty("product_key", productKey);
        result.addProperty("product_name", productName);
        result.addProperty("category", category);

        JsonObject materials = new JsonObject();

        for (Map.Entry<Material, Integer> entry : this.materials.entrySet()) {
            materials.addProperty(entry.getKey().name(), entry.getValue());
        }

        result.add("materials", materials);
        return result;
    }
}
