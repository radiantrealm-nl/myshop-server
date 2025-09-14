package nl.radiantrealm.myshop.record;

import com.google.gson.JsonObject;
import nl.radiantrealm.library.utils.DataObject;
import nl.radiantrealm.myshop.Main;
import nl.radiantrealm.myshop.cache.ShopMaterialCache;
import org.bukkit.Material;

import java.math.BigDecimal;
import java.util.Map;

public record ShopProduct(String productKey, String productName, Material productIcon, String productCategory, Map<Material, Integer> materials) implements DataObject {
    private static final ShopMaterialCache shopMaterialCache = Main.shopMaterialCache;

    @Override
    public JsonObject toJson() throws IllegalStateException {
        JsonObject result = new JsonObject();
        result.addProperty("product_key", productKey);
        result.addProperty("product_name", productName);
        result.addProperty("product_icon", productIcon.name());
        result.addProperty("product_category", productCategory);

        JsonObject object = new JsonObject();

        for (Map.Entry<Material, Integer> entry : materials.entrySet()) {
            object.addProperty(entry.getKey().name(), entry.getValue());
        }

        result.add("materials", object);

        return result;
    }

    public int availability() throws Exception {
        Map<Material, ShopMaterial> materialMap = shopMaterialCache.get(materials.keySet());
        int minAvailability = Integer.MAX_VALUE;

        for (ShopMaterial material : materialMap.values()) {
            int requiredAmount = materials.get(material.material());
            int available = material.stock() / requiredAmount;
            if (available < minAvailability) {
                minAvailability = available;
            }
        }

        return minAvailability;
    }

    public BigDecimal price() throws Exception {
        Map<Material, ShopMaterial> materialMap = shopMaterialCache.get(materials.keySet());
        BigDecimal price = BigDecimal.ZERO;

        for (ShopMaterial material : materialMap.values()) {
            int amount = materials.get(material.material());
            price = price.add(material.price().multiply(BigDecimal.valueOf(amount)));
        }

        return price;
    }
}
