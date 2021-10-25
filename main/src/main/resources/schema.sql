CREATE TABLE IF NOT EXISTS inventory (
  sku  VARCHAR(80)    NOT NULL,
  price DECIMAL(10, 2) NOT NULL,
  qty   INTEGER        NOT NULL,

  PRIMARY KEY (sku)
);

CREATE TABLE IF NOT EXISTS accounts (
  id      INTEGER     NOT NULL,
  api_key VARCHAR(80) NOT NULL,
  name    VARCHAR(80) NOT NULL,

  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS orders (
  id         INTEGER        NOT NULL,
  account_id INTEGER        NOT NULL,
  status     VARCHAR(16)    NOT NULL,
  total      DECIMAL(10, 2) NOT NULL,

  PRIMARY KEY (id),
  FOREIGN KEY (account_id) REFERENCES accounts (id)
);

CREATE TABLE IF NOT EXISTS order_items (
  id        INTEGER        NOT NULL,
  order_id  INTEGER        NOT NULL,
  sku       VARCHAR(80)    NOT NULL,
  price     DECIMAL(10, 2) NOT NULL,
  qty       INTEGER        NOT NULL,
  ext_price DECIMAL(10, 2) NOT NULL,

  PRIMARY KEY (id),
  FOREIGN KEY (order_id) REFERENCES orders (id)
);
