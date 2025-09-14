package nl.radiantrealm.myshop.cache;

import com.google.gson.JsonObject;
import nl.radiantrealm.library.cache.CacheRegistry;
import nl.radiantrealm.library.utils.FormatUtils;
import nl.radiantrealm.library.utils.JsonUtils;
import nl.radiantrealm.myshop.Database;
import nl.radiantrealm.myshop.record.ShopProduct;
import org.bukkit.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.*;

public class ShopProductCache extends CacheRegistry<String, ShopProduct> {

    public ShopProductCache() {
        super(Duration.ofMinutes(15));
    }

    private Map<String, Integer> parseMaterialMap(ResultSet rs) throws Exception {
        JsonObject object = JsonUtils.getJsonObject(rs.getString("materials"));
        return JsonUtils.getJsonMap(object, (key, value) -> new AbstractMap.SimpleEntry<>(
                key,
                value.getAsInt()
        ));
    }

    @Override
    protected ShopProduct load(String key) throws Exception {
        try (Connection connection = Database.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM myshop_products WHERE product_key = ?"
            );

            statement.setString(1, key);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return new ShopProduct(
                        key,
                        rs.getString("product_name"),
                        FormatUtils.formatEnum(Material.class, rs, "product_icon"),
                        rs.getString("product_category"),
                        parseMaterialMap(rs)
                );
            }

            return null;
        }
    }

    @Override
    protected Map<String, ShopProduct> load(List<String> keys) throws Exception {
        String params = String.join(", ", Collections.nCopies(keys.size(), "?"));

        try (Connection connection = Database.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM myshop_products WHERE product_key IN (" + params + ")"
            );

            for (int i = 0; i < keys.size(); i++) {
                statement.setString(i + 1, keys.get(i));
            }

            ResultSet rs = statement.executeQuery();
            Map<String, ShopProduct> map = new HashMap<>(keys.size());

            while (rs.next()) {
                String productKey = rs.getString("product_key");

                map.put(productKey, new ShopProduct(
                        productKey,
                        rs.getString("product_name"),
                        FormatUtils.formatEnum(Material.class, rs, "product_icon"),
                        rs.getString("product_category"),
                        parseMaterialMap(rs)
                ));
            }

            return map;
        }
    }
}
