INSERT INTO menu_items (name, description, price, category) VALUES ('Caesar Salad', 'Chicken, romaine lettuce, parmesan, Caesar dressing', 10.50, 'Salads');
INSERT INTO menu_items (name, description, price, category) VALUES ('Warm Beef Salad', 'Beef, vegetables, teriyaki', 12.90, 'Salads');
INSERT INTO menu_items (name, description, price, category) VALUES ('Greek Salad', 'Vegetables, feta, olives', 8.90, 'Salads');
INSERT INTO menu_items (name, description, price, category) VALUES ('Shrimp Salad', 'Shrimp, salad greens, lime-garlic sauce', 13.50, 'Salads');

INSERT INTO menu_items (name, description, price, category) VALUES ('Ukrainian Borscht', 'Traditional soup with sour cream', 7.50, 'Soups');
INSERT INTO menu_items (name, description, price, category) VALUES ('Tom Yum', 'Spicy soup with shrimp and coconut', 11.90, 'Soups');
INSERT INTO menu_items (name, description, price, category) VALUES ('Mushroom Cream Soup', 'Champignons, cream', 7.90, 'Soups');
INSERT INTO menu_items (name, description, price, category) VALUES ('Chicken Noodle Soup', 'Homemade noodles, chicken', 6.90, 'Soups');

INSERT INTO menu_items (name, description, price, category) VALUES ('Ribeye Steak', 'Beef steak with rosemary', 24.50, 'Main Courses');
INSERT INTO menu_items (name, description, price, category) VALUES ('Grilled Salmon', 'Salmon fillet, lemon', 19.80, 'Main Courses');
INSERT INTO menu_items (name, description, price, category) VALUES ('Chicken in Cream Sauce', 'Chicken, mushrooms, cream', 14.50, 'Main Courses');
INSERT INTO menu_items (name, description, price, category) VALUES ('Pasta Carbonara', 'Pasta, bacon, cream, parmesan', 11.90, 'Main Courses');
INSERT INTO menu_items (name, description, price, category) VALUES ('Pasta with Shrimp', 'Fettuccine, shrimp, cream sauce', 14.80, 'Main Courses');
INSERT INTO menu_items (name, description, price, category) VALUES ('Mushroom Risotto', 'Arborio rice, mushrooms, cream', 12.50, 'Main Courses');
INSERT INTO menu_items (name, description, price, category) VALUES ('Chicken Kiev', 'Chicken with butter inside', 13.20, 'Main Courses');

INSERT INTO menu_items (name, description, price, category) VALUES ('Salmon Bruschetta', 'Bread, salmon, cream cheese', 8.20, 'Appetizers');
INSERT INTO menu_items (name, description, price, category) VALUES ('Beef Carpaccio', 'Thinly sliced beef, parmesan', 12.90, 'Appetizers');
INSERT INTO menu_items (name, description, price, category) VALUES ('Cheese Platter', 'Assorted European cheeses', 14.40, 'Appetizers');
INSERT INTO menu_items (name, description, price, category) VALUES ('Hummus with Pita', 'Homemade hummus, warm pita', 7.20, 'Appetizers');
INSERT INTO menu_items (name, description, price, category) VALUES ('Tuna Tartare', 'Tuna, lime, sesame', 13.80, 'Appetizers');

INSERT INTO menu_items (name, description, price, category) VALUES ('Homemade Lemonade', 'Lemon, mint, syrup', 3.90, 'Drinks');
INSERT INTO menu_items (name, description, price, category) VALUES ('Berry Drink', 'Wild berry fruit drink', 3.50, 'Drinks');
INSERT INTO menu_items (name, description, price, category) VALUES ('Black/Green Tea', 'Natural loose-leaf tea', 2.90, 'Drinks');
INSERT INTO menu_items (name, description, price, category) VALUES ('Espresso', 'Italian coffee', 2.50, 'Drinks');
INSERT INTO menu_items (name, description, price, category) VALUES ('Cappuccino', 'Coffee with milk', 3.20, 'Drinks');
INSERT INTO menu_items (name, description, price, category) VALUES ('Fresh Orange Juice', 'Freshly squeezed juice', 4.90, 'Drinks');

INSERT INTO menu_items (name, description, price, category) VALUES ('Tiramisu', 'Coffee dessert with mascarpone', 5.80, 'Desserts');
INSERT INTO menu_items (name, description, price, category) VALUES ('New York Cheesecake', 'Classic cheesecake', 5.50, 'Desserts');
INSERT INTO menu_items (name, description, price, category) VALUES ('Vanilla Éclair', 'Choux pastry, cream', 3.80, 'Desserts');
INSERT INTO menu_items (name, description, price, category) VALUES ('Napoleon Cake', 'Layered cake with cream', 4.80, 'Desserts');
INSERT INTO menu_items (name, description, price, category) VALUES ('Panna Cotta', 'Creamy Italian dessert', 5.20, 'Desserts');

INSERT INTO users (email, first_name, last_name, password, role) VALUES ('admin@example.com', 'Админ', 'Системный', '$2a$12$3Gg14f0D5fzelZY1GS.FeebNl/QDCuYLFCZMAtz7cZFAL9Z8CHJV6', 'ADMIN');

INSERT INTO users (email, first_name, last_name, password, role) VALUES ('tom@gmail.com', 'Том', 'Жуков', '$2a$12$Jkkl81yQoOtt7wVnqtp99eK3qutmAlgkzJ3cS2mPAzHkiPWvueGQa', 'CLIENT');

INSERT INTO users (email, first_name, last_name, password, role) VALUES ('nata.udinslaya18@gmail.com', 'Nata', 'Udynska', '$2a$12$3Gg14f0D5fzelZY1GS.FeebNl/QDCuYLFCZMAtz7cZFAL9Z8CHJV6', 'CLIENT');



