package nl.radiantrealm.myshop.record;

import com.google.gson.JsonObject;
import nl.radiantrealm.library.utils.DataObject;
import nl.radiantrealm.myshop.enumerator.TransactionType;
import org.bukkit.Material;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record TransactionLog(int logID, long timestamp, TransactionType transactionType, BigDecimal transactionAmount, UUID playerUUID, Map<Material, Integer> materials) implements DataObject {

    @Override
    public JsonObject toJson() throws IllegalStateException {
        JsonObject result = new JsonObject();
        result.addProperty("log_id", logID);
        result.addProperty("timestamp", timestamp);
        result.addProperty("transaction_type", transactionType.name());
        result.addProperty("transaction_amount", transactionAmount);
        result.addProperty("player_uuid", playerUUID.toString());

        JsonObject materials = new JsonObject();

        for (Map.Entry<Material, Integer> entry : this.materials.entrySet()) {
            materials.addProperty(entry.getKey().name(), entry.getValue());
        }

        result.add("materials", materials);
        return result;
    }
}
