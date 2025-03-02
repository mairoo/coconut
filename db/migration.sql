ALTER TABLE "public"."member_loginlog"
    ADD COLUMN "email" varchar(32) NULL,
    ADD COLUMN "username" varchar(32) NULL,
    ADD COLUMN "user_agent" varchar(255) NULL,
    ADD COLUMN "is_successful" boolean NOT NULL DEFAULT FALSE,
    ADD COLUMN "reason" varchar(128) NULL;