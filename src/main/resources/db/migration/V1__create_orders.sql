CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        status VARCHAR(30) NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP NOT NULL
);