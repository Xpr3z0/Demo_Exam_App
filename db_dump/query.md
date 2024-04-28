-- Создание таблицы requests
CREATE TABLE requests (
id SERIAL PRIMARY KEY,
equip_num VARCHAR(255),
equip_type VARCHAR(255),
problem_desc VARCHAR(255),
request_comments TEXT, -- Оставляем как TEXT
status VARCHAR(50)
);

-- Создание таблицы request_regs с внешним ключом к requests (request_id)
CREATE TABLE request_regs (
request_id INT PRIMARY KEY REFERENCES requests(id),
client_name VARCHAR(100),
client_phone VARCHAR(20), -- Предполагаем максимальную длину номера телефона
date_start DATE
);

-- Создание таблицы request_processes с внешним ключом к requests (request_id)
CREATE TABLE request_processes (
request_id INT PRIMARY KEY REFERENCES requests(id),
priority VARCHAR(50),
date_finish_plan DATE
);

-- Создание таблицы members
CREATE TABLE members (
id SERIAL PRIMARY KEY,
name VARCHAR(100),
login VARCHAR(50),
pass VARCHAR(50),
role VARCHAR(50)
);

-- Создание таблицы assignments с внешними ключами к request_processes (id_request) и members (member_id)
CREATE TABLE assignments (
id SERIAL PRIMARY KEY,
id_request INT,
member_id INT,
is_responsible BOOLEAN,
-- Создаем уникальное ограничение для комбинации (id_request, is_responsible)
CONSTRAINT unique_assignment_request_responsible UNIQUE (id_request, is_responsible),
-- Внешние ключи
CONSTRAINT fk_assignment_request_id FOREIGN KEY (id_request) REFERENCES request_processes(request_id),
CONSTRAINT fk_assignment_member_id FOREIGN KEY (member_id) REFERENCES members(id)
);


-- Создание таблицы reports с внешним ключом к request_processes (request_id)
CREATE TABLE reports (
request_id INT PRIMARY KEY REFERENCES request_processes(request_id),
repair_type VARCHAR(100),
time INT,
cost NUMERIC,
resources TEXT,
reason TEXT, -- Оставляем как TEXT
help TEXT -- Оставляем как TEXT
);

-- Создание таблицы orders с внешним ключом к request_processes (request_id)
CREATE TABLE orders (
id SERIAL PRIMARY KEY,
request_id INT REFERENCES request_processes(request_id),
resource_type VARCHAR(100),
resource_name VARCHAR(255),
cost NUMERIC
);



-- Добавление 8 пользователей с разными ролями
INSERT INTO members (name, login, pass, role)
VALUES
('Иванов Иван Иванович', 'ivan', '123', 'manager'),
('Петров Петр Петрович', 'petr', '123', 'manager'),
('Сидорова Анна Александровна', 'anna', '123', 'operator'),
('Козлов Дмитрий Сергеевич', 'dmitry', '123', 'repairer'),
('Смирнова Елена Павловна', 'elena', '123', 'repairer'),
('Николаев Алексей Викторович', 'alexei', '123', 'repairer'),
('Кузнецов Сергей Дмитриевич', 'sergei', '123', 'repairer'),
('Иванова Мария Владимировна', 'maria', '123', 'repairer');



-- Добавление 10 заявок (будут отображаться среди новых у менеджера)
INSERT INTO requests (equip_num, equip_type, problem_desc, status)
VALUES
('SN123456789', 'Принтер', 'Принтер не печатает, возможно, проблема с картриджем.', 'Новая'),
('SN987654321', 'Ноутбук', 'Ноутбук не загружается, экран черный.', 'Новая'),
('SN777777777', 'Монитор', 'Монитор мерцает, цвета искажены.', 'Новая'),
('SN444444444', 'Смартфон', 'Смартфон не заряжается, возможно, неисправен разъем.', 'Новая'),
('SN555555555', 'Планшет', 'Планшет тормозит, приложения открываются медленно.', 'Новая'),
('SN222333444', 'Маршрутизатор', 'Нет доступа к Интернету через маршрутизатор.', 'Новая'),
('SN666666666', 'Компьютер', 'Компьютер выключается через несколько минут работы.', 'Новая'),
('SN888888888', 'МФУ (многофункциональное устройство)', 'МФУ не сканирует документы.', 'Новая'),
('SN999999999', 'Кондиционер', 'Кондиционер не охлаждает воздух.', 'Новая'),
('SN111122223333', 'Фотоаппарат', 'Фотоаппарат не включается после зарядки.', 'Новая');

INSERT INTO request_regs (request_id, client_name, client_phone, date_start)
VALUES
(1, 'Иванов Александр Сергеевич', '+7 (987) 654-32-10', '2024-04-25'),
(2, 'Петрова Елена Ивановна', '+7 (926) 123-45-67', '2024-04-25'),
(3, 'Смирнов Дмитрий Алексеевич', '+7 (925) 555-55-55', '2024-04-25'),
(4, 'Козлова Мария Викторовна', '+7 (903) 111-22-33', '2024-04-25'),
(5, 'Николаев Артем Владимирович', '+7 (999) 777-88-99', '2024-04-25'),
(6, 'Иванова Ольга Петровна', '+7 (910) 222-33-44', '2024-04-25'),
(7, 'Соколов Игорь Александрович', '+7 (915) 444-55-66', '2024-04-25'),
(8, 'Медведева Анастасия Сергеевна', '+7 (926) 777-88-99', '2024-04-25'),
(9, 'Васильев Иван Павлович', '+7 (987) 123-45-67', '2024-04-25'),
(10, 'Сидорова Екатерина Дмитриевна', '+7 (910) 333-44-55', '2024-04-25');
