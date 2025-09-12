package nl.radiantrealm.myshop.record;

import com.google.gson.JsonObject;
import nl.radiantrealm.library.utils.DataObject;
import nl.radiantrealm.myshop.enumerator.TransactionType;

import java.util.Map;
import java.util.UUID;

public record Transaction(TransactionType transactionType, UUID playerUUID, Map<String, Integer> products) implements DataObject {

    @Override
    public JsonObject toJson() throws IllegalStateException {
        JsonObject result = new JsonObject();
        result.addProperty("transaction_type", transactionType.name());
        result.addProperty("player_uuid", playerUUID.toString());

        JsonObject object = new JsonObject();

        for (Map.Entry<String, Integer> entry : products.entrySet()) {
            object.addProperty(entry.getKey(), entry.getValue());
        }

        result.add("products", object);
        return result;
    }
}
