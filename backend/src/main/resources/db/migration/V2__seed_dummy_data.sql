-- Safe to run once (Flyway). Inserts are idempotent via ON CONFLICT.

-- Employees
insert into employee(employee_code, full_name, email, phone, department, active)
values
  ('EMP-001', 'Aarav Sharma', 'aarav.sharma@yukstra.com', '+91-90000-00001', 'HR', true),
  ('EMP-002', 'Diya Patel', 'diya.patel@yukstra.com', '+91-90000-00002', 'Inventory', true),
  ('EMP-003', 'Kabir Singh', 'kabir.singh@yukstra.com', '+91-90000-00003', 'Purchase', true),
  ('EMP-004', 'Isha Verma', 'isha.verma@yukstra.com', '+91-90000-00004', 'Reporting', true)
on conflict (employee_code) do nothing;

-- Items
insert into item(sku, name, unit, unit_price)
values
  ('SKU-LAP-001', 'Laptop - 14 inch', 'pcs', 65000.00),
  ('SKU-MON-001', 'Monitor - 24 inch', 'pcs', 12000.00),
  ('SKU-MSE-001', 'Mouse - Wireless', 'pcs', 850.00),
  ('SKU-KBD-001', 'Keyboard - Mechanical', 'pcs', 3200.00)
on conflict (sku) do nothing;

-- Suppliers
insert into supplier(name, email, phone, address)
values
  ('Acme Supplies Pvt Ltd', 'sales@acme.example', '+91-80000-00001', 'Mumbai, India'),
  ('Northwind Traders', 'orders@northwind.example', '+91-80000-00002', 'Pune, India'),
  ('Contoso Components', 'hello@contoso.example', '+91-80000-00003', 'Bengaluru, India')
on conflict do nothing;

-- Purchase Orders (use subselects to map supplier ids)
insert into purchase_order(po_number, supplier_id, status, ordered_at)
values
  (
    'PO-2026-0001',
    (select id from supplier where name = 'Acme Supplies Pvt Ltd' order by id asc limit 1),
    'APPROVED',
    now() - interval '7 days'
  ),
  (
    'PO-2026-0002',
    (select id from supplier where name = 'Northwind Traders' order by id asc limit 1),
    'SUBMITTED',
    now() - interval '2 days'
  )
on conflict (po_number) do nothing;

-- Purchase Order Lines
insert into purchase_order_line(purchase_order_id, item_id, qty, unit_price)
values
  (
    (select id from purchase_order where po_number = 'PO-2026-0001'),
    (select id from item where sku = 'SKU-LAP-001'),
    5.00,
    64500.00
  ),
  (
    (select id from purchase_order where po_number = 'PO-2026-0001'),
    (select id from item where sku = 'SKU-MON-001'),
    5.00,
    11800.00
  ),
  (
    (select id from purchase_order where po_number = 'PO-2026-0002'),
    (select id from item where sku = 'SKU-MSE-001'),
    20.00,
    830.00
  ),
  (
    (select id from purchase_order where po_number = 'PO-2026-0002'),
    (select id from item where sku = 'SKU-KBD-001'),
    10.00,
    3100.00
  )
on conflict do nothing;

