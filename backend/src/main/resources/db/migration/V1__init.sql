create table if not exists app_user (
  id bigserial primary key,
  username varchar(80) not null unique,
  password_hash varchar(255) not null,
  enabled boolean not null default true,
  created_at timestamptz not null default now()
);

create table if not exists app_role (
  id bigserial primary key,
  name varchar(80) not null unique
);

create table if not exists app_user_role (
  user_id bigint not null references app_user(id) on delete cascade,
  role_id bigint not null references app_role(id) on delete cascade,
  primary key (user_id, role_id)
);

create table if not exists employee (
  id bigserial primary key,
  employee_code varchar(40) not null unique,
  full_name varchar(160) not null,
  email varchar(160),
  phone varchar(40),
  department varchar(120),
  active boolean not null default true,
  created_at timestamptz not null default now()
);

create table if not exists item (
  id bigserial primary key,
  sku varchar(64) not null unique,
  name varchar(180) not null,
  unit varchar(24) not null default 'pcs',
  unit_price numeric(12,2) not null default 0,
  created_at timestamptz not null default now()
);

create table if not exists supplier (
  id bigserial primary key,
  name varchar(200) not null,
  email varchar(160),
  phone varchar(40),
  address text,
  created_at timestamptz not null default now()
);

create table if not exists purchase_order (
  id bigserial primary key,
  po_number varchar(40) not null unique,
  supplier_id bigint not null references supplier(id),
  status varchar(40) not null default 'DRAFT',
  ordered_at timestamptz,
  created_at timestamptz not null default now()
);

create table if not exists purchase_order_line (
  id bigserial primary key,
  purchase_order_id bigint not null references purchase_order(id) on delete cascade,
  item_id bigint not null references item(id),
  qty numeric(12,2) not null,
  unit_price numeric(12,2) not null default 0
);

insert into app_role(name) values ('ROLE_ADMIN') on conflict do nothing;
insert into app_role(name) values ('ROLE_USER') on conflict do nothing;

