package nl.radiantrealm.myshop.record;

import com.google.gson.JsonObject;
import nl.radiantrealm.library.utils.DataObject;
import nl.radiantrealm.myshop.enumerator.TransactionType;
import org.bukkit.Material;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record Transaction(TransactionType transactionType, BigDecimal transactionAmount, UUID playerUUID, Map<Material, Integer> materials) implements DataObject {

    @Override
    public JsonObject toJson() throws IllegalStateException {
        JsonObject object = new JsonObject();
        object.addProperty("transaction_type", transactionType.name());
        object.addProperty("transaction_amount", transactionAmount);
        object.addProperty("player_uuid", playerUUID.toString());
        object.add("materials", ShopProduct.parseMaterialJSON(materials));
        return object;
    }

    public TransactionLog createTransactionLog() {
        return new TransactionLog(
                0,
                System.currentTimeMillis(),
                transactionType,
                transactionAmount,
                playerUUID,
                materials
        );
    }
}
