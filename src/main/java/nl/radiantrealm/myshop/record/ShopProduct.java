package nl.radiantrealm.myshop.record;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nl.radiantrealm.library.utils.DataObject;
import nl.radiantrealm.library.utils.FormatUtils;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public record ShopProduct(String productKey, String productName, Material productIcon, String productCategory, Map<Material, Integer> materials) implements DataObject {

    @Override
    public JsonObject toJson() throws IllegalStateException {
        JsonObject object = new JsonObject();
        object.addProperty("product_key", productKey);
        object.addProperty("product_name", productName);
        object.addProperty("product_icon", productIcon.name());
        object.addProperty("product_category", productCategory);
        object.add("materials", parseMaterialJSON(materials));
        return object;
    }

    public static Map<Material, Integer> parseMaterialMap(JsonObject object) throws IllegalArgumentException {
        Map<Material, Integer> map = new HashMap<>(object.size());

        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            map.put(
                    FormatUtils.formatEnum(Material.class, entry.getKey()),
                    FormatUtils.formatInteger(entry.getValue().getAsString())
            );
        }

        return map;
    }

    public static JsonObject parseMaterialJSON(Map<Material, Integer> materials) throws IllegalStateException {
        JsonObject object = new JsonObject();

        for (Map.Entry<Material, Integer> entry : materials.entrySet()) {
            object.addProperty(entry.getKey().name(), entry.getValue());
        }

        return object;
    }
}
