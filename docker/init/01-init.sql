-- App database (chat history, saved connections, embeddings)
CREATE ROLE copilot_app LOGIN PASSWORD 'copilot_app';
CREATE DATABASE copilot OWNER copilot_app;
\c copilot
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Demo database the copilot answers questions about (read-only role)
\c postgres
CREATE ROLE copilot_ro LOGIN PASSWORD 'copilot_ro';
ALTER ROLE copilot_ro SET default_transaction_read_only = on;
ALTER ROLE copilot_ro SET statement_timeout = '10s';
CREATE DATABASE sales_dw;
GRANT CONNECT ON DATABASE sales_dw TO copilot_ro;
\c sales_dw

CREATE TABLE regions   (id serial PRIMARY KEY, name text NOT NULL);
CREATE TABLE customers (id serial PRIMARY KEY, name text, email text, region_id int REFERENCES regions, created_at timestamptz);
CREATE TABLE products  (id serial PRIMARY KEY, name text, category text, price numeric(10,2));
CREATE TABLE orders    (id serial PRIMARY KEY, customer_id int REFERENCES customers, region_id int REFERENCES regions,
                        total numeric(12,2), status text, created_at timestamptz);
CREATE TABLE order_items (id serial PRIMARY KEY, order_id int REFERENCES orders, product_id int REFERENCES products, quantity int);

INSERT INTO regions(name) VALUES ('North'),('West'),('South'),('East'),('Central');
INSERT INTO customers(name,email,region_id,created_at)
  SELECT 'Customer '||g, 'customer'||g||'@example.com', 1+(g%5), now()-(g||' days')::interval FROM generate_series(1,200) g;
INSERT INTO products(name,category,price)
  SELECT 'Product '||g, (ARRAY['Electronics','Home','Sports','Books'])[1+g%4], 10+g*7 FROM generate_series(1,20) g;
INSERT INTO orders(customer_id,region_id,total,status,created_at)
  SELECT c.id, c.region_id, 20+(g*37)%480, (ARRAY['placed','shipped','shipped','refunded'])[1+g%4], now()-((g%270)||' days')::interval
  FROM generate_series(1,2000) g JOIN customers c ON c.id = 1+(g%200);
INSERT INTO order_items(order_id,product_id,quantity) SELECT id, 1+(id%20), 1+(id%3) FROM orders;

GRANT USAGE ON SCHEMA public TO copilot_ro;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO copilot_ro;
