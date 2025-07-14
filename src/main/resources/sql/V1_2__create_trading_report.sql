CREATE TABLE trading_report
(
    id             BIGSERIAL PRIMARY KEY,
    main_volume    text,
    main_value     text,
    big_lot_volume text,
    big_lot_value  text,
    buying_volume  text,
    buying_order   text,
    selling_volume text,
    selling_order  text,
    total_volume   text,
    total_value    text,
    record_date    timestamp
);

CREATE TABLE trading_report_odd
(
    id             BIGSERIAL PRIMARY KEY,
    main_volume    text,
    main_value     text,
    big_lot_volume text,
    big_lot_value  text,
    buying_volume  text,
    buying_order   text,
    selling_volume text,
    selling_order  text,
    total_volume   text,
    total_value    text,
    record_date    timestamp
);
