DROP TABLE IF EXISTS sender;

CREATE TABLE sender (
  date       TIMESTAMP DEFAULT now() NOT NULL,
  from_email TEXT                    NOT NULL,
  to_list    TEXT                    NOT NULL,
  cc_list    TEXT,
  subject    TEXT,
  message    TEXT,
  success    BOOLEAN                 NOT NULL,
  cause      TEXT
);

