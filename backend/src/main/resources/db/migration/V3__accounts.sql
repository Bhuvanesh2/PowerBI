create table if not exists account (
  id bigserial primary key,
  code varchar(40) not null unique,
  name varchar(200) not null,
  type varchar(40) not null,
  active boolean not null default true,
  created_at timestamptz not null default now()
);

insert into account(code, name, type, active) values
  ('1000', 'Cash', 'ASSET', true),
  ('1100', 'Bank', 'ASSET', true),
  ('1200', 'Accounts Receivable', 'ASSET', true),
  ('2000', 'Accounts Payable', 'LIABILITY', true),
  ('3000', 'Owner Equity', 'EQUITY', true),
  ('4000', 'Sales Revenue', 'INCOME', true),
  ('5000', 'Cost of Goods Sold', 'EXPENSE', true)
on conflict (code) do nothing;

