-- Insert USER role if it doesn't exist
INSERT INTO roles (name, description)
SELECT 'USER', 'Basic user role with standard permissions'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'USER');

-- Insert ADMIN role if it doesn't exist
INSERT INTO roles (name, description)
SELECT 'ADMIN', 'Administrator role with full system access'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');
