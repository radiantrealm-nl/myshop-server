package nl.radiantrealm.myshop.cache;

import nl.radiantrealm.library.cache.CacheRegistry;
import nl.radiantrealm.library.utils.FormatUtils;
import nl.radiantrealm.myshop.Database;
import nl.radiantrealm.myshop.record.ShopMaterial;
import org.bukkit.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopMaterialCache extends CacheRegistry<Material, ShopMaterial> {

    public ShopMaterialCache() {
        super(Duration.ofMinutes(15));
    }

    @Override
    protected ShopMaterial load(Material material) throws Exception {
        try (Connection connection = Database.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM myshop_materials WHERE material = ?"
            );

            statement.setString(1, material.name());
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return new ShopMaterial(
                        material,
                        rs.getInt("stock"),
                        rs.getBigDecimal("price")
                );
            }

            return null;
        }
    }

    @Override
    protected Map<Material, ShopMaterial> load(List<Material> keys) throws Exception {
        String params = String.join(", ", Collections.nCopies(keys.size(), "?"));

        try (Connection connection = Database.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM myshop_materials WHERE material IN (" + params + ")"
            );

            for (int i = 0; i < keys.size(); i++) {
                statement.setString(i + 1, keys.get(i).name());
            }

            ResultSet rs = statement.executeQuery();
            Map<Material, ShopMaterial> map = new HashMap<>(keys.size());

            while (rs.next()) {
                Material material = FormatUtils.formatEnum(Material.class, rs, "material");

                map.put(material, new ShopMaterial(
                        material,
                        rs.getInt("stock"),
                        rs.getBigDecimal("price")
                ));
            }

            return map;
        }
    }
}
