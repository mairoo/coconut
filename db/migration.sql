ALTER TABLE "public"."member_loginlog"
    ADD COLUMN "email" varchar(32) NULL,
    ADD COLUMN "username" varchar(32) NULL,
    ADD COLUMN "user_agent" varchar(255) NULL,
    ADD COLUMN "is_successful" boolean NOT NULL DEFAULT FALSE,
    ADD COLUMN "reason" varchar(128) NULL;

CREATE TABLE shop_cart (
                           id SERIAL PRIMARY KEY,
                           user_id INTEGER NOT NULL,
                           cart_data TEXT NOT NULL,
                           version BIGINT NOT NULL DEFAULT 0
);

ALTER TABLE shop_cart ADD CONSTRAINT fk_shop_cart_user_id FOREIGN KEY (user_id) REFERENCES auth_user(id);
