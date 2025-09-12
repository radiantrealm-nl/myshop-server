package nl.radiantrealm.myshop.cache;

import nl.radiantrealm.library.cache.CacheRegistry;
import nl.radiantrealm.library.utils.FormatUtils;
import nl.radiantrealm.myshop.Database;
import nl.radiantrealm.myshop.record.ShopCategory;
import org.bukkit.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopCategoryCache extends CacheRegistry<String, ShopCategory> {

    public ShopCategoryCache() {
        super(Duration.ofMinutes(60));
    }

    @Override
    protected ShopCategory load(String key) throws Exception {
        try (Connection connection = Database.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM myshop_categories WHERE category_key = ?"
            );

            statement.setString(1, key);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return new ShopCategory(
                        key,
                        rs.getString("category_name"),
                        FormatUtils.formatEnum(Material.class, rs, "category_icon")
                );
            }

            return null;
        }
    }

    @Override
    protected Map<String, ShopCategory> load(List<String> keys) throws Exception {
        String params = String.join(", ", Collections.nCopies(keys.size(), "?"));

        try (Connection connection = Database.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM myshop_categories WHERE category_key IN (" + params + ")"
            );

            for (int i = 0; i < keys.size(); i++) {
                statement.setString(i + 1, keys.get(i));
            }

            ResultSet rs = statement.executeQuery();
            Map<String, ShopCategory> map = new HashMap<>(keys.size());

            while (rs.next()) {
                String categoryKey = rs.getString("category_key");

                map.put(categoryKey, new ShopCategory(
                        categoryKey,
                        rs.getString("category_name"),
                        FormatUtils.formatEnum(Material.class, rs, "category_icon")
                ));
            }

            return map;
        }
    }
}
