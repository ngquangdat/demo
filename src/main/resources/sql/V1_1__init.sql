CREATE TABLE api_sync_config
(
    id                        bigserial NOT NULL,
    api_url                   text      NOT NULL,
    target_table              text      NOT NULL,
    record_json_path          text      NOT NULL,
    page_response_path        text NULL,
    total_page_response_path  text NULL,
    total_count_response_path text NULL,
    last_synced               timestamp NULL,
    sync_code                 text NULL,
    status                    text NULL,
    http_method               text      NOT NULL DEFAULT 'GET'::text,
    clean_condition           text NULL,
    upsert_key                text NULL,
    parallel                  text NULL,
    CONSTRAINT api_sync_config_pkey PRIMARY KEY (id)
);

CREATE TABLE api_sync_field_mapping
(
    id            bigserial NOT NULL,
    config_id     int8      NOT NULL,
    json_path     text NULL,
    target_column text      NOT NULL,
    default_value text NULL,
    operation     text NULL,
    CONSTRAINT api_sync_field_mapping_pkey PRIMARY KEY (id)
);

CREATE TABLE api_sync_param_config
(
    id            bigserial NOT NULL,
    config_id     int8      NOT NULL,
    param_name    text      NOT NULL,
    param_type    text NULL,
    default_value text NULL,
    operation     text NULL,
    CONSTRAINT api_sync_param_config_pkey PRIMARY KEY (id)
);