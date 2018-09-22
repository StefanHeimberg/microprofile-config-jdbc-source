CREATE TABLE config (
    name VARCHAR(255) NOT NULL,
    value VARCHAR(255) NOT NULL,
    PRIMARY KEY (name)
);

INSERT INTO config (name, value) VALUES
  ('my.config.key.3', 'my-value-3'),
  ('my.config.key.4', 'my-value-4'),
  ('my.config.key.5', 'my-value-5'),
  ('my.config.key.6', 'my-value-6'),
  ('my.config.key.7', 'my-value-7');