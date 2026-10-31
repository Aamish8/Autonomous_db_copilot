package com.aamish.db_copilot.service;

import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class SchemaReader {
   private final ConnectionManager manager;
   private static final Map<String,String>meanings=Map.of(
		   "regions", "sales regions such as North and West. ",
		   "customers", "people who buy from us. region_id references regions.id. ",
		   "orders", "customer purchases. Revenue means SUM(total), so questions about revenue per region or customer need SUM(total) with GROUP BY. "
		   		+ "customer_id references customers.id, region_id references regions.id. ",
		   		"order_items", "products inside each order. quantity is the number of units sold, so 'quantity sold' means SUM(quantity) with no price involved. order_id references orders.id, product_id references products.id. ",
		   		"products", "items we sell. price is the unit price, so 'most expensive' means the highest price in this table. "		   
		   );
   public List<String>listTables(Long id){
	   String sql = """
               SELECT table_name
               FROM information_schema.tables
               WHERE table_schema = 'public'
               """;
	      JdbcClient client=manager.clientFor(id);
	   return client.sql(sql).query(String.class).list();
   }
   public List<Map<String, @Nullable Object>> listColumns(Long id,String table){
	   String sql="""
	   		SELECT column_name,data_type
	   		FROM information_schema.columns
	   		WHERE table_schema='public' AND table_name=?
	   		""";
	      JdbcClient client=manager.clientFor(id);
	   return client.sql(sql).param(table).query().listOfRows();
   }
   public String describeTable(Long id,String table) {
	   String columns=listColumns(id,table).stream()
			   .map(row->row.get("column_name")+ "(" +row.get("data_type")+ ")")
			   .collect(Collectors.joining(", "));
	   String meaning=meanings.getOrDefault(table,"");
	   return "Table "+table+": "+meaning+columns;
   }
}
