package nl.radiantrealm.myshop.record;

import com.google.gson.JsonObject;
import nl.radiantrealm.library.utils.DataObject;
import org.bukkit.Material;

public record ShopCategory(String categoryKey, String categoryName, Material categoryIcon) implements DataObject {

    @Override
    public JsonObject toJson() throws IllegalStateException {
        JsonObject object = new JsonObject();
        object.addProperty("category_key", categoryKey);
        object.addProperty("category_name", categoryName);
        object.addProperty("category_icon", categoryIcon.name());
        return object;
    }
}
