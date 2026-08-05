INSERT IGNORE INTO schemes
(scheme_code, scheme_name, scheme_type, risk_level, launch_date,
 min_investment, expense_ratio, status, created_at)
VALUES
('HDFC-MID-001', 'HDFC Mid-Cap Opportunities Fund', 'EQUITY', 'VERY_HIGH',
 '2014-01-01', 500.00, 1.45, 'ACTIVE', NOW()),

('SBI-BLUE-001', 'SBI Bluechip Fund', 'EQUITY', 'HIGH',
 '2006-02-14', 500.00, 0.92, 'ACTIVE', NOW()),

('ICICI-LIQ-001', 'ICICI Prudential Liquid Fund', 'DEBT', 'LOW',
 '2005-10-11', 500.00, 0.32, 'ACTIVE', NOW()),

('KOTAK-HYB-001', 'Kotak Equity Hybrid Fund', 'HYBRID', 'HIGH',
 '2013-07-25', 500.00, 1.18, 'ACTIVE', NOW());