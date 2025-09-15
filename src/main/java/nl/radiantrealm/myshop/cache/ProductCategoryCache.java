package nl.radiantrealm.myshop.cache;

import nl.radiantrealm.library.cache.CacheRegistry;
import nl.radiantrealm.myshop.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ProductCategoryCache extends CacheRegistry<String, List<String>> {

    public ProductCategoryCache() {
        super(Duration.ofMinutes(15));
    }

    @Override
    protected List<String> load(String string) throws Exception {
        try (Connection connection = Database.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM myshop_categories WHERE category_key = ?"
            );

            statement.setString(1, string);
            ResultSet rs = statement.executeQuery();
            List<String> list = new ArrayList<>();

            while (rs.next()) {
                list.add(rs.getString("product_key"));
            }

            return list;
        }
    }
}
