package nl.radiantrealm.myshop.record;

import com.google.gson.JsonObject;
import nl.radiantrealm.library.utils.DataObject;
import nl.radiantrealm.myshop.enumerator.OrderStatus;
import nl.radiantrealm.myshop.enumerator.TransactionType;

import java.util.Map;
import java.util.UUID;

public record TransactionLog(int logID, long timestamp, OrderStatus orderStatus, TransactionType transactionType, UUID playerUUID, Map<String, Integer> materials) implements DataObject {

    @Override
    public JsonObject toJson() throws IllegalStateException {
        JsonObject result = new JsonObject();
        result.addProperty("log_id", logID);
        result.addProperty("timestamp", timestamp);
        result.addProperty("order_status", orderStatus.name());
        result.addProperty("transaction_type", transactionType.name());
        result.addProperty("player_uuid", playerUUID.toString());

        JsonObject object = new JsonObject();

        for (Map.Entry<String, Integer> entry : materials.entrySet()) {
            object.addProperty(entry.getKey(), entry.getValue());
        }

        result.add("materials", object);
        return result;
    }
}
