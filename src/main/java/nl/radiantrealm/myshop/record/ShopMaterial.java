package nl.radiantrealm.myshop.record;

import com.google.gson.JsonObject;
import nl.radiantrealm.library.utils.DataObject;
import org.bukkit.Material;

import java.math.BigDecimal;

public record ShopMaterial(Material material, int stock, BigDecimal price) implements DataObject {

    @Override
    public JsonObject toJson() throws IllegalStateException {
        JsonObject object = new JsonObject();
        object.addProperty("material", material.name());
        object.addProperty("stock", stock);
        object.addProperty("price", price);
        return object;
    }
}
