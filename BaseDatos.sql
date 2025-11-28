-- DATABASE: customers
CREATE TABLE customer (
    identification VARCHAR(20) PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    gender         VARCHAR(10),
    address        VARCHAR(200),
    phone          VARCHAR(20),
    password       VARCHAR(100) NOT NULL,
    state          BOOLEAN      NOT NULL
);

-- DATABASE: accounts
CREATE TABLE account (
    id              BIGSERIAL PRIMARY KEY,
    number          VARCHAR(20) NOT NULL UNIQUE,
    type            VARCHAR(20) NOT NULL,
    initial_balance NUMERIC(18,2) NOT NULL,
    state           BOOLEAN      NOT NULL,
    customer_id     VARCHAR(20)  NOT NULL
);

CREATE TABLE movement (
    id         BIGSERIAL PRIMARY KEY,
    date       TIMESTAMP     NOT NULL,
    type       VARCHAR(20)   NOT NULL,
    amount     NUMERIC(18,2) NOT NULL,
    balance    NUMERIC(18,2) NOT NULL,
    account_id BIGINT        NOT NULL REFERENCES account(id)
);
