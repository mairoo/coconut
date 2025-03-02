-- member_loginlog 테이블 수정
ALTER TABLE "public"."member_loginlog"
    ADD COLUMN "email" varchar(32) NULL,
    ADD COLUMN "username" varchar(32) NULL,
    ADD COLUMN "user_agent" varchar(255) NULL,
    ADD COLUMN "is_successful" boolean NOT NULL DEFAULT FALSE,
    ADD COLUMN "reason" varchar(128) NULL;

-- shop_cart 테이블 추가
CREATE TABLE shop_cart (
    id BIGSERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL UNIQUE,
    cart_data TEXT NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

ALTER TABLE shop_cart ADD CONSTRAINT fk_shop_cart_user_id
    FOREIGN KEY (user_id) REFERENCES auth_user(id);

CREATE INDEX idx_shop_cart_user_id ON shop_cart(user_id);