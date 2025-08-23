CREATE TABLE IF NOT EXISTS clients (
                                       id SERIAL PRIMARY KEY,
                                       balance DECIMAL(10, 2)
);
CREATE TABLE IF NOT EXISTS operations (
                            id BIGSERIAL PRIMARY KEY,
                            from_id INT NULL,
                            to_id INT NULL,
                            money_sum DOUBLE PRECISION NOT NULL,
                            status VARCHAR(50) NOT NULL,
                            operation_datetime TIMESTAMP NOT NULL
);